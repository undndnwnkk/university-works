"""
ЛР-4 по АКГ: «Моделирование цветных изображений (построение спектра по цвету)».

Этот модуль - «вычислительное ядро» без графического интерфейса:
  1) спектральная сцена (как в ЛР-3)  ->  RGB-сцена;
  2) RGB-сцена  ->  восстановленные спектры (построение спектра по цвету);
  3) рендеринг сферы: спектральное изображение -> RGB (абс.) -> RGB 0..255;
  4) сравнение спектров и изображений, яркость в точках сферы, сохранение файлов.

Интерфейс (app.py) только собирает параметры, вызывает run_lab() и показывает результат.
Запуск без интерфейса:  python lab4.py   - посчитает два сценария и сохранит их в results/.
"""
from __future__ import annotations

import csv
import os
from dataclasses import dataclass, field

import numpy as np
from PIL import Image

from cie_data import CMF_XYZ, D65, D_LAMBDA, WAVELENGTHS

# ----------------------------------------------------------------------------
# 1. Цветовые преобразования (матрицы из методички, система CIE RGB 1931)
# ----------------------------------------------------------------------------

# RGB -> XYZ
M_RGB_TO_XYZ = (1.0 / 0.17697) * np.array([
    [0.49000, 0.31000, 0.20000],
    [0.17697, 0.81240, 0.01063],
    [0.00000, 0.01000, 0.99000],
])

# XYZ -> RGB (обратная матрица). В методичке она записана без коэффициента:
# матрица из методички - обратная к матрице в скобках, поэтому для настоящей
# обратной к M_RGB_TO_XYZ её нужно домножить на 0.17697 (проверка: M * M^-1 = E).
M_XYZ_TO_RGB = 0.17697 * np.array([
    [2.36461, -0.89654, -0.46807],
    [-0.51517, 1.42641, 0.08876],
    [0.00520, -0.01441, 1.00920],
])

LUMINOUS_EFFICACY = 683.0  # лм/Вт, перевод энергетических величин в световые


def spectrum_to_xyz(spectrum: np.ndarray) -> np.ndarray:
    """X = ∫ I(λ) x̄(λ) dλ (и так же Y, Z). Интеграл - сумма с шагом 10 нм.
    Работает и для одного спектра (41,), и для изображения (H, W, 41)."""
    return np.asarray(spectrum) @ CMF_XYZ * D_LAMBDA


def xyz_to_rgb(xyz: np.ndarray) -> np.ndarray:
    return np.asarray(xyz) @ M_XYZ_TO_RGB.T


def rgb_to_xyz(rgb: np.ndarray) -> np.ndarray:
    return np.asarray(rgb) @ M_RGB_TO_XYZ.T


def spectrum_to_rgb(spectrum: np.ndarray) -> np.ndarray:
    return xyz_to_rgb(spectrum_to_xyz(spectrum))


# ----------------------------------------------------------------------------
# 2. Спектры источников и поверхности исходной (спектральной) сцены
# ----------------------------------------------------------------------------

def planck(temperature_k: float) -> np.ndarray:
    """Спектр абсолютно чёрного тела (закон Планка), в относительных единицах."""
    lam = WAVELENGTHS * 1e-9  # нм -> м
    c2 = 1.4388e-2            # h*c/k, м*K
    return 1.0 / (lam ** 5 * (np.exp(c2 / (lam * temperature_k)) - 1.0))


LIGHT_KINDS = [
    "D65 (белый дневной)",
    "E (равноэнергетический)",
    "A (лампа накаливания, 2856 K)",
    "Планк, T [K]",
    "Цветной гаусс, λ0 [нм]",
]


def light_spectrum_shape(kind: str, param: float) -> np.ndarray:
    """Форма спектра источника (до нормировки)."""
    if kind.startswith("D65"):
        return D65.copy()
    if kind.startswith("E "):
        return np.ones_like(WAVELENGTHS)
    if kind.startswith("A "):
        return planck(2856.0)
    if kind.startswith("Планк"):
        return planck(param)
    if kind.startswith("Цветной"):
        sigma = 30.0  # ширина «цветного» пика, нм
        return np.exp(-0.5 * ((WAVELENGTHS - param) / sigma) ** 2)
    raise ValueError(f"Неизвестный тип источника: {kind}")


def normalized(spectrum: np.ndarray) -> np.ndarray:
    """Нормировка спектра так, что ∫ S(λ) dλ = 1.
    Тогда спектральная сила излучения I(λ) = I0 * S(λ)  [Вт/(ср*нм)]."""
    return spectrum / (spectrum.sum() * D_LAMBDA)


D65_NORM = normalized(D65)

SPHERE_KINDS = [
    "Белый (kd = ρ)",
    "Цветной: ρ·(0.1 + 0.9·гаусс(λ0, 50 нм))",
]


def sphere_reflectance(kind: str, rho: float, lam0: float) -> np.ndarray:
    """Спектральный коэффициент диффузного отражения сферы kd(λ).
    По заданию сфера белая (kd = const); цветной вариант - дополнительный опыт."""
    if kind.startswith("Белый"):
        return np.full_like(WAVELENGTHS, rho)
    return rho * (0.1 + 0.9 * np.exp(-0.5 * ((WAVELENGTHS - lam0) / 50.0) ** 2))


# ----------------------------------------------------------------------------
# 3. Построение спектра по цвету (лекция 4: S(λ) = a1*F1 + a2*F2 + a3*F3)
# ----------------------------------------------------------------------------

def _box(lo, hi):
    return ((WAVELENGTHS >= lo) & (WAVELENGTHS <= hi)).astype(float)


def _gauss(center, sigma=35.0):
    return np.exp(-0.5 * ((WAVELENGTHS - center) / sigma) ** 2)


def _delta(lam):
    return (WAVELENGTHS == lam).astype(float)


# Базисные функции F1, F2, F3 (столбцы матрицы 41 x 3) в порядке R, G, B.
BASES = {
    "Прямоугольные полосы (R 590-780, G 490-580, B 380-480)":
        np.column_stack([_box(590, 780), _box(490, 580), _box(380, 480)]),
    "Гауссовы (610 / 550 / 450 нм)":
        np.column_stack([_gauss(610), _gauss(550), _gauss(450)]),
    "Дельта-функции (700 / 550 / 440 нм)":
        np.column_stack([_delta(700), _delta(550), _delta(440)]),
}


def basis_matrix(basis: np.ndarray, weight: np.ndarray | None = None) -> np.ndarray:
    """Матрица C: C[k, j] = ∫ w(λ) F_j(λ) cmf_k(λ) dλ  (k = X, Y, Z).
    Столбец j - это XYZ базисной функции F_j (умноженной на вес w, если он задан)."""
    f = basis if weight is None else basis * weight[:, None]
    return CMF_XYZ.T @ f * D_LAMBDA


def reconstruct_light(rgb: np.ndarray, basis: np.ndarray):
    """Спектр источника по его RGB:  X = M*RGB,  A = C^-1 * X,  S = Σ a_j F_j."""
    xyz = rgb_to_xyz(rgb)
    coeffs = np.linalg.solve(basis_matrix(basis), xyz)
    return basis @ coeffs, coeffs


def reconstruct_reflectance(kd_rgb: np.ndarray, basis: np.ndarray):
    """Спектр коэффициента отражения kd(λ) по его RGB.
    RGB отражателя задан относительно D65: kd_rgb = (R_/R, G_/G, B_/B), поэтому
    ищем kd(λ) = Σ b_j F_j(λ) такой, что цвет D65(λ)*kd(λ) равен kd_rgb * RGB(D65)."""
    target_rgb = kd_rgb * spectrum_to_rgb(D65_NORM)
    xyz = rgb_to_xyz(target_rgb)
    coeffs = np.linalg.solve(basis_matrix(basis, weight=D65_NORM), xyz)
    return basis @ coeffs, coeffs


# ----------------------------------------------------------------------------
# 4. Описание сцены
# ----------------------------------------------------------------------------

@dataclass
class Light:
    x: float
    y: float
    z: float
    i0: float                        # сила излучения в направлении оси (θ = 0), Вт/ср
    kind: str = LIGHT_KINDS[0]       # тип спектра (см. LIGHT_KINDS)
    param: float = 6500.0            # T [K] для Планка или λ0 [нм] для гаусса


@dataclass
class Scene:
    W: float = 1000.0      # ширина экрана, мм
    H: float = 1000.0      # высота экрана, мм
    Wres: int = 400        # разрешение по ширине, пикс (Hres считается из W, H)
    zO: float = 3000.0     # наблюдатель в точке (0, 0, zO), смотрит вдоль -Z
    xC: float = 0.0        # центр сферы, мм
    yC: float = 0.0
    zC: float = 300.0
    r: float = 300.0       # радиус сферы, мм
    rho: float = 1.0       # коэффициент отражения белой сферы, kd(λ) = rho
    sphere_kind: str = SPHERE_KINDS[0]
    sphere_lam0: float = 600.0  # λ0 для цветной сферы, нм
    lights: list = field(default_factory=lambda: [
        Light(-1500.0, 1000.0, 2000.0, 1000.0, LIGHT_KINDS[0], 6500.0),
        Light(1500.0, -500.0, 1200.0, 800.0, LIGHT_KINDS[0], 6500.0),
    ])
    basis_name: str = next(iter(BASES))
    gamma: bool = False    # гамма-коррекция 1/2.2 при выводе на монитор
    white_balance: bool = False  # баланс белого: цвет D65 выводится нейтральным серым

    @property
    def pixel(self) -> float:
        """Размер пикселя, мм (пиксели квадратные: W/Wres = H/Hres)."""
        return self.W / self.Wres

    @property
    def Hres(self) -> int:
        return int(round(self.H / self.pixel))

    @property
    def center(self) -> np.ndarray:
        return np.array([self.xC, self.yC, self.zC])

    @property
    def eye(self) -> np.ndarray:
        return np.array([0.0, 0.0, self.zO])


def validate(scene: Scene) -> list[str]:
    """Проверка параметров на соответствие рекомендуемым пределам из задания."""
    err = []

    def check(name, value, lo, hi):
        if not lo <= value <= hi:
            err.append(f"{name} = {value:g} вне диапазона [{lo:g}; {hi:g}]")

    check("W", scene.W, 100, 10000)
    check("H", scene.H, 100, 10000)
    check("Wres", scene.Wres, 200, 800)
    check("Hres", scene.Hres, 200, 800)
    check("xC", scene.xC, -10000, 10000)
    check("yC", scene.yC, -10000, 10000)
    check("zC", scene.zC, 0, 10000)
    if scene.r <= 0:
        err.append("Радиус сферы должен быть > 0")
    check("ρ", scene.rho, 0, 1)
    if not scene.sphere_kind.startswith("Белый"):
        check("λ0 сферы", scene.sphere_lam0, 380, 780)
    for n, L in enumerate(scene.lights, 1):
        check(f"x источника {n}", L.x, -10000, 10000)
        check(f"y источника {n}", L.y, -10000, 10000)
        check(f"z источника {n}", L.z, -10000, 10000)
        check(f"I0 источника {n}", L.i0, 0.01, 10000)
        if L.kind.startswith("Планк"):
            check(f"T источника {n}", L.param, 1000, 25000)
        if L.kind.startswith("Цветной"):
            check(f"λ0 источника {n}", L.param, 380, 780)
    if err:
        return err

    # Сфера должна целиком помещаться в пирамиду видимости:
    # вершина - глаз, основание - экран W x H в плоскости z = 0.
    if scene.zO - scene.zC <= scene.r:
        err.append("Наблюдатель должен быть выше сферы: zO > zC + r")
        return err
    w2 = scene.W / 2
    h2 = scene.Hres * scene.pixel / 2
    corners = [np.array(c, float) for c in
               [(w2, h2, 0), (-w2, h2, 0), (-w2, -h2, 0), (w2, -h2, 0)]]
    inside = np.array([0.0, 0.0, scene.zO / 2])  # точка заведомо внутри пирамиды
    for a, b in zip(corners, corners[1:] + corners[:1]):
        n = np.cross(a - scene.eye, b - scene.eye)
        n /= np.linalg.norm(n)
        if np.dot(inside - scene.eye, n) < 0:
            n = -n
        if np.dot(scene.center - scene.eye, n) < scene.r:
            err.append("Сфера не помещается целиком в область видимости "
                       "(уменьшите r, сдвиньте центр или увеличьте экран / zO)")
            break
    return err


# ----------------------------------------------------------------------------
# 5. Геометрия: трассировка лучей из глаза через пиксели экрана
# ----------------------------------------------------------------------------

def trace_sphere(scene: Scene):
    """Для каждого пикселя ищет точку пересечения луча из глаза со сферой.
    Возвращает mask (H, W) - попал ли луч в сферу, P - точки, N - нормали."""
    p = scene.pixel
    xs = (np.arange(scene.Wres) + 0.5) * p - scene.W / 2
    ys = scene.Hres * p / 2 - (np.arange(scene.Hres) + 0.5) * p  # строка 0 - верх (+Y)
    X, Y = np.meshgrid(xs, ys)

    # Луч: P(t) = O + t*D, D - единичный вектор от глаза к пикселю (x, y, 0)
    D = np.stack([X, Y, np.full_like(X, -scene.zO)], axis=-1)
    D /= np.linalg.norm(D, axis=-1, keepdims=True)

    # |O + tD - C|^2 = r^2  ->  t^2 + 2bt + c = 0
    oc = scene.eye - scene.center
    b = D @ oc
    c = oc @ oc - scene.r ** 2
    disc = b * b - c
    mask = disc >= 0
    t = -b - np.sqrt(np.where(mask, disc, 0.0))  # ближайшее пересечение
    mask &= t > 0

    P = scene.eye + t[..., None] * D
    N = (P - scene.center) / scene.r
    return mask, P, N


def light_geometry(P: np.ndarray, N: np.ndarray, light: Light) -> np.ndarray:
    """Геометрический множитель освещённости G = cosθ * cosσ / R^2  [1/м^2].
    Тогда освещённость E = I0 * G (I = I0*cosθ - ламбертовская диаграмма).
      s = P - P_L,  R = |s|,
      cosθ = (s·O)/|s|, O = (0, 0, -1) - ось излучения источника (вниз),
      cosσ = (-s·N)/|s| - угол падения света на поверхность."""
    s = P - np.array([light.x, light.y, light.z])
    R = np.linalg.norm(s, axis=-1)
    R = np.maximum(R, 1e-9)
    cos_theta = np.clip(-s[..., 2] / R, 0.0, None)
    cos_sigma = np.clip(-np.sum(s * N, axis=-1) / R, 0.0, None)
    R_m = R / 1000.0  # мм -> м
    return cos_theta * cos_sigma / R_m ** 2


# ----------------------------------------------------------------------------
# 6. Рендеринг
# ----------------------------------------------------------------------------

def render_spectral(weights: np.ndarray, light_spectra: np.ndarray, kd: np.ndarray) -> np.ndarray:
    """Спектральное изображение L(x, y, λ) в Вт/(ср*м^2*нм):
       L(λ) = kd(λ)/π * Σ_i I0_i * G_i * S_i(λ).
    weights: (H, W, n) = I0_i*G_i;  light_spectra: (n, 41);  kd: (41,)."""
    E = weights.astype(np.float32) @ light_spectra.astype(np.float32)  # (H, W, 41)
    return E * (kd.astype(np.float32) / np.pi)


def render_rgb(weights: np.ndarray, light_rgb: np.ndarray, kd_rgb: np.ndarray) -> np.ndarray:
    """То же самое, но сразу в трёх каналах R, G, B (как в RGB-рендеринге ЛР-3)."""
    return (weights @ light_rgb) * kd_rgb / np.pi


def to_monitor(rgb_abs: np.ndarray, gamma: bool = False, white_balance: bool = False) -> np.ndarray:
    """Абсолютные RGB -> мониторные 0..255 с нормировкой на максимальное значение.
    Точка белого системы CIE RGB - источник E, поэтому белый D65 в ней голубоватый.
    white_balance=True делит каналы на RGB(D65) (простейшая адаптация по фон Крису),
    и тогда белая сфера под D65 выглядит серой."""
    v = np.clip(rgb_abs, 0.0, None)
    if white_balance:
        v = v / spectrum_to_rgb(D65_NORM)
    vmax = v.max()
    if vmax > 0:
        v = v / vmax
    if gamma:
        v = v ** (1 / 2.2)
    return np.round(v * 255).astype(np.uint8)


# ----------------------------------------------------------------------------
# 7. Полный расчёт лабораторной
# ----------------------------------------------------------------------------

@dataclass
class Result:
    scene: Scene
    # исходная спектральная сцена (ЛР-3)
    light_spectra_orig: np.ndarray       # (n, 41), нормированные: ∫S dλ = 1
    kd_orig: np.ndarray                  # (41,)
    # RGB-сцена (получена из спектральной)
    light_rgb: np.ndarray                # (n, 3)  RGB на 1 Вт/ср
    kd_rgb: np.ndarray                   # (3,)
    # восстановленная спектральная сцена (ЛР-4)
    light_spectra_rec: np.ndarray        # (n, 41)
    light_coeffs: np.ndarray             # (n, 3)
    kd_rec: np.ndarray                   # (41,)
    kd_coeffs: np.ndarray                # (3,)
    # изображения
    mask: np.ndarray
    spectral_A: np.ndarray               # (H, W, 41) спектральное изображение A
    rgb_A: np.ndarray                    # (H, W, 3) абсолютные RGB
    rgb_1: np.ndarray                    # ЛР-3, спектральный расчёт
    rgb_2: np.ndarray                    # ЛР-3, RGB-расчёт
    image_A: np.ndarray                  # (H, W, 3) uint8
    image_1: np.ndarray
    image_2: np.ndarray
    points: list = field(default_factory=list)   # яркость в трёх точках
    stats: dict = field(default_factory=dict)    # max/min, сравнения


def luminance_from_rgb(rgb: np.ndarray) -> np.ndarray:
    """Световая яркость Lv = 683 * Y, кд/м^2 (Y - вторая строка матрицы RGB->XYZ)."""
    return LUMINOUS_EFFICACY * (np.asarray(rgb) @ M_RGB_TO_XYZ[1])


def image_difference(a: np.ndarray, b: np.ndarray, mask: np.ndarray) -> dict:
    """Относительное СКО двух изображений в абсолютных RGB (по пикселям сферы)."""
    da = a[mask] - b[mask]
    rel = np.linalg.norm(da) / max(np.linalg.norm(b[mask]), 1e-30)
    return {"rel_rms_abs": float(rel)}


def compare_monitor(a: np.ndarray, b: np.ndarray, mask: np.ndarray) -> dict:
    d = np.abs(a.astype(int) - b.astype(int))[mask]
    return {"mean_255": float(d.mean()), "max_255": int(d.max())}


def run_lab(scene: Scene) -> Result:
    errors = validate(scene)
    if errors:
        raise ValueError("\n".join(errors))
    basis = BASES[scene.basis_name]

    # --- Исходная спектральная сцена (как в ЛР-3) ---
    spectra_orig = np.array([normalized(light_spectrum_shape(L.kind, L.param))
                             for L in scene.lights])
    kd_orig = sphere_reflectance(scene.sphere_kind, scene.rho, scene.sphere_lam0)

    # --- Спектральная сцена -> RGB-сцена (методичка, «Преобразование данных...») ---
    light_rgb = np.array([spectrum_to_rgb(s) for s in spectra_orig])
    kd_rgb = spectrum_to_rgb(D65_NORM * kd_orig) / spectrum_to_rgb(D65_NORM)

    # --- A) RGB -> спектральное представление ---
    rec = [reconstruct_light(rgb, basis) for rgb in light_rgb]
    spectra_rec = np.array([s for s, _ in rec])
    light_coeffs = np.array([a for _, a in rec])
    kd_rec, kd_coeffs = reconstruct_reflectance(kd_rgb, basis)

    # --- Геометрия ---
    mask, P, N = trace_sphere(scene)
    G = np.stack([light_geometry(P, N, L) for L in scene.lights], axis=-1)
    i0 = np.array([L.i0 for L in scene.lights])
    weights = G * i0 * mask[..., None]  # вне сферы - 0 (чёрный фон)

    # --- A) спектральное изображение -> RGB (абс.) -> RGB (0..255) ---
    spectral_A = render_spectral(weights, spectra_rec, kd_rec)
    rgb_A = xyz_to_rgb(spectrum_to_xyz(spectral_A)).astype(np.float64)
    image_A = to_monitor(rgb_A, scene.gamma, scene.white_balance)

    # --- Изображения ЛР-3 для сравнения (C) ---
    spectral_1 = render_spectral(weights, spectra_orig, kd_orig)
    rgb_1 = xyz_to_rgb(spectrum_to_xyz(spectral_1)).astype(np.float64)
    del spectral_1
    rgb_2 = render_rgb(weights, light_rgb, kd_rgb)
    image_1 = to_monitor(rgb_1, scene.gamma, scene.white_balance)
    image_2 = to_monitor(rgb_2, scene.gamma, scene.white_balance)

    res = Result(scene, spectra_orig, kd_orig, light_rgb, kd_rgb,
                 spectra_rec, light_coeffs, kd_rec, kd_coeffs,
                 mask, spectral_A, rgb_A, rgb_1, rgb_2, image_A, image_1, image_2)
    res.points = brightness_points(res)
    res.stats = compute_stats(res)
    return res


def pixel_brightness(res: Result, row: int, col: int) -> dict:
    """Яркость в пикселе изображения A (для щелчка мышью в интерфейсе)."""
    L = res.spectral_A[row, col].astype(np.float64)
    return {
        "Le": float(L.sum() * D_LAMBDA),
        "Lv": float(LUMINOUS_EFFICACY * spectrum_to_xyz(L)[1]),
        "rgb_A": res.rgb_A[row, col],
        "monitor_A": res.image_A[row, col],
        "Lv_1": float(luminance_from_rgb(res.rgb_1[row, col])),
    }


def brightness_points(res: Result) -> list[dict]:
    """Яркость в трёх точках сферы, заданных направлением нормали."""
    sc = res.scene
    normals = {
        "верх (к наблюдателю)": np.array([0.0, 0.0, 1.0]),
        "45° к +X": np.array([1.0, 0.0, 1.0]) / np.sqrt(2),
        "45° к -X, -Y": np.array([-1.0, -1.0, 1.4142]) / 2.0,
    }
    out = []
    for name, n in normals.items():
        P = sc.center + sc.r * n
        weights = np.array([L.i0 * light_geometry(P, n, L) for L in sc.lights])
        L_rec = res.kd_rec / np.pi * (weights @ res.light_spectra_rec)
        L_orig = res.kd_orig / np.pi * (weights @ res.light_spectra_orig)
        rgb_rgb = (weights @ res.light_rgb) * res.kd_rgb / np.pi
        out.append({
            "name": name, "P": P,
            "Le_A": float(L_rec.sum() * D_LAMBDA),          # Вт/(ср*м^2)
            "Lv_A": float(LUMINOUS_EFFICACY * spectrum_to_xyz(L_rec)[1]),  # кд/м^2
            "rgb_A": spectrum_to_rgb(L_rec),
            "Le_1": float(L_orig.sum() * D_LAMBDA),
            "Lv_1": float(LUMINOUS_EFFICACY * spectrum_to_xyz(L_orig)[1]),
            "Lv_2": float(luminance_from_rgb(rgb_rgb)),
        })
    return out


def compute_stats(res: Result) -> dict:
    m = res.mask
    Le = res.spectral_A.sum(axis=-1) * D_LAMBDA            # энергетическая яркость
    Lv = luminance_from_rgb(res.rgb_A)                     # световая яркость
    lit = m & (Le > 0)                                     # освещённая часть
    st = {
        "Le_max": float(Le[m].max()), "Le_min": float(Le[m].min()),
        "Lv_max": float(Lv[m].max()), "Lv_min": float(Lv[m].min()),
        "Le_min_lit": float(Le[lit].min()) if lit.any() else 0.0,
        "Lv_min_lit": float(Lv[lit].min()) if lit.any() else 0.0,
        "A_vs_1": {**image_difference(res.rgb_A, res.rgb_1, m),
                   **compare_monitor(res.image_A, res.image_1, m)},
        "A_vs_2": {**image_difference(res.rgb_A, res.rgb_2, m),
                   **compare_monitor(res.image_A, res.image_2, m)},
        "1_vs_2": {**image_difference(res.rgb_2, res.rgb_1, m),
                   **compare_monitor(res.image_2, res.image_1, m)},
    }
    st["spectra"] = []
    for n, (so, sr) in enumerate(zip(res.light_spectra_orig, res.light_spectra_rec), 1):
        st["spectra"].append(spectrum_diff(f"Источник {n}", so, sr))
    st["spectra"].append(spectrum_diff("kd сферы", res.kd_orig, res.kd_rec))
    return st


def spectrum_diff(name, orig, rec) -> dict:
    d = rec - orig
    return {
        "name": name,
        "rms": float(np.sqrt(np.mean(d ** 2))),
        "max_abs": float(np.abs(d).max()),
        "rel_rms": float(np.linalg.norm(d) / max(np.linalg.norm(orig), 1e-30)),
        "xyz_orig": spectrum_to_xyz(orig if name != "kd сферы" else orig * D65_NORM),
        "xyz_rec": spectrum_to_xyz(rec if name != "kd сферы" else rec * D65_NORM),
        "has_negative": bool((rec < -1e-12).any()),
    }


# ----------------------------------------------------------------------------
# 8. Текстовый отчёт, графики, сохранение файлов
# ----------------------------------------------------------------------------

def fmt_vec(v, f="{:.5g}"):
    return "(" + ", ".join(f.format(x) for x in v) + ")"


def text_summary(res: Result) -> str:
    sc, st = res.scene, res.stats
    lines = [f"Изображение {sc.Wres} x {sc.Hres} пикс, пиксель {sc.pixel:.3f} мм",
             f"Сфера: {sc.sphere_kind}, ρ = {sc.rho:g}"
             + ("" if sc.sphere_kind.startswith("Белый") else f", λ0 = {sc.sphere_lam0:g} нм"),
             f"Базис восстановления: {sc.basis_name}", "",
             "=== RGB-сцена (получена из спектральной, ЛР-3) ==="]
    for n, (L, rgb) in enumerate(zip(sc.lights, res.light_rgb), 1):
        lines.append(f"Источник {n}: {L.kind}"
                     + (f" ({L.param:g})" if L.kind[0] in "ПЦ" else "")
                     + f", I0 = {L.i0:g} Вт/ср, RGB на 1 Вт/ср = {fmt_vec(rgb)}")
    lines.append(f"RGB(D65) = {fmt_vec(spectrum_to_rgb(D65_NORM))}")
    lines.append(f"kd сферы (RGB относительно D65) = {fmt_vec(res.kd_rgb)}")
    lines += ["", "=== Коэффициенты восстановленных спектров (a_R, a_G, a_B) ==="]
    for n, a in enumerate(res.light_coeffs, 1):
        lines.append(f"Источник {n}: {fmt_vec(a)}")
    lines.append(f"kd сферы:   {fmt_vec(res.kd_coeffs)}")
    lines += ["", "=== B) Сравнение спектров (исходный vs восстановленный) ==="]
    for s in st["spectra"]:
        lines.append(f"{s['name']}: СКО = {s['rms']:.4g}, max|Δ| = {s['max_abs']:.4g}, "
                     f"отн. СКО = {100 * s['rel_rms']:.2f} %"
                     + (" (есть отрицательные значения!)" if s["has_negative"] else ""))
        lines.append(f"   XYZ исх. = {fmt_vec(s['xyz_orig'])}, XYZ восст. = {fmt_vec(s['xyz_rec'])}")
    lines += ["", "=== Яркость в трёх точках сферы (изображение A) ==="]
    for p in res.points:
        lines.append(f"{p['name']}: P = {fmt_vec(p['P'], '{:.1f}')} мм")
        lines.append(f"   Le = {p['Le_A']:.5g} Вт/(ср·м²),  Lv = {p['Lv_A']:.5g} кд/м²,  "
                     f"RGB(абс) = {fmt_vec(p['rgb_A'])}")
        lines.append(f"   для сравнения Lv(image_1) = {p['Lv_1']:.5g},  Lv(image_2) = {p['Lv_2']:.5g} кд/м²")
    lines += ["", "=== Яркость по всей видимой части сферы (изображение A) ===",
              f"Le: max = {st['Le_max']:.5g}, min = {st['Le_min']:.5g} Вт/(ср·м²)",
              f"Lv: max = {st['Lv_max']:.5g}, min = {st['Lv_min']:.5g} кд/м²",
              f"min по освещённой части: Le = {st['Le_min_lit']:.5g} Вт/(ср·м²), "
              f"Lv = {st['Lv_min_lit']:.5g} кд/м² (min = 0 - точки в собственной тени)",
              "", "=== C) Сравнение изображений (по пикселям сферы) ==="]
    for key, title in [("A_vs_1", "image_A vs image_1"), ("A_vs_2", "image_A vs image_2"),
                       ("1_vs_2", "image_1 vs image_2")]:
        d = st[key]
        lines.append(f"{title}: отн. СКО (абс. RGB) = {100 * d['rel_rms_abs']:.3f} %, "
                     f"ср.|Δ| = {d['mean_255']:.2f}, max|Δ| = {d['max_255']} (из 255)")
    return "\n".join(lines)


def draw_spectra(fig, res: Result):
    """Графики спектрограмм: исходные (ЛР-3) и восстановленные (ЛР-4)."""
    fig.clear()
    n = len(res.light_spectra_orig)
    axes = fig.subplots(1, n + 1)
    for i in range(n):
        ax = axes[i]
        ax.plot(WAVELENGTHS, res.light_spectra_orig[i], "k-", lw=2, label="исходный")
        ax.plot(WAVELENGTHS, res.light_spectra_rec[i], "r--o", ms=3, label="восстановленный")
        ax.set_title(f"Источник {i + 1}: S(λ), 1/нм", fontsize=9)
        ax.set_xlabel("λ, нм")
    ax = axes[-1]
    ax.plot(WAVELENGTHS, res.kd_orig, "k-", lw=2, label="исходный")
    ax.plot(WAVELENGTHS, res.kd_rec, "r--o", ms=3, label="восстановленный")
    ax.set_title("Сфера: kd(λ)", fontsize=9)
    ax.set_xlabel("λ, нм")
    for ax in axes:
        ax.grid(alpha=0.3)
        ax.axhline(0, color="gray", lw=0.5)
        ax.tick_params(labelsize=8)
    axes[0].legend(fontsize=8)
    fig.tight_layout()


def spectra_table(res: Result) -> tuple[list[str], list[list[float]]]:
    header = ["λ, нм"]
    for n in range(1, len(res.light_spectra_orig) + 1):
        header += [f"S{n} исх.", f"S{n} восст.", f"ΔS{n}"]
    header += ["kd исх.", "kd восст.", "Δkd"]
    rows = []
    for k, lam in enumerate(WAVELENGTHS):
        row = [lam]
        for so, sr in zip(res.light_spectra_orig, res.light_spectra_rec):
            row += [so[k], sr[k], sr[k] - so[k]]
        row += [res.kd_orig[k], res.kd_rec[k], res.kd_rec[k] - res.kd_orig[k]]
        rows.append(row)
    return header, rows


def save_results(res: Result, folder: str) -> list[str]:
    from matplotlib.figure import Figure

    os.makedirs(folder, exist_ok=True)
    saved = []
    for name, img in [("image_A", res.image_A), ("image_1", res.image_1), ("image_2", res.image_2)]:
        path = os.path.join(folder, f"{name}.png")
        Image.fromarray(img).save(path)
        saved.append(path)

    fig = Figure(figsize=(12, 3.8), dpi=110)
    draw_spectra(fig, res)
    path = os.path.join(folder, "spectra.png")
    fig.savefig(path)
    saved.append(path)

    header, rows = spectra_table(res)
    path = os.path.join(folder, "spectra_table.csv")
    with open(path, "w", newline="", encoding="utf-8") as f:
        w = csv.writer(f, delimiter=";")
        w.writerow(header)
        for row in rows:
            w.writerow([f"{row[0]:.0f}"] + [f"{v:.6g}" for v in row[1:]])
    saved.append(path)

    path = os.path.join(folder, "results.txt")
    with open(path, "w", encoding="utf-8") as f:
        f.write(text_summary(res) + "\n")
    saved.append(path)
    return saved


if __name__ == "__main__":
    here = os.path.dirname(os.path.abspath(__file__))
    scenarios = {
        "white": Scene(),  # оба источника белые (D65) - для сравнения с ЛР-3
        "colored": Scene(lights=[
            Light(-1500.0, 1000.0, 2000.0, 1000.0, "A (лампа накаливания, 2856 K)", 2856),
            Light(1500.0, -500.0, 1200.0, 800.0, "Цветной гаусс, λ0 [нм]", 470),
        ]),
    }
    scenarios["colored-sphere"] = Scene(sphere_kind=SPHERE_KINDS[1], sphere_lam0=600,
                                        lights=scenarios["colored"].lights)
    for name, scene in scenarios.items():
        for basis_name in BASES:
            scene.basis_name = basis_name
            res = run_lab(scene)
            tag = {"П": "box", "Г": "gauss", "Д": "delta"}[basis_name[0]]
            folder = os.path.join(here, "results", f"{name}_{tag}")
            save_results(res, folder)
            print(f"--- {name} / {basis_name} -> {folder}")
            print(text_summary(res), "\n")
