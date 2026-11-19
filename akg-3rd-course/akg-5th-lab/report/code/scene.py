import json
import math
import os

import numpy as np


def normalize(v):
    v = np.asarray(v, dtype=np.float64)
    n = np.linalg.norm(v, axis=-1, keepdims=True)
    return v / np.where(n == 0.0, 1.0, n)


def rotation_matrix(angles):
    ax, ay, az = [math.radians(a) for a in angles]
    rx = np.array([[1, 0, 0], [0, math.cos(ax), -math.sin(ax)], [0, math.sin(ax), math.cos(ax)]])
    ry = np.array([[math.cos(ay), 0, math.sin(ay)], [0, 1, 0], [-math.sin(ay), 0, math.cos(ay)]])
    rz = np.array([[math.cos(az), -math.sin(az), 0], [math.sin(az), math.cos(az), 0], [0, 0, 1]])
    return rz @ ry @ rx


def quad_mesh(points):
    p = np.asarray(points, dtype=np.float64)
    verts = p
    faces = np.array([[0, 1, 2], [0, 2, 3]])
    return verts, faces


def box_mesh(size):
    sx, sy, sz = np.asarray(size, dtype=np.float64) / 2.0
    verts = np.array([
        [-sx, -sy, -sz], [sx, -sy, -sz], [sx, sy, -sz], [-sx, sy, -sz],
        [-sx, -sy, sz], [sx, -sy, sz], [sx, sy, sz], [-sx, sy, sz],
    ])
    faces = np.array([
        [0, 2, 1], [0, 3, 2], [4, 5, 6], [4, 6, 7],
        [0, 1, 5], [0, 5, 4], [3, 7, 6], [3, 6, 2],
        [0, 4, 7], [0, 7, 3], [1, 2, 6], [1, 6, 5],
    ])
    return verts, faces


def icosphere_mesh(subdiv):
    t = (1.0 + math.sqrt(5.0)) / 2.0
    verts = [[-1, t, 0], [1, t, 0], [-1, -t, 0], [1, -t, 0],
             [0, -1, t], [0, 1, t], [0, -1, -t], [0, 1, -t],
             [t, 0, -1], [t, 0, 1], [-t, 0, -1], [-t, 0, 1]]
    verts = [list(normalize(v)) for v in verts]
    faces = [[0, 11, 5], [0, 5, 1], [0, 1, 7], [0, 7, 10], [0, 10, 11],
             [1, 5, 9], [5, 11, 4], [11, 10, 2], [10, 7, 6], [7, 1, 8],
             [3, 9, 4], [3, 4, 2], [3, 2, 6], [3, 6, 8], [3, 8, 9],
             [4, 9, 5], [2, 4, 11], [6, 2, 10], [8, 6, 7], [9, 8, 1]]
    for _ in range(subdiv):
        cache = {}
        new_faces = []

        def middle(a, b):
            key = (min(a, b), max(a, b))
            if key not in cache:
                verts.append(list(normalize((np.array(verts[a]) + np.array(verts[b])) / 2.0)))
                cache[key] = len(verts) - 1
            return cache[key]

        for a, b, c in faces:
            ab, bc, ca = middle(a, b), middle(b, c), middle(c, a)
            new_faces += [[a, ab, ca], [b, bc, ab], [c, ca, bc], [ab, bc, ca]]
        faces = new_faces
    return np.array(verts, dtype=np.float64), np.array(faces)


def checker_faces(n):
    verts = []
    faces = []
    parity = []
    for i in range(n + 1):
        for j in range(n + 1):
            verts.append([i / n - 0.5, 0.0, j / n - 0.5])
    for i in range(n):
        for j in range(n):
            a = i * (n + 1) + j
            b = a + 1
            c = a + n + 2
            d = a + n + 1
            faces += [[a, b, c], [a, c, d]]
            parity += [(i + j) % 2] * 2
    return np.array(verts, dtype=np.float64), np.array(faces), np.array(parity)


def load_obj(path):
    verts = []
    faces = []
    with open(path, encoding="utf-8") as f:
        for line in f:
            parts = line.split()
            if not parts:
                continue
            if parts[0] == "v":
                verts.append([float(x) for x in parts[1:4]])
            elif parts[0] == "f":
                idx = []
                for token in parts[1:]:
                    k = int(token.split("/")[0])
                    idx.append(k - 1 if k > 0 else len(verts) + k)
                for k in range(1, len(idx) - 1):
                    faces.append([idx[0], idx[k], idx[k + 1]])
    return np.array(verts, dtype=np.float64), np.array(faces)


def save_obj(path, verts, faces):
    with open(path, "w", encoding="utf-8") as f:
        for v in verts:
            f.write("v %.6f %.6f %.6f\n" % tuple(v))
        for a, b, c in faces:
            f.write("f %d %d %d\n" % (a + 1, b + 1, c + 1))


def vertex_normals(verts, faces):
    tri = verts[faces]
    fn = np.cross(tri[:, 1] - tri[:, 0], tri[:, 2] - tri[:, 0])
    vn = np.zeros_like(verts)
    for k in range(3):
        np.add.at(vn, faces[:, k], fn)
    return normalize(vn)


def object_mesh(obj, base_dir):
    kind = obj["type"]
    parity = None
    if kind == "quad":
        verts, faces = quad_mesh(obj["points"])
    elif kind == "box":
        verts, faces = box_mesh(obj.get("size", [1, 1, 1]))
    elif kind == "sphere":
        verts, faces = icosphere_mesh(int(obj.get("subdiv", 3)))
        verts = verts * float(obj.get("radius", 1.0))
    elif kind == "checker":
        verts, faces, parity = checker_faces(int(obj.get("cells", 8)))
        verts = verts * float(obj.get("size", 1.0))
    elif kind == "obj":
        verts, faces = load_obj(os.path.join(base_dir, obj["file"]))
    else:
        raise ValueError("unknown object type: " + kind)
    if kind != "quad":
        verts = verts * np.asarray(obj.get("scale", 1.0), dtype=np.float64)
        verts = verts @ rotation_matrix(obj.get("rotate", [0, 0, 0])).T
        verts = verts + np.asarray(obj.get("position", [0, 0, 0]), dtype=np.float64)
    return verts, faces, parity


MATERIAL_DEFAULTS = {
    "kd": [0.0, 0.0, 0.0],
    "ks": [0.0, 0.0, 0.0],
    "shininess": 32.0,
    "model": "blinn",
    "kr": [0.0, 0.0, 0.0],
    "kt": [0.0, 0.0, 0.0],
    "kdt": [0.0, 0.0, 0.0],
    "ior": 1.0,
}


def color(value):
    a = np.asarray(value, dtype=np.float64)
    return np.repeat(a, 3) if a.ndim == 0 else a


class Scene:
    def __init__(self, config, base_dir="."):
        self.config = config
        self.base_dir = base_dir
        self.build_materials()
        self.build_geometry()
        self.build_lights()

    @staticmethod
    def load(path):
        with open(path, encoding="utf-8") as f:
            config = json.load(f)
        return Scene(config, os.path.dirname(os.path.abspath(path)))

    def save(self, path):
        with open(path, "w", encoding="utf-8") as f:
            json.dump(self.config, f, ensure_ascii=False, indent=2)

    def build_materials(self):
        self.material_names = list(self.config["materials"].keys())
        mats = [dict(MATERIAL_DEFAULTS, **self.config["materials"][n]) for n in self.material_names]
        self.kd = np.array([color(m["kd"]) for m in mats])
        self.ks = np.array([color(m["ks"]) for m in mats])
        self.shininess = np.array([float(m["shininess"]) for m in mats])
        self.blinn = np.array([m["model"] == "blinn" for m in mats])
        self.kr = np.array([color(m["kr"]) for m in mats])
        self.kt = np.array([color(m["kt"]) for m in mats])
        self.kdt = np.array([color(m["kdt"]) for m in mats])
        self.ior = np.array([float(m["ior"]) for m in mats])
        self.energy = (self.kd + self.ks + self.kr + self.kt + self.kdt).max(axis=1)

    def build_geometry(self):
        tris, norms, mats, owners = [], [], [], []
        for index, obj in enumerate(self.config["objects"]):
            verts, faces, parity = object_mesh(obj, self.base_dir)
            tri = verts[faces]
            if obj.get("smooth", False):
                nrm = vertex_normals(verts, faces)[faces]
            else:
                fn = normalize(np.cross(tri[:, 1] - tri[:, 0], tri[:, 2] - tri[:, 0]))
                nrm = np.repeat(fn[:, None, :], 3, axis=1)
            if parity is None:
                mid = np.full(len(faces), self.material_names.index(obj["material"]))
            else:
                pair = [self.material_names.index(m) for m in obj["materials"]]
                mid = np.where(parity == 0, pair[0], pair[1])
            tris.append(tri)
            norms.append(nrm)
            mats.append(mid)
            owners.append(np.full(len(faces), index))
        self.triangles = np.concatenate(tris)
        self.vertex_normals = np.concatenate(norms)
        self.tri_material = np.concatenate(mats)
        self.tri_object = np.concatenate(owners)
        e1 = self.triangles[:, 1] - self.triangles[:, 0]
        e2 = self.triangles[:, 2] - self.triangles[:, 0]
        self.face_normals = normalize(np.cross(e1, e2))
        points = self.triangles.reshape(-1, 3)
        self.scale = float(np.max(points.max(0) - points.min(0)))
        self.eps = 1e-4 * max(self.scale, 1.0)

    def build_lights(self):
        self.lights = []
        for light in self.config["lights"]:
            if light["type"] == "point":
                self.lights.append({
                    "type": "point",
                    "position": np.asarray(light["position"], dtype=np.float64),
                    "axis": normalize(light.get("direction", [0, -1, 0])),
                    "intensity": color(light["intensity"]) * np.asarray(light.get("color", [1, 1, 1]), dtype=np.float64),
                    "lambert": bool(light.get("lambert", True)),
                })
            else:
                self.lights.append({
                    "type": "directional",
                    "direction": normalize(light["direction"]),
                    "irradiance": color(light["irradiance"]) * np.asarray(light.get("color", [1, 1, 1]), dtype=np.float64),
                })

    def summary(self):
        return "%d triangles, %d materials, %d lights" % (len(self.triangles), len(self.material_names), len(self.lights))
