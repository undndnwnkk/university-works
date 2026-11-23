# Лабораторная работа №3 — линейная регрессия

Множественная линейная регрессия по методу наименьших квадратов на датасете
Student Performance (вариант для нечётного номера в группе). Коэффициенты
находятся решением нормальных уравнений `(XᵀX)B = XᵀY` собственным методом
Гаусса; из библиотек используются только NumPy и Pandas (matplotlib — для
графиков).

## Состав

- `linreg.py` — МНК (`solve_gauss`, `LinearRegressionOLS`), R², adj. R², RMSE,
  разбиение train/test, z-нормировка, пересчёт коэффициентов в исходные единицы;
- `main.py` — статистика и графики, предобработка, 3 модели + бонусная,
  сравнение; пишет `results.md` и `figures/`;
- `test_linreg.py` — 11 тестов (в т. ч. пример из методички и сверка с `np.linalg.lstsq`);
- `data/Student_Performance.csv` — датасет (распакован из `archive (2).zip`);
- `report/report.pdf` — отчёт (исходник `report/report.html`);
- `REPORT.md` — краткое изложение для защиты;
- `EXPLANATION.md` — объяснение простым языком, определения и вопросы к защите.

## Запуск

```bash
python3 main.py           # results.md + figures/
python3 -m unittest -v    # тесты
```

PDF отчёта пересобирается так:

```bash
cd report && google-chrome --headless=new --no-pdf-header-footer \
  --allow-file-access-from-files --print-to-pdf=report.pdf "file://$PWD/report.html"
```

## Результаты (R² на тесте)

| Модель | Признаки | R² |
|---|---|---|
| 1 | все 5 | **0.9885** |
| 2 | Hours, Previous | 0.9851 |
| 3 | всё, кроме Previous | 0.1371 |
| 4 (бонус) | все + Hours·Previous, Hours² | 0.9885 |
