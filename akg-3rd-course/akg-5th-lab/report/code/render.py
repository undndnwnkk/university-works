import time

import numpy as np

from scene import color, normalize

RENDER_DEFAULTS = {
    "spp": 4,
    "max_depth": 5,
    "backend": "embree",
    "mode": "split",
    "seed": 1,
    "background": [0.0, 0.0, 0.0],
    "normalize": "mean",
    "norm_value": 1.0,
    "gamma": 2.2,
    "min_weight": 1e-4,
    "output": "output/image",
}

LUMA = np.array([0.2126, 0.7152, 0.0722])


def settings_of(config):
    return dict(RENDER_DEFAULTS, **config.get("render", {}))


def dot(a, b):
    return np.einsum("ij,ij->i", a, b)


def accumulate(result, idx, values):
    n = len(result)
    for k in range(3):
        result[:, k] += np.bincount(idx, weights=values[:, k], minlength=n)


def camera_basis(cam):
    pos = np.asarray(cam["position"], dtype=np.float64)
    forward = normalize(np.asarray(cam["look_at"], dtype=np.float64) - pos)
    right = normalize(np.cross(forward, cam.get("up", [0, 1, 0])))
    up = np.cross(right, forward)
    return pos, forward, right, up


def primary_rays(cam, px, py):
    width, height = cam["width"], cam["height"]
    pos, forward, right, up = camera_basis(cam)
    tan = np.tan(np.radians(cam["fov"]) / 2.0)
    x = (2.0 * px / width - 1.0) * tan * width / height
    y = (1.0 - 2.0 * py / height) * tan
    dirs = normalize(forward + x[:, None] * right + y[:, None] * up)
    return np.broadcast_to(pos, dirs.shape).copy(), dirs


def project(cam, points):
    width, height = cam["width"], cam["height"]
    pos, forward, right, up = camera_basis(cam)
    tan = np.tan(np.radians(cam["fov"]) / 2.0)
    rel = np.asarray(points, dtype=np.float64) - pos
    z = rel @ forward
    x = (rel @ right) / z / (tan * width / height)
    y = (rel @ up) / z / tan
    return np.stack([(x + 1.0) * width / 2.0, (1.0 - y) * height / 2.0], axis=-1)


def pixel_offsets(spp, index, count, rng):
    if spp == 1:
        return np.full(count, 0.5), np.full(count, 0.5)
    k = int(round(np.sqrt(spp)))
    if k * k == spp:
        return (index % k + rng.random(count)) / k, (index // k + rng.random(count)) / k
    return rng.random(count), rng.random(count)


def surface(scene, orig, dirs, t, tid, u, v):
    points = orig + t[:, None] * dirs
    ng = scene.face_normals[tid]
    vn = scene.vertex_normals[tid]
    w = 1.0 - u - v
    ns = normalize(w[:, None] * vn[:, 0] + u[:, None] * vn[:, 1] + v[:, None] * vn[:, 2])
    side = np.where(dot(dirs, ng) < 0.0, 1.0, -1.0)
    ngf = ng * side[:, None]
    nsf = ns * side[:, None]
    bad = dot(nsf, ngf) <= 0.0
    nsf[bad] = ngf[bad]
    return points, ngf, nsf, scene.tri_material[tid], side > 0.0


def light_sample(light, points):
    if light["type"] == "point":
        vec = light["position"] - points
        dist = np.linalg.norm(vec, axis=1)
        to_light = vec / dist[:, None]
        intensity = np.broadcast_to(light["intensity"], points.shape)
        if light["lambert"]:
            intensity = intensity * np.clip(-to_light @ light["axis"], 0.0, None)[:, None]
        return to_light, dist, intensity / (dist ** 2)[:, None]
    to_light = np.broadcast_to(-light["direction"], points.shape)
    return to_light, np.full(len(points), np.inf), np.broadcast_to(light["irradiance"], points.shape)


def brdf_specular(scene, mat, normal, to_light, view):
    ks = scene.ks[mat]
    n = scene.shininess[mat]
    blinn = scene.blinn[mat]
    half = normalize(to_light + view)
    cos_h = np.clip(dot(normal, half), 0.0, 1.0)
    mirror = 2.0 * dot(normal, to_light)[:, None] * normal - to_light
    cos_r = np.clip(dot(mirror, view), 0.0, 1.0)
    blinn_value = (n + 8.0) / (8.0 * np.pi) * cos_h ** n
    phong_value = (n + 2.0) / (2.0 * np.pi) * cos_r ** n
    return ks * np.where(blinn, blinn_value, phong_value)[:, None]


def direct_light(scene, backend, points, ngf, nsf, view, mat, stats):
    radiance = np.zeros_like(points)
    kd = scene.kd[mat]
    kdt = scene.kdt[mat]
    reflective = (kd + scene.ks[mat]).max(axis=1) > 0.0
    transmissive = kdt.max(axis=1) > 0.0
    for light in scene.lights:
        to_light, dist, irradiance = light_sample(light, points)
        cos_n = dot(nsf, to_light)
        front = dot(ngf, to_light) > 0.0
        active = (irradiance.max(axis=1) > 0.0) & np.where(front, reflective, transmissive)
        if not active.any():
            continue
        idx = np.nonzero(active)[0]
        offset = np.where(front[idx], scene.eps, -scene.eps)[:, None] * ngf[idx]
        blocked = backend.occluded(points[idx] + offset, to_light[idx], dist[idx] - 2.0 * scene.eps)
        stats["shadow_rays"] += len(idx)
        idx = idx[~blocked]
        f, c = front[idx], cos_n[idx]
        reflected = (kd[idx] / np.pi + brdf_specular(scene, mat[idx], nsf[idx], to_light[idx], view[idx])) * np.clip(c, 0.0, None)[:, None]
        transmitted = kdt[idx] / np.pi * np.clip(-c, 0.0, None)[:, None]
        radiance[idx] += np.where(f[:, None], reflected, transmitted) * irradiance[idx]
    return radiance


def refract(dirs, normal, eta):
    cos_i = -dot(dirs, normal)
    k = 1.0 - eta ** 2 * (1.0 - cos_i ** 2)
    tir = k < 0.0
    out = eta[:, None] * dirs + (eta * cos_i - np.sqrt(np.clip(k, 0.0, None)))[:, None] * normal
    return normalize(out), tir


def reflect(dirs, normal):
    return dirs - 2.0 * dot(dirs, normal)[:, None] * normal


def trace(scene, backend, orig, dirs, settings, rng, stats):
    result = np.zeros_like(orig)
    idx = np.arange(len(orig))
    weight = np.ones_like(orig)
    background = color(settings["background"])
    for depth in range(settings["max_depth"] + 1):
        if len(idx) == 0:
            break
        t, tid, u, v = backend.intersect(orig, dirs)
        stats["rays"] += len(idx)
        miss = tid < 0
        accumulate(result, idx[miss], weight[miss] * background)
        hit = ~miss
        orig, dirs, t, tid, u, v, idx, weight = orig[hit], dirs[hit], t[hit], tid[hit], u[hit], v[hit], idx[hit], weight[hit]
        points, ngf, nsf, mat, entering = surface(scene, orig, dirs, t, tid, u, v)
        local = direct_light(scene, backend, points, ngf, nsf, -dirs, mat, stats)
        accumulate(result, idx, weight * local)
        if depth == settings["max_depth"]:
            break
        eta = np.where(entering, 1.0 / scene.ior[mat], scene.ior[mat])
        refr_dirs, tir = refract(dirs, nsf, eta)
        kt = np.where(tir[:, None], 0.0, scene.kt[mat])
        kr = scene.kr[mat] + np.where(tir[:, None], scene.kt[mat], 0.0)
        refl_dirs = reflect(dirs, nsf)
        if settings["mode"] == "path":
            pr = kr.mean(axis=1)
            pt = kt.mean(axis=1)
            total = np.maximum(pr + pt, 1.0)
            pr, pt = pr / total, pt / total
            x = rng.random(len(idx))
            take_r = x < pr
            take_t = (x >= pr) & (x < pr + pt)
            wr = kr / np.where(pr > 0.0, pr, 1.0)[:, None]
            wt = kt / np.where(pt > 0.0, pt, 1.0)[:, None]
        else:
            take_r = kr.max(axis=1) > 0.0
            take_t = kt.max(axis=1) > 0.0
            wr, wt = kr, kt
        new_orig = [points[take_r] + scene.eps * ngf[take_r], points[take_t] - scene.eps * ngf[take_t]]
        new_dirs = [refl_dirs[take_r], refr_dirs[take_t]]
        new_weight = [weight[take_r] * wr[take_r], weight[take_t] * wt[take_t]]
        new_idx = [idx[take_r], idx[take_t]]
        orig, dirs = np.concatenate(new_orig), np.concatenate(new_dirs)
        weight, idx = np.concatenate(new_weight), np.concatenate(new_idx)
        alive = weight.max(axis=1) > settings["min_weight"]
        orig, dirs, weight, idx = orig[alive], dirs[alive], weight[alive], idx[alive]
    return result


def render(scene, backend, settings, progress=None):
    cam = scene.config["camera"]
    width, height = cam["width"], cam["height"]
    rng = np.random.default_rng(settings["seed"])
    py, px = np.divmod(np.arange(width * height), width)
    image = np.zeros((width * height, 3))
    stats = {"rays": 0, "shadow_rays": 0}
    start = time.perf_counter()
    for s in range(settings["spp"]):
        sx, sy = pixel_offsets(settings["spp"], s, width * height, rng)
        orig, dirs = primary_rays(cam, px + sx, py + sy)
        image += trace(scene, backend, orig, dirs, settings, rng, stats)
        if progress:
            progress(s + 1, settings["spp"], time.perf_counter() - start)
    stats["time"] = time.perf_counter() - start
    return image.reshape(height, width, 3) / settings["spp"], stats


def radiance_at(scene, backend, settings, px, py):
    orig, dirs = primary_rays(scene.config["camera"], np.asarray(px, dtype=np.float64), np.asarray(py, dtype=np.float64))
    stats = {"rays": 0, "shadow_rays": 0}
    return trace(scene, backend, orig, dirs, dict(settings, mode="split"), np.random.default_rng(0), stats)


def luminance(image):
    return image @ LUMA


def normalization(image, mode, value):
    if mode == "max":
        return float(image.max())
    if mode == "mean":
        return float(luminance(image).mean() / 0.5)
    return float(value)


def tone_map(image, mode="mean", value=1.0, gamma=2.2):
    norm = normalization(image, mode, value)
    relative = np.clip(image / norm, 0.0, 1.0)
    return np.round(relative ** (1.0 / gamma) * 255.0).astype(np.uint8), norm


def save_ppm(path, image8):
    height, width = image8.shape[:2]
    with open(path, "wb") as f:
        f.write(b"P6\n%d %d\n255\n" % (width, height))
        f.write(np.ascontiguousarray(image8).tobytes())


def save_png(path, image8):
    try:
        from PIL import Image
    except ImportError:
        return False
    Image.fromarray(image8).save(path)
    return True
