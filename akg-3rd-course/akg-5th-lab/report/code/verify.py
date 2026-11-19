import copy
import os
import sys

import numpy as np
from PIL import Image, ImageDraw

from accel import make_backend
from render import (project, primary_rays, radiance_at, reflect, refract, render, save_png,
                    settings_of, tone_map, luminance)
from scene import Scene, normalize, rotation_matrix

LINES = []


def out(text=""):
    print(text)
    LINES.append(text)


def backends_agree(path, count):
    scene = Scene.load(path)
    rng = np.random.default_rng(7)
    cam = scene.config["camera"]
    o1, d1 = primary_rays(cam, rng.random(count) * cam["width"], rng.random(count) * cam["height"])
    points = scene.triangles.reshape(-1, 3)
    lo, hi = points.min(0), points.max(0)
    o2 = lo + rng.random((count, 3)) * (hi - lo)
    d2 = normalize(rng.normal(size=(count, 3)))
    orig, dirs = np.concatenate([o1, o2]), np.concatenate([d1, d2])
    tmax = rng.random(2 * count) * scene.scale
    res = {}
    for name in ["brute", "bvh", "embree"]:
        b = make_backend(name, scene.triangles)
        res[name] = b.intersect(orig, dirs) + (b.occluded(orig, dirs, tmax),)
    out("%s: %d треугольников, %d лучей" % (os.path.basename(path), len(scene.triangles), 2 * count))
    for name in ["bvh", "embree"]:
        same_id = np.mean(res[name][1] == res["brute"][1]) * 100
        hit = (res[name][1] >= 0) & (res["brute"][1] >= 0)
        dt = np.max(np.abs(res[name][0][hit] - res["brute"][0][hit]))
        same_occ = np.mean(res[name][4] == res["brute"][4]) * 100
        other = res[name][1] != res["brute"][1]
        miss = np.sum(other & ((res[name][1] < 0) != (res["brute"][1] < 0)))
        tie = np.sum(other & np.isclose(res[name][0], res["brute"][0], rtol=0.0, atol=1e-4))
        out("  %-6s совпадение ближайшего треугольника %.3f %%, max|dt| = %.1e, совпадение теней %.3f %%" % (
            name, same_id, dt, same_occ))
        out("         расхождений %d: из них попадание/промах %d, равное t (совпадающие грани) %d" % (
            other.sum(), miss, tie))


def plane_test():
    scene = Scene.load("scenes/test_plane.json")
    backend = make_backend("bvh", scene.triangles)
    settings = settings_of(scene.config)
    kd = scene.kd[0, 0]
    light = scene.config["lights"][0]
    i0 = light["intensity"][0]
    h = light["position"][1]
    out("Точечный ламбертовский источник I0 = %g Вт/ср на высоте h = %g, kd = %g" % (i0, h, kd))
    out("  L = kd/pi * I0*cos(a) * cos(b) / r^2 = kd*I0*h^2 / (pi*r^4)")
    out("  %-10s %-22s %8s %12s %12s %10s" % ("пиксель", "точка", "r", "L аналит.", "L трассир.", "отн. ошибка"))
    pixels = [(250, 250), (250, 150), (100, 250), (400, 400), (120, 120), (480, 300), (250, 90)]
    cam = scene.config["camera"]
    for px, py in pixels:
        o, d = primary_rays(cam, np.array([px + 0.5]), np.array([py + 0.5]))
        t = -o[0, 1] / d[0, 1]
        p = o[0] + t * d[0]
        r = np.linalg.norm(np.array(light["position"]) - p)
        analytic = kd * i0 * h ** 2 / (np.pi * r ** 4)
        traced = radiance_at(scene, backend, settings, [px + 0.5], [py + 0.5])[0, 0]
        out("  (%3d,%3d)  (%6.3f, %6.3f, %6.3f) %8.4f %12.6g %12.6g %10.2e" % (
            px, py, p[0], p[1], p[2], r, analytic, traced, abs(traced - analytic) / analytic))
    config = copy.deepcopy(scene.config)
    config["lights"] = [{"type": "directional", "direction": [1, -2, 0.5], "irradiance": [3, 3, 3]}]
    config["materials"]["gray"] = {"kd": [0.5, 0.5, 0.5], "ks": [0.4, 0.4, 0.4], "shininess": 20, "model": "blinn"}
    scene2 = Scene(config, scene.base_dir)
    backend2 = make_backend("bvh", scene2.triangles)
    l = -normalize(np.array([1.0, -2.0, 0.5]))
    out("Параллельный источник E0 = 3, направление (1,-2,0.5), Ламберт kd=0.5 + Блинн-Фонг ks=0.4, n=20")
    out("  L = (kd/pi + ks*(n+8)/(8pi)*cos^n(theta_h)) * E0 * cos(theta)")
    for px, py in pixels[:4]:
        o, d = primary_rays(cam, np.array([px + 0.5]), np.array([py + 0.5]))
        t = -o[0, 1] / d[0, 1]
        p = o[0] + t * d[0]
        v = -d[0]
        hvec = (l + v) / np.linalg.norm(l + v)
        analytic = (0.5 / np.pi + 0.4 * 28 / (8 * np.pi) * hvec[1] ** 20) * 3 * l[1]
        traced = radiance_at(scene2, backend2, settings, [px + 0.5], [py + 0.5])[0, 0]
        out("  (%3d,%3d) L аналит. = %.6g, L трассир. = %.6g, отн. ошибка %.2e" % (
            px, py, analytic, traced, abs(traced - analytic) / analytic))


def segment_hits_box(p, light, center, half, rot):
    a = (p - center) @ rot
    b = (light - center) @ rot
    d = b - a
    t0, t1 = 0.0, 1.0
    for k in range(3):
        if abs(d[k]) < 1e-15:
            if abs(a[k]) > half[k]:
                return False
            continue
        u = (-half[k] - a[k]) / d[k]
        w = (half[k] - a[k]) / d[k]
        t0 = max(t0, min(u, w))
        t1 = min(t1, max(u, w))
    return t0 <= t1


def convex_hull(points):
    pts = sorted(map(tuple, points))
    def cross(o, a, b):
        return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0])
    lower, upper = [], []
    for p in pts:
        while len(lower) >= 2 and cross(lower[-2], lower[-1], p) <= 0:
            lower.pop()
        lower.append(p)
    for p in reversed(pts):
        while len(upper) >= 2 and cross(upper[-2], upper[-1], p) <= 0:
            upper.pop()
        upper.append(p)
    return np.array(lower[:-1] + upper[:-1])


def shadow_test(samples):
    scene = Scene.load("scenes/test_shadow.json")
    cube = scene.config["objects"][1]
    center = np.array(cube["position"], dtype=np.float64)
    half = np.array(cube["size"], dtype=np.float64) / 2
    rot = rotation_matrix(cube["rotate"])
    light = np.array(scene.config["lights"][0]["position"], dtype=np.float64)
    rng = np.random.default_rng(3)
    pts = np.zeros((samples, 3))
    pts[:, [0, 2]] = rng.uniform(-2.5, 2.5, size=(samples, 2))
    inside = np.all(np.abs((pts - center) @ rot) <= half + 1e-9, axis=1)
    pts = pts[~inside]
    analytic = np.array([segment_hits_box(p, light, center, half, rot) for p in pts])
    out("Тень куба (повёрнут на %g°) от точечного источника %s, %d точек плоскости" % (cube["rotate"][1], light.tolist(), len(pts)))
    for name in ["brute", "bvh", "embree"]:
        backend = make_backend(name, scene.triangles)
        vec = light - pts
        dist = np.linalg.norm(vec, axis=1)
        traced = backend.occluded(pts + np.array([0, scene.eps, 0]), vec / dist[:, None], dist - 2 * scene.eps)
        wrong = np.sum(traced != analytic)
        out("  %-6s точек в тени %d (аналитически %d), несовпадений %d" % (name, traced.sum(), analytic.sum(), wrong))
    corners = np.array([[x, y, z] for x in (-1, 1) for y in (-1, 1) for z in (-1, 1)]) * half
    corners = corners @ rot.T + center
    shadow = light + (corners - light) * (light[1] / (light[1] - corners[:, 1]))[:, None]
    hull = convex_hull(shadow[:, [0, 2]])
    outline = np.stack([hull[:, 0], np.zeros(len(hull)), hull[:, 1]], axis=1)
    backend = make_backend("embree", scene.triangles)
    image, _ = render(scene, backend, settings_of(scene.config))
    image8, _ = tone_map(image, "max", 1.0, 2.2)
    img = Image.fromarray(image8)
    draw = ImageDraw.Draw(img)
    pix = project(scene.config["camera"], outline)
    draw.line([tuple(p) for p in pix] + [tuple(pix[0])], fill=(255, 0, 0), width=2)
    for p in project(scene.config["camera"], shadow):
        draw.ellipse([p[0] - 3, p[1] - 3, p[0] + 3, p[1] + 3], outline=(255, 220, 0), width=2)
    img.save("output/verify_shadow.png")
    out("  контур аналитической тени нанесён на изображение: output/verify_shadow.png")


def optics_test():
    rng = np.random.default_rng(5)
    n = np.tile([[0.0, 1.0, 0.0]], (6, 1))
    angles = np.radians([0, 15, 30, 45, 60, 80])
    d = np.stack([np.sin(angles), -np.cos(angles), np.zeros(6)], axis=1)
    ior = 1.5
    t, tir = refract(d, n, np.full(6, 1 / ior))
    r = reflect(d, n)
    out("Закон Снеллиуса n1*sin(a1) = n2*sin(a2), воздух -> стекло (n = %.2f)" % ior)
    out("  %8s %10s %14s %14s %16s" % ("a1, °", "a2, °", "n1*sin(a1)", "n2*sin(a2)", "угол отраж., °"))
    for k in range(6):
        a2 = np.degrees(np.arccos(-t[k, 1]))
        ar = np.degrees(np.arccos(r[k, 1]))
        out("  %8.1f %10.4f %14.8f %14.8f %16.4f" % (
            np.degrees(angles[k]), a2, np.sin(angles[k]), ior * np.sin(np.radians(a2)), ar))
    crit = np.degrees(np.arcsin(1 / ior))
    test = np.radians([crit - 0.5, crit + 0.5])
    dd = np.stack([np.sin(test), np.cos(test), np.zeros(2)], axis=1)
    _, tir2 = refract(dd, -np.tile([[0.0, 1.0, 0.0]], (2, 1)), np.full(2, ior))
    out("  стекло -> воздух: критический угол %.3f°, при %.3f° ПВО = %s, при %.3f° ПВО = %s" % (
        crit, crit - 0.5, tir2[0], crit + 0.5, tir2[1]))
    config = {
        "camera": {"position": [0, 0, 5], "look_at": [0, 0, 0], "fov": 40, "width": 10, "height": 10},
        "materials": {"glass": {"kt": 1.0, "ior": 1.5}},
        "objects": [{"type": "box", "size": [4, 0.5, 4], "rotate": [0, 0, 0], "material": "glass"}],
        "lights": [],
    }
    scene = Scene(config)
    backend = make_backend("bvh", scene.triangles)
    dirs = normalize(np.column_stack([rng.uniform(-0.6, 0.6, 5), -np.ones(5), rng.uniform(-0.6, 0.6, 5)]))
    orig = np.tile([0.0, 2.0, 0.0], (5, 1))
    cur_o, cur_d = orig, dirs
    for step in range(2):
        tt, tid, u, v = backend.intersect(cur_o, cur_d)
        p = cur_o + tt[:, None] * cur_d
        ng = scene.face_normals[tid]
        side = np.where(np.einsum("ij,ij->i", cur_d, ng) < 0, 1.0, -1.0)
        eta = np.where(side > 0, 1 / 1.5, 1.5)
        cur_d, _ = refract(cur_d, ng * side[:, None], eta)
        cur_o = p - scene.eps * ng * side[:, None]
    shift = np.linalg.norm(np.cross(cur_o - orig, dirs), axis=1)
    out("Плоскопараллельная пластина толщиной 0.5 (n = 1.5): угол между входящим и вышедшим лучом")
    for k in range(5):
        out("  луч %d: угол падения %6.2f°, отклонение %.2e°, боковое смещение %.4f" % (
            k, np.degrees(np.arccos(-dirs[k, 1])), np.degrees(np.arccos(np.clip(dirs[k] @ cur_d[k], -1, 1))), shift[k]))


def tonemap_test():
    rng = np.random.default_rng(1)
    image = rng.gamma(2.0, 0.3, size=(64, 64, 3))
    for mode in ["max", "mean", "fixed"]:
        image8, norm = tone_map(image, mode, 0.8, 2.2)
        rel = image / norm
        out("  %-5s нормировка на %.4f: max = %.4f, средняя яркость Y = %.4f, доля отсечённых > 1: %.2f %%" % (
            mode, norm, rel.max(), luminance(rel).mean(), np.mean(rel > 1) * 100))
    out("  гамма 2.2: 0.5 -> %d, 0.18 -> %d, 1.0 -> %d" % tuple(
        tone_map(np.array([[[0.5, 0.18, 1.0]]]), "fixed", 1.0, 2.2)[0][0, 0]))


def energy_test():
    for name in ["room", "outdoor", "materials"]:
        scene = Scene.load("scenes/%s.json" % name)
        values = ", ".join("%s %.2f" % (m, e) for m, e in zip(scene.material_names, scene.energy))
        out("  %-10s %s -> %s" % (name, values, "OK" if scene.energy.max() <= 1.0 + 1e-9 else "НАРУШЕНИЕ"))


def main():
    os.makedirs("output", exist_ok=True)
    count = int(sys.argv[1]) if len(sys.argv) > 1 else 20000
    out("== 1. Совпадение собственной BVH, перебора и Embree")
    for path in ["scenes/room.json", "scenes/outdoor.json"]:
        backends_agree(path, count)
    out()
    out("== 2. Яркость: сравнение с аналитическим расчётом")
    plane_test()
    out()
    out("== 3. Тени: сравнение с аналитической проверкой отрезок-куб")
    shadow_test(4000)
    out()
    out("== 4. Отражение и преломление")
    optics_test()
    out()
    out("== 5. Перевод в относительные яркости и гамма-коррекция")
    tonemap_test()
    out()
    out("== 6. Закон сохранения энергии в материалах (сумма коэффициентов <= 1)")
    energy_test()
    with open("output/verify.txt", "w", encoding="utf-8") as f:
        f.write("\n".join(LINES) + "\n")


if __name__ == "__main__":
    main()
