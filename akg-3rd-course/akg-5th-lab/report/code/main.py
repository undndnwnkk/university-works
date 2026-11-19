import argparse
import json
import os
import shlex
import time

import numpy as np

from accel import make_backend
from render import radiance_at, render, save_png, save_ppm, settings_of, tone_map
from scene import Scene

HELP = """Команды:
  show [раздел]          показать параметры (camera, render, materials, objects, lights)
  set путь значение      изменить параметр, значение в формате JSON
                         примеры: set camera.width 800
                                  set render.backend "bvh"
                                  set lights.0.intensity [5,5,5]
                                  set materials.glass.ior 1.33
                                  set objects.6.position [0,0.5,0]
  render                 построить изображение (PPM + PNG)
  probe x y              абсолютная яркость в пикселе (x, y)
  bench [N]              сравнить brute / bvh / embree на N первичных лучах
  save файл.json         сохранить сцену
  load файл.json         загрузить другую сцену
  help                   эта подсказка
  quit                   выход"""


def flatten(value, prefix=""):
    if isinstance(value, dict):
        for key, item in value.items():
            yield from flatten(item, prefix + "." + key if prefix else key)
    elif isinstance(value, list) and value and isinstance(value[0], dict):
        for i, item in enumerate(value):
            yield from flatten(item, prefix + "." + str(i))
    else:
        yield prefix, value


def set_value(config, path, value):
    keys = path.split(".")
    node = config
    for key in keys[:-1]:
        node = node[int(key)] if isinstance(node, list) else node.setdefault(key, {})
    last = keys[-1]
    if isinstance(node, list):
        node[int(last)] = value
    else:
        node[last] = value


def parse_value(text):
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        return text


class App:
    def __init__(self, path):
        self.load(path)

    def load(self, path):
        self.path = path
        self.scene = Scene.load(path)
        self.backend = None
        self.backend_key = None
        print("Сцена %s: %s" % (path, self.scene.summary()))

    def rebuild(self):
        geometry = json.dumps([self.scene.config["objects"], self.scene.config["materials"], self.scene.config["lights"]])
        if geometry != getattr(self, "geometry_key", None):
            self.scene = Scene(self.scene.config, self.scene.base_dir)
            self.geometry_key = geometry
            self.backend = None
        settings = settings_of(self.scene.config)
        key = (settings["backend"], len(self.scene.triangles), id(self.scene))
        if self.backend is None or key != self.backend_key:
            start = time.perf_counter()
            self.backend = make_backend(settings["backend"], self.scene.triangles)
            self.backend_key = key
            print("Ускоряющая структура %s построена за %.3f с" % (self.backend.name, time.perf_counter() - start))
        return settings

    def show(self, section=None):
        config = self.scene.config if section is None else {section: self.scene.config[section]}
        for path, value in flatten(config):
            print("  %-40s = %s" % (path, json.dumps(value, ensure_ascii=False)))

    def render(self):
        settings = self.rebuild()
        cam = self.scene.config["camera"]
        print("Рендер %dx%d, %d лучей на пиксель, глубина %d, режим %s, %s" % (
            cam["width"], cam["height"], settings["spp"], settings["max_depth"], settings["mode"], self.scene.summary()))

        def progress(done, total, elapsed):
            print("\r  проход %d/%d, %.1f с" % (done, total, elapsed), end="", flush=True)

        image, stats = render(self.scene, self.backend, settings, progress)
        print()
        image8, norm = tone_map(image, settings["normalize"], settings["norm_value"], settings["gamma"])
        out = settings["output"]
        os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
        save_ppm(out + ".ppm", image8)
        save_png(out + ".png", image8)
        np.save(out + "_abs.npy", image.astype(np.float32))
        print("Время %.2f с, лучей %d, теневых лучей %d (%.2f млн лучей/с)" % (
            stats["time"], stats["rays"], stats["shadow_rays"], (stats["rays"] + stats["shadow_rays"]) / stats["time"] / 1e6))
        print("Яркость: макс %.4g, средняя %.4g, нормировка на %.4g (%s), гамма %.2f" % (
            image.max(), image.mean(), norm, settings["normalize"], settings["gamma"]))
        print("Сохранено: %s.ppm, %s.png" % (out, out))
        return image

    def probe(self, x, y):
        settings = self.rebuild()
        value = radiance_at(self.scene, self.backend, settings, [x + 0.5], [y + 0.5])[0]
        print("Яркость L(%d, %d) = [%.6g, %.6g, %.6g] Вт/(ср*м2)" % (x, y, *value))

    def bench(self, count):
        from render import primary_rays
        cam = self.scene.config["camera"]
        rng = np.random.default_rng(0)
        orig, dirs = primary_rays(cam, rng.random(count) * cam["width"], rng.random(count) * cam["height"])
        results = {}
        for name in ["brute", "bvh", "embree"]:
            backend = make_backend(name, self.scene.triangles)
            backend.intersect(orig[:64], dirs[:64])
            start = time.perf_counter()
            results[name] = backend.intersect(orig, dirs)
            elapsed = time.perf_counter() - start
            print("  %-7s построение %.3f с, трассировка %d лучей %.3f с, %.3f млн лучей/с" % (
                name, backend.build_time, count, elapsed, count / elapsed / 1e6))
        for name in ["bvh", "embree"]:
            same = np.mean(results[name][1] == results["brute"][1]) * 100.0
            hit = results["brute"][1] >= 0
            dt = np.max(np.abs(results[name][0][hit] - results["brute"][0][hit])) if hit.any() else 0.0
            print("  %-7s совпадение треугольников с brute: %.3f %%, max |dt| = %.2e" % (name, same, dt))

    def execute(self, line):
        parts = shlex.split(line)
        if not parts:
            return True
        cmd, args = parts[0], parts[1:]
        if cmd in ("quit", "exit", "q"):
            return False
        if cmd == "help":
            print(HELP)
        elif cmd == "show":
            self.show(args[0] if args else None)
        elif cmd == "set":
            set_value(self.scene.config, args[0], parse_value(" ".join(args[1:])))
            print("  %s = %s" % (args[0], " ".join(args[1:])))
        elif cmd == "render":
            self.render()
        elif cmd == "probe":
            self.probe(int(args[0]), int(args[1]))
        elif cmd == "bench":
            self.bench(int(args[0]) if args else 20000)
        elif cmd == "save":
            self.scene.save(args[0])
            print("Сцена сохранена в", args[0])
        elif cmd == "load":
            self.load(args[0])
        else:
            print("Неизвестная команда, help - список команд")
        return True

    def loop(self):
        print(HELP)
        while True:
            try:
                line = input("lab5> ")
            except EOFError:
                break
            try:
                if not self.execute(line):
                    break
            except (KeyError, IndexError, ValueError) as error:
                print("Ошибка:", error)


def main():
    parser = argparse.ArgumentParser(description="ЛР-5 АКГ: обратная трассировка путей")
    parser.add_argument("scene", nargs="?", default="scenes/room.json")
    parser.add_argument("--set", action="append", default=[], metavar="ПУТЬ=ЗНАЧЕНИЕ")
    parser.add_argument("--render", action="store_true")
    parser.add_argument("--bench", type=int, default=0)
    parser.add_argument("--commands", metavar="ФАЙЛ")
    args = parser.parse_args()
    app = App(args.scene)
    for item in args.set:
        path, value = item.split("=", 1)
        set_value(app.scene.config, path, parse_value(value))
    if args.commands:
        with open(args.commands, encoding="utf-8") as f:
            for line in f:
                print("lab5>", line.strip())
                if not app.execute(line):
                    return
        return
    if args.bench:
        app.bench(args.bench)
    if args.render:
        app.render()
    if not (args.render or args.bench):
        app.loop()


if __name__ == "__main__":
    main()
