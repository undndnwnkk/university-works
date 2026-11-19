import os
import shutil
import time

import numba
import numpy as np
from PIL import Image, ImageDraw, ImageFont

from accel import BVHBackend, make_backend
from render import primary_rays, project, render, save_png, save_ppm, settings_of, tone_map
from scene import Scene

REPORT = "report"
THREADS = numba.get_num_threads()
LINES = []


def out(text=""):
    print(text)
    LINES.append(text)


def final_render(name, **overrides):
    scene = Scene.load("scenes/%s.json" % name)
    settings = dict(settings_of(scene.config), **overrides)
    backend = make_backend(settings["backend"], scene.triangles)
    image, stats = render(scene, backend, settings)
    image8, norm = tone_map(image, settings["normalize"], settings["norm_value"], settings["gamma"])
    save_ppm("output/%s.ppm" % name, image8)
    save_png(os.path.join(REPORT, "%s.png" % name), image8)
    cam = scene.config["camera"]
    out("%-10s %4dx%-4d spp=%2d %6d треуг. время %6.2f с, лучей %8d, теневых %8d, нормировка %s=%.4g" % (
        name, cam["width"], cam["height"], settings["spp"], len(scene.triangles), stats["time"],
        stats["rays"], stats["shadow_rays"], settings["normalize"], norm))
    return scene, image, settings


def side_by_side(images, labels, path, gap=8):
    h = max(im.shape[0] for im in images)
    w = sum(im.shape[1] for im in images) + gap * (len(images) - 1)
    canvas = Image.new("RGB", (w, h + 34), (255, 255, 255))
    draw = ImageDraw.Draw(canvas)
    font = ImageFont.load_default(size=22)
    x = 0
    for im, label in zip(images, labels):
        canvas.paste(Image.fromarray(im), (x, 34))
        draw.text((x + 6, 4), label, fill=(0, 0, 0), font=font)
        x += im.shape[1] + gap
    canvas.save(path)


def crop_zoom(image8, x, y, size, zoom):
    part = image8[y:y + size, x:x + size]
    return np.repeat(np.repeat(part, zoom, axis=0), zoom, axis=1)


def heat(values):
    v = values / values.max()
    stops = np.array([[0, 0, 0], [30, 30, 160], [200, 40, 120], [250, 200, 40], [255, 255, 255]], dtype=np.float64)
    pos = v * (len(stops) - 1)
    i = np.clip(pos.astype(int), 0, len(stops) - 2)
    f = (pos - i)[..., None]
    return (stops[i] * (1 - f) + stops[i + 1] * f).astype(np.uint8)


def antialiasing(scene, settings):
    images = []
    for spp in [1, 16]:
        image, _ = render(scene, make_backend("embree", scene.triangles), dict(settings, spp=spp))
        images.append(tone_map(image, settings["normalize"], settings["norm_value"], settings["gamma"])[0])
    crops = [crop_zoom(im, 300, 330, 110, 4) for im in images]
    side_by_side(crops, ["spp = 1", "spp = 16 (stratified jitter)"],
                 os.path.join(REPORT, "aa_compare.png"))


def normalization(image, settings):
    tiles = []
    labels = []
    for mode, value in [("max", 1.0), ("mean", 1.0), ("fixed", settings["norm_value"])]:
        image8, norm = tone_map(image, mode, value, settings["gamma"])
        tiles.append(np.array(Image.fromarray(image8).resize((300, 300), Image.LANCZOS)))
        labels.append("%s: norm = %.3g" % (mode, norm))
    image8, _ = tone_map(image, "fixed", settings["norm_value"], 1.0)
    tiles.append(np.array(Image.fromarray(image8).resize((300, 300), Image.LANCZOS)))
    labels.append("fixed, gamma = 1")
    side_by_side(tiles, labels, os.path.join(REPORT, "norm_compare.png"))


def modes(scene, settings):
    tiles = []
    labels = []
    for mode in ["split", "path"]:
        image, stats = render(scene, make_backend("embree", scene.triangles), dict(settings, mode=mode, spp=16))
        image8 = tone_map(image, settings["normalize"], settings["norm_value"], settings["gamma"])[0]
        tiles.append(crop_zoom(image8, 150, 360, 200, 2))
        labels.append("%s: %.1f s, %d rays" % (mode, stats["time"], stats["rays"]))
        out("  режим %-5s время %.2f с, лучей %d, теневых %d" % (mode, stats["time"], stats["rays"], stats["shadow_rays"]))
    side_by_side(tiles, labels, os.path.join(REPORT, "mode_compare.png"))


def heatmap(scene):
    backend = BVHBackend(scene.triangles)
    cam = scene.config["camera"]
    py, px = np.divmod(np.arange(cam["width"] * cam["height"]), cam["width"])
    orig, dirs = primary_rays(cam, px + 0.5, py + 0.5)
    backend.intersect(orig, dirs)
    steps = backend.last_steps.reshape(cam["height"], cam["width"]).astype(np.float64)
    Image.fromarray(heat(steps)).save(os.path.join(REPORT, "bvh_heatmap.png"))
    out("  посещённых узлов BVH на первичный луч: среднее %.1f, максимум %d (из %d узлов)" % (
        steps.mean(), steps.max(), backend.nodes()))


def bvh_boxes(scene, image8):
    backend = BVHBackend(scene.triangles)
    tree = backend.tree
    depth = np.zeros(backend.nodes(), np.int32)
    for node in range(backend.nodes()):
        if tree["left"][node] >= 0:
            depth[tree["left"][node]] = depth[node] + 1
            depth[tree["right"][node]] = depth[node] + 1
    img = Image.fromarray(image8 // 2 + 40)
    draw = ImageDraw.Draw(img)
    colors = {1: (255, 60, 60), 2: (60, 220, 60), 3: (80, 160, 255), 4: (255, 220, 40)}
    edges = [(0, 1), (1, 3), (3, 2), (2, 0), (4, 5), (5, 7), (7, 6), (6, 4), (0, 4), (1, 5), (2, 6), (3, 7)]
    for node in range(backend.nodes()):
        if depth[node] not in colors:
            continue
        lo, hi = tree["bmin"][node], tree["bmax"][node]
        corners = np.array([[x, y, z] for x in (lo[0], hi[0]) for y in (lo[1], hi[1]) for z in (lo[2], hi[2])])
        pix = project(scene.config["camera"], corners)
        for a, b in edges:
            draw.line([tuple(pix[a]), tuple(pix[b])], fill=colors[depth[node]], width=2)
    img.save(os.path.join(REPORT, "bvh_boxes.png"))


def benchmark():
    out("Сцена | треуг. | backend | построение, с | узлов | листьев | глубина | лучей | время, с | млн лучей/с")
    for name in ["test_shadow", "materials", "room", "outdoor"]:
        scene = Scene.load("scenes/%s.json" % name)
        cam = scene.config["camera"]
        rng = np.random.default_rng(0)
        for backend_name, threads in [("brute", THREADS), ("bvh", THREADS), ("bvh", 1), ("embree", 1)]:
            count = 20000 if backend_name == "brute" else 400000
            orig, dirs = primary_rays(cam, rng.random(count) * cam["width"], rng.random(count) * cam["height"])
            backend = make_backend(backend_name, scene.triangles)
            numba.set_num_threads(threads)
            backend.intersect(orig[:100], dirs[:100])
            best = np.inf
            for _ in range(3):
                start = time.perf_counter()
                backend.intersect(orig, dirs)
                best = min(best, time.perf_counter() - start)
            numba.set_num_threads(THREADS)
            extra = (backend.nodes(), backend.leaves(), backend.depth()) if backend_name == "bvh" else ("-", "-", "-")
            out("%s | %d | %s (%d пот.) | %.4f | %s | %s | %s | %d | %.4f | %.3f" % (
                name, len(scene.triangles), backend_name, threads, backend.build_time, *extra, count, best, count / best / 1e6))


def render_times():
    scene = Scene.load("scenes/room.json")
    settings = dict(settings_of(scene.config), spp=4)
    for name in ["bvh", "embree"]:
        backend = make_backend(name, scene.triangles)
        _, stats = render(scene, backend, settings)
        out("  room 600x600 spp=4 backend=%-6s время %.2f с (%d лучей + %d теневых)" % (
            name, stats["time"], stats["rays"], stats["shadow_rays"]))


def main():
    os.makedirs(REPORT, exist_ok=True)
    os.makedirs("output", exist_ok=True)
    out("== Финальные изображения")
    room, room_image, room_settings = final_render("room")
    final_render("outdoor")
    final_render("materials")
    out()
    out("== Антиалиасинг, нормировка, режимы трассировки")
    antialiasing(room, room_settings)
    normalization(room_image, room_settings)
    modes(room, room_settings)
    out()
    out("== BVH")
    heatmap(room)
    bvh_boxes(room, tone_map(room_image, room_settings["normalize"], room_settings["norm_value"], 2.2)[0])
    out()
    out("== Производительность (первичные лучи)")
    benchmark()
    render_times()
    if os.path.exists("output/verify_shadow.png"):
        shutil.copy("output/verify_shadow.png", os.path.join(REPORT, "verify_shadow.png"))
    with open("output/experiments.txt", "w", encoding="utf-8") as f:
        f.write("\n".join(LINES) + "\n")


if __name__ == "__main__":
    main()
