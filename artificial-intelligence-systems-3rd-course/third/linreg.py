import numpy as np


def solve_gauss(a, b):
    a = np.array(a, dtype=float)
    b = np.array(b, dtype=float).reshape(-1)
    n = a.shape[0]
    if a.shape != (n, n) or b.shape[0] != n:
        raise ValueError("ожидается квадратная матрица n x n и вектор длины n")

    m = np.hstack([a, b.reshape(-1, 1)])
    scale = np.abs(a).max() or 1.0
    eps = 1e-12 * scale

    for col in range(n):
        pivot = col + int(np.argmax(np.abs(m[col:, col])))
        if abs(m[pivot, col]) < eps:
            raise np.linalg.LinAlgError(
                "матрица вырождена: признаки линейно зависимы")
        if pivot != col:
            m[[col, pivot]] = m[[pivot, col]]
        for row in range(col + 1, n):
            factor = m[row, col] / m[col, col]
            m[row, col:] -= factor * m[col, col:]

    x = np.zeros(n)
    for row in range(n - 1, -1, -1):
        x[row] = (m[row, -1] - m[row, row + 1:n] @ x[row + 1:]) / m[row, row]
    return x


class LinearRegressionOLS:
    def __init__(self):
        self.intercept_ = None
        self.coef_ = None

    @staticmethod
    def _design(x):
        x = np.asarray(x, dtype=float)
        if x.ndim == 1:
            x = x.reshape(-1, 1)
        return np.hstack([np.ones((x.shape[0], 1)), x])

    def fit(self, x, y):
        xd = self._design(x)
        y = np.asarray(y, dtype=float).reshape(-1)
        if xd.shape[0] != y.shape[0]:
            raise ValueError("число строк X и длина y не совпадают")
        beta = solve_gauss(xd.T @ xd, xd.T @ y)
        self.intercept_ = beta[0]
        self.coef_ = beta[1:]
        return self

    def predict(self, x):
        if self.coef_ is None:
            raise RuntimeError("модель не обучена: сначала вызовите fit()")
        return self._design(x) @ np.concatenate([[self.intercept_], self.coef_])


def r2_score(y_true, y_pred):
    y_true = np.asarray(y_true, dtype=float)
    y_pred = np.asarray(y_pred, dtype=float)
    ss_res = np.sum((y_true - y_pred) ** 2)
    ss_tot = np.sum((y_true - y_true.mean()) ** 2)
    return 1.0 - ss_res / ss_tot


def adjusted_r2(r2, n, p):
    return 1.0 - (1.0 - r2) * (n - 1) / (n - p - 1)


def rmse(y_true, y_pred):
    y_true = np.asarray(y_true, dtype=float)
    y_pred = np.asarray(y_pred, dtype=float)
    return float(np.sqrt(np.mean((y_true - y_pred) ** 2)))


def train_test_split(x, y, test_size=0.2, seed=42):
    n = len(y)
    rng = np.random.default_rng(seed)
    idx = rng.permutation(n)
    n_test = int(round(n * test_size))
    test_idx, train_idx = idx[:n_test], idx[n_test:]
    return x[train_idx], x[test_idx], y[train_idx], y[test_idx]


class StandardScaler:
    def fit(self, x):
        x = np.asarray(x, dtype=float)
        self.mean_ = x.mean(axis=0)
        self.std_ = x.std(axis=0)
        self.std_[self.std_ == 0] = 1.0
        return self

    def transform(self, x):
        return (np.asarray(x, dtype=float) - self.mean_) / self.std_

    def fit_transform(self, x):
        return self.fit(x).transform(x)


def destandardize(model, scaler):
    coef = model.coef_ / scaler.std_
    intercept = model.intercept_ - np.sum(coef * scaler.mean_)
    return intercept, coef
