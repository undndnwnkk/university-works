import unittest

import numpy as np

from linreg import (LinearRegressionOLS, StandardScaler, destandardize,
                    r2_score, solve_gauss, train_test_split)


class TestGauss(unittest.TestCase):
    def test_matches_numpy(self):
        rng = np.random.default_rng(0)
        a = rng.normal(size=(6, 6))
        b = rng.normal(size=6)
        np.testing.assert_allclose(solve_gauss(a, b), np.linalg.solve(a, b),
                                   atol=1e-10)

    def test_needs_pivoting(self):
        a = [[0, 1], [1, 1]]
        np.testing.assert_allclose(solve_gauss(a, [2, 3]), [1, 2])

    def test_singular(self):
        with self.assertRaises(np.linalg.LinAlgError):
            solve_gauss([[1, 2], [2, 4]], [1, 2])


class TestOLS(unittest.TestCase):
    def test_example_from_lecture(self):
        x = np.array([[40, 60], [55, 36], [45, 36], [30, 15], [30, 90]])
        s = np.array([3, 6, 5, 3.5, 1.5])
        m = LinearRegressionOLS().fit(x, s)
        self.assertAlmostEqual(m.intercept_, 0.2787, places=4)
        np.testing.assert_allclose(m.coef_, [0.1229, -0.0294], atol=1e-4)

    def test_exact_recovery(self):
        rng = np.random.default_rng(1)
        x = rng.normal(size=(200, 3))
        y = 4 + x @ np.array([1.5, -2.0, 0.5])
        m = LinearRegressionOLS().fit(x, y)
        self.assertAlmostEqual(m.intercept_, 4)
        np.testing.assert_allclose(m.coef_, [1.5, -2.0, 0.5], atol=1e-10)
        self.assertAlmostEqual(r2_score(y, m.predict(x)), 1.0)

    def test_matches_lstsq_on_noise(self):
        rng = np.random.default_rng(2)
        x = rng.normal(size=(500, 4))
        y = x @ np.array([1, 2, 3, 4]) + rng.normal(size=500)
        m = LinearRegressionOLS().fit(x, y)
        xd = np.hstack([np.ones((500, 1)), x])
        ref = np.linalg.lstsq(xd, y, rcond=None)[0]
        np.testing.assert_allclose([m.intercept_, *m.coef_], ref, atol=1e-9)

    def test_destandardize(self):
        rng = np.random.default_rng(3)
        x = rng.normal(10, 5, size=(100, 2))
        y = 7 + x @ np.array([3.0, -1.0])
        sc = StandardScaler().fit(x)
        m = LinearRegressionOLS().fit(sc.transform(x), y)
        b0, b = destandardize(m, sc)
        self.assertAlmostEqual(b0, 7)
        np.testing.assert_allclose(b, [3, -1], atol=1e-10)

    def test_predict_before_fit(self):
        with self.assertRaises(RuntimeError):
            LinearRegressionOLS().predict([[1.0]])


class TestHelpers(unittest.TestCase):
    def test_r2_mean_prediction_is_zero(self):
        y = np.array([1.0, 2, 3, 4])
        self.assertAlmostEqual(r2_score(y, np.full(4, y.mean())), 0.0)

    def test_split_sizes_and_disjoint(self):
        x = np.arange(100).reshape(-1, 1)
        y = np.arange(100)
        a, b, ya, yb = train_test_split(x, y, test_size=0.2, seed=0)
        self.assertEqual((len(ya), len(yb)), (80, 20))
        self.assertEqual(set(ya) | set(yb), set(range(100)))
        self.assertFalse(set(ya) & set(yb))

    def test_scaler_uses_train_stats(self):
        sc = StandardScaler().fit(np.array([[0.0], [2.0]]))
        np.testing.assert_allclose(sc.transform([[4.0]]), [[3.0]])


if __name__ == "__main__":
    unittest.main()
