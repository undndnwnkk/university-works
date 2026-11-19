import time

import numpy as np
from numba import njit, prange

T_EPS = 1e-9
CHUNK = 256
STACK = 64


def surface_area(bmin, bmax):
    d = np.maximum(bmax - bmin, 0.0)
    return 2.0 * (d[..., 0] * d[..., 1] + d[..., 1] * d[..., 2] + d[..., 2] * d[..., 0])


def build_bvh(triangles, leaf_size=4, max_leaf=16):
    tmin = triangles.min(axis=1)
    tmax = triangles.max(axis=1)
    cent = (tmin + tmax) / 2.0
    order = np.arange(len(triangles))
    bmin, bmax, left, right, start, count = [], [], [], [], [], []

    def new_node():
        bmin.append(None)
        bmax.append(None)
        left.append(-1)
        right.append(-1)
        start.append(0)
        count.append(0)
        return len(left) - 1

    stack = [(new_node(), 0, len(triangles))]
    while stack:
        node, s, e = stack.pop()
        idx = order[s:e]
        lo = tmin[idx].min(axis=0)
        hi = tmax[idx].max(axis=0)
        bmin[node], bmax[node] = lo, hi
        m = e - s
        area = surface_area(lo, hi)
        best_cost, best_split, best_order = np.inf, -1, None
        if m > leaf_size and area > 0.0:
            k = np.arange(1, m)
            for axis in range(3):
                srt = idx[np.argsort(cent[idx, axis], kind="stable")]
                lmin = np.minimum.accumulate(tmin[srt], axis=0)[:-1]
                lmax = np.maximum.accumulate(tmax[srt], axis=0)[:-1]
                rmin = np.minimum.accumulate(tmin[srt][::-1], axis=0)[::-1][1:]
                rmax = np.maximum.accumulate(tmax[srt][::-1], axis=0)[::-1][1:]
                cost = 1.0 + (surface_area(lmin, lmax) * k + surface_area(rmin, rmax) * (m - k)) / area
                i = int(np.argmin(cost))
                if cost[i] < best_cost:
                    best_cost, best_split, best_order = cost[i], i + 1, srt
        if best_order is None or (best_cost >= m and m <= max_leaf):
            start[node], count[node] = s, m
            continue
        order[s:e] = best_order
        mid = s + best_split
        l, r = new_node(), new_node()
        left[node], right[node] = l, r
        stack.append((r, mid, e))
        stack.append((l, s, mid))
    return {
        "bmin": np.array(bmin), "bmax": np.array(bmax),
        "left": np.array(left, dtype=np.int32), "right": np.array(right, dtype=np.int32),
        "start": np.array(start, dtype=np.int32), "count": np.array(count, dtype=np.int32),
        "order": order.astype(np.int32),
    }


@njit(cache=True, inline="always")
def ray_triangle(ox, oy, oz, dx, dy, dz, v0, e1, e2, j, tmax):
    px = dy * e2[j, 2] - dz * e2[j, 1]
    py = dz * e2[j, 0] - dx * e2[j, 2]
    pz = dx * e2[j, 1] - dy * e2[j, 0]
    det = e1[j, 0] * px + e1[j, 1] * py + e1[j, 2] * pz
    if abs(det) < 1e-14:
        return -1.0, 0.0, 0.0
    inv = 1.0 / det
    sx = ox - v0[j, 0]
    sy = oy - v0[j, 1]
    sz = oz - v0[j, 2]
    u = (sx * px + sy * py + sz * pz) * inv
    if u < 0.0 or u > 1.0:
        return -1.0, 0.0, 0.0
    qx = sy * e1[j, 2] - sz * e1[j, 1]
    qy = sz * e1[j, 0] - sx * e1[j, 2]
    qz = sx * e1[j, 1] - sy * e1[j, 0]
    v = (dx * qx + dy * qy + dz * qz) * inv
    if v < 0.0 or u + v > 1.0:
        return -1.0, 0.0, 0.0
    t = (e2[j, 0] * qx + e2[j, 1] * qy + e2[j, 2] * qz) * inv
    if t <= T_EPS or t >= tmax:
        return -1.0, 0.0, 0.0
    return t, u, v


@njit(cache=True, inline="always")
def ray_box(ox, oy, oz, ix, iy, iz, bmin, bmax, node, tmax):
    t1 = (bmin[node, 0] - ox) * ix
    t2 = (bmax[node, 0] - ox) * ix
    tn = min(t1, t2)
    tf = max(t1, t2)
    t1 = (bmin[node, 1] - oy) * iy
    t2 = (bmax[node, 1] - oy) * iy
    tn = max(tn, min(t1, t2))
    tf = min(tf, max(t1, t2))
    t1 = (bmin[node, 2] - oz) * iz
    t2 = (bmax[node, 2] - oz) * iz
    tn = max(tn, min(t1, t2))
    tf = min(tf, max(t1, t2))
    if tn <= tf and tf >= 0.0 and tn < tmax:
        return tn
    return np.inf


@njit(cache=True, inline="always")
def inverse(x):
    if x == 0.0:
        return 1e30
    return 1.0 / x


@njit(parallel=True, cache=True)
def brute_intersect(orig, dirs, tfar, v0, e1, e2, out_t, out_id, out_u, out_v):
    n = orig.shape[0]
    for i in prange(n):
        best = tfar[i]
        hit = -1
        bu = 0.0
        bv = 0.0
        for j in range(v0.shape[0]):
            t, u, v = ray_triangle(orig[i, 0], orig[i, 1], orig[i, 2], dirs[i, 0], dirs[i, 1], dirs[i, 2], v0, e1, e2, j, best)
            if t > 0.0:
                best, hit, bu, bv = t, j, u, v
        out_t[i] = best
        out_id[i] = hit
        out_u[i] = bu
        out_v[i] = bv


@njit(parallel=True, cache=True)
def brute_occluded(orig, dirs, tfar, v0, e1, e2, out):
    n = orig.shape[0]
    for i in prange(n):
        out[i] = False
        for j in range(v0.shape[0]):
            t, u, v = ray_triangle(orig[i, 0], orig[i, 1], orig[i, 2], dirs[i, 0], dirs[i, 1], dirs[i, 2], v0, e1, e2, j, tfar[i])
            if t > 0.0:
                out[i] = True
                break


@njit(parallel=True, cache=True)
def bvh_intersect(orig, dirs, tfar, bmin, bmax, left, right, start, count, v0, e1, e2, out_t, out_id, out_u, out_v, out_steps):
    n = orig.shape[0]
    chunks = (n + CHUNK - 1) // CHUNK
    for c in prange(chunks):
        stack = np.empty(STACK, np.int32)
        dist = np.empty(STACK, np.float64)
        for i in range(c * CHUNK, min(n, (c + 1) * CHUNK)):
            ox, oy, oz = orig[i, 0], orig[i, 1], orig[i, 2]
            dx, dy, dz = dirs[i, 0], dirs[i, 1], dirs[i, 2]
            ix, iy, iz = inverse(dx), inverse(dy), inverse(dz)
            best = tfar[i]
            hit = -1
            bu = 0.0
            bv = 0.0
            steps = 0
            sp = 0
            tn = ray_box(ox, oy, oz, ix, iy, iz, bmin, bmax, 0, best)
            if tn < np.inf:
                stack[0] = 0
                dist[0] = tn
                sp = 1
            while sp > 0:
                sp -= 1
                node = stack[sp]
                if dist[sp] >= best:
                    continue
                steps += 1
                if count[node] > 0:
                    for j in range(start[node], start[node] + count[node]):
                        t, u, v = ray_triangle(ox, oy, oz, dx, dy, dz, v0, e1, e2, j, best)
                        if t > 0.0:
                            best, hit, bu, bv = t, j, u, v
                else:
                    l = left[node]
                    r = right[node]
                    tl = ray_box(ox, oy, oz, ix, iy, iz, bmin, bmax, l, best)
                    tr = ray_box(ox, oy, oz, ix, iy, iz, bmin, bmax, r, best)
                    hl = tl < np.inf
                    hr = tr < np.inf
                    if hl and hr:
                        if tl <= tr:
                            stack[sp] = r
                            dist[sp] = tr
                            stack[sp + 1] = l
                            dist[sp + 1] = tl
                        else:
                            stack[sp] = l
                            dist[sp] = tl
                            stack[sp + 1] = r
                            dist[sp + 1] = tr
                        sp += 2
                    elif hl:
                        stack[sp] = l
                        dist[sp] = tl
                        sp += 1
                    elif hr:
                        stack[sp] = r
                        dist[sp] = tr
                        sp += 1
            out_t[i] = best
            out_id[i] = hit
            out_u[i] = bu
            out_v[i] = bv
            out_steps[i] = steps


@njit(parallel=True, cache=True)
def bvh_occluded(orig, dirs, tfar, bmin, bmax, left, right, start, count, v0, e1, e2, out):
    n = orig.shape[0]
    chunks = (n + CHUNK - 1) // CHUNK
    for c in prange(chunks):
        stack = np.empty(STACK, np.int32)
        for i in range(c * CHUNK, min(n, (c + 1) * CHUNK)):
            ox, oy, oz = orig[i, 0], orig[i, 1], orig[i, 2]
            dx, dy, dz = dirs[i, 0], dirs[i, 1], dirs[i, 2]
            ix, iy, iz = inverse(dx), inverse(dy), inverse(dz)
            tmax = tfar[i]
            found = False
            stack[0] = 0
            sp = 1
            while sp > 0 and not found:
                sp -= 1
                node = stack[sp]
                if ray_box(ox, oy, oz, ix, iy, iz, bmin, bmax, node, tmax) == np.inf:
                    continue
                if count[node] > 0:
                    for j in range(start[node], start[node] + count[node]):
                        t, u, v = ray_triangle(ox, oy, oz, dx, dy, dz, v0, e1, e2, j, tmax)
                        if t > 0.0:
                            found = True
                            break
                else:
                    stack[sp] = left[node]
                    stack[sp + 1] = right[node]
                    sp += 2
            out[i] = found


def prepare(triangles):
    v0 = np.ascontiguousarray(triangles[:, 0])
    e1 = np.ascontiguousarray(triangles[:, 1] - triangles[:, 0])
    e2 = np.ascontiguousarray(triangles[:, 2] - triangles[:, 0])
    return v0, e1, e2


def as_rays(orig, dirs, tfar):
    orig = np.ascontiguousarray(orig, dtype=np.float64)
    dirs = np.ascontiguousarray(dirs, dtype=np.float64)
    if tfar is None:
        tfar = np.full(len(orig), np.inf)
    tfar = np.ascontiguousarray(np.broadcast_to(tfar, (len(orig),)), dtype=np.float64)
    return orig, dirs, tfar


class BruteBackend:
    name = "brute"

    def __init__(self, triangles):
        self.v0, self.e1, self.e2 = prepare(triangles)
        self.build_time = 0.0

    def intersect(self, orig, dirs, tfar=None):
        orig, dirs, tfar = as_rays(orig, dirs, tfar)
        n = len(orig)
        t, tid, u, v = np.empty(n), np.empty(n, np.int64), np.empty(n), np.empty(n)
        brute_intersect(orig, dirs, tfar, self.v0, self.e1, self.e2, t, tid, u, v)
        t[tid < 0] = np.inf
        return t, tid, u, v

    def occluded(self, orig, dirs, tfar):
        orig, dirs, tfar = as_rays(orig, dirs, tfar)
        out = np.empty(len(orig), np.bool_)
        brute_occluded(orig, dirs, tfar, self.v0, self.e1, self.e2, out)
        return out


class BVHBackend:
    name = "bvh"

    def __init__(self, triangles, leaf_size=4):
        start_time = time.perf_counter()
        self.tree = build_bvh(triangles, leaf_size)
        self.build_time = time.perf_counter() - start_time
        self.order = self.tree["order"]
        self.v0, self.e1, self.e2 = prepare(triangles[self.order])
        self.args = (self.tree["bmin"], self.tree["bmax"], self.tree["left"], self.tree["right"],
                     self.tree["start"], self.tree["count"], self.v0, self.e1, self.e2)
        self.last_steps = None

    def nodes(self):
        return len(self.tree["left"])

    def leaves(self):
        return int(np.sum(self.tree["count"] > 0))

    def depth(self):
        depth = np.zeros(self.nodes(), np.int32)
        for node in range(self.nodes()):
            if self.tree["left"][node] >= 0:
                depth[self.tree["left"][node]] = depth[node] + 1
                depth[self.tree["right"][node]] = depth[node] + 1
        return int(depth.max())

    def intersect(self, orig, dirs, tfar=None):
        orig, dirs, tfar = as_rays(orig, dirs, tfar)
        n = len(orig)
        t, tid, u, v = np.empty(n), np.empty(n, np.int64), np.empty(n), np.empty(n)
        steps = np.empty(n, np.int64)
        bvh_intersect(orig, dirs, tfar, *self.args, t, tid, u, v, steps)
        self.last_steps = steps
        hit = tid >= 0
        tid[hit] = self.order[tid[hit]]
        t[~hit] = np.inf
        return t, tid, u, v

    def occluded(self, orig, dirs, tfar):
        orig, dirs, tfar = as_rays(orig, dirs, tfar)
        out = np.empty(len(orig), np.bool_)
        bvh_occluded(orig, dirs, tfar, *self.args, out)
        return out


class EmbreeBackend:
    name = "embree"

    def __init__(self, triangles):
        from embreex import rtcore_scene
        from embreex.mesh_construction import TriangleMesh
        start_time = time.perf_counter()
        self.scene = rtcore_scene.EmbreeScene()
        self.mesh = TriangleMesh(self.scene, triangles.astype(np.float32))
        self.scene.run(np.zeros((1, 3), np.float32), np.ones((1, 3), np.float32))
        self.build_time = time.perf_counter() - start_time

    def intersect(self, orig, dirs, tfar=None):
        orig, dirs, tfar = as_rays(orig, dirs, tfar)
        res = self.scene.run(orig.astype(np.float32), dirs.astype(np.float32),
                             dists=np.minimum(tfar, 1e30).astype(np.float32), output=1)
        tid = res["primID"].astype(np.int64)
        t = res["tfar"].astype(np.float64)
        t[tid < 0] = np.inf
        return t, tid, res["u"].astype(np.float64), res["v"].astype(np.float64)

    def occluded(self, orig, dirs, tfar):
        orig, dirs, tfar = as_rays(orig, dirs, tfar)
        res = self.scene.run(orig.astype(np.float32), dirs.astype(np.float32),
                             dists=np.minimum(tfar, 1e30).astype(np.float32), query="OCCLUDED")
        return res >= 0


def embree_available():
    try:
        import embreex
        return True
    except ImportError:
        return False


def make_backend(name, triangles):
    if name == "embree" and not embree_available():
        print("embreex не установлен, используется собственная BVH")
        name = "bvh"
    if name == "brute":
        return BruteBackend(triangles)
    if name == "embree":
        return EmbreeBackend(triangles)
    return BVHBackend(triangles)
