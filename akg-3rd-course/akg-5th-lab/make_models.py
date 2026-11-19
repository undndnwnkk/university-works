import os
import sys

import numpy as np

from scene import normalize, save_obj


def torus_knot(p, q, segments, sides, radius, tube):
    s = np.linspace(0.0, 2.0 * np.pi, segments, endpoint=False)

    def curve(a):
        r = 1.0 + 0.4 * np.cos(q * a)
        return np.stack([r * np.cos(p * a), 0.4 * np.sin(q * a), r * np.sin(p * a)], axis=-1) * radius

    center = curve(s)
    tangent = normalize(curve(s + 1e-4) - curve(s - 1e-4))
    normal = normalize(np.cross(tangent, curve(s + 1e-4) + curve(s - 1e-4) - 2.0 * center + 1e-12))
    normal = normalize(np.cross(tangent, np.cross(normal, tangent)))
    binormal = np.cross(tangent, normal)
    a = np.linspace(0.0, 2.0 * np.pi, sides, endpoint=False)
    verts = (center[:, None, :]
             + tube * np.cos(a)[None, :, None] * normal[:, None, :]
             + tube * np.sin(a)[None, :, None] * binormal[:, None, :]).reshape(-1, 3)
    faces = []
    for i in range(segments):
        for j in range(sides):
            a0 = i * sides + j
            a1 = i * sides + (j + 1) % sides
            b0 = ((i + 1) % segments) * sides + j
            b1 = ((i + 1) % segments) * sides + (j + 1) % sides
            faces += [[a0, b0, b1], [a0, b1, a1]]
    return verts, np.array(faces)


if __name__ == "__main__":
    segments = int(sys.argv[1]) if len(sys.argv) > 1 else 400
    sides = int(sys.argv[2]) if len(sys.argv) > 2 else 24
    verts, faces = torus_knot(2, 3, segments, sides, 1.0, 0.28)
    os.makedirs("models", exist_ok=True)
    save_obj("models/knot.obj", verts, faces)
    print("models/knot.obj:", len(verts), "vertices,", len(faces), "triangles")
