from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

from linreg import (LinearRegressionOLS, StandardScaler, adjusted_r2,
                    destandardize, r2_score, rmse, train_test_split)

ROOT = Path(__file__).resolve().parent
DATA = ROOT / "data" / "Student_Performance.csv"
FIG = ROOT / "figures"
RESULTS = ROOT / "results.md"
TARGET = "Performance Index"
SEED = 42

SHORT = {
    "Hours Studied": "Hours",
    "Previous Scores": "Previous",
    "Extracurricular Activities": "Extracurricular",
    "Sleep Hours": "Sleep",
    "Sample Question Papers Practiced": "Papers",
    "Performance Index": "Performance",
}

BASE = ["Hours", "Previous", "Extracurricular", "Sleep", "Papers"]
MODELS = {
    "Модель 1 — все признаки": BASE,
    "Модель 2 — Hours + Previous": ["Hours", "Previous"],
    "Модель 3 — без Previous": ["Hours", "Extracurricular", "Sleep", "Papers"],
}
BONUS = {
    "Модель 4 (бонус) — все + синтетические":
        BASE + ["Hours_x_Previous", "Hours_sq"],
}

out = []


def md_table(df, floatfmt="{:.4f}"):
    cols = [str(c) for c in df.columns]
    lines = ["| " + " | ".join([""] + cols) + " |",
             "|" + "---|" * (len(cols) + 1)]
    for idx, *row in df.itertuples():
        cells = [floatfmt.format(v) if isinstance(v, (float, np.floating))
                 else str(v) for v in row]
        lines.append("| " + " | ".join([str(idx)] + cells) + " |")
    return "\n".join(lines)


def log(text=""):
    print(text)
    out.append(text)


def load():
    df = pd.read_csv(DATA).rename(columns=SHORT)
    log("# Результаты лабораторной работы №3\n")
    log(f"Файл: `{DATA.relative_to(ROOT)}`, строк: {len(df)}, "
        f"столбцов: {df.shape[1]}\n")
    return df


def statistics(df):
    log("## Статистика по датасету\n")
    num = df.select_dtypes("number")
    stats = num.describe(percentiles=[0.05, 0.25, 0.5, 0.75, 0.95]).T
    stats.index.name = None
    log(md_table(stats, "{:.2f}") + "\n")

    cat = df["Extracurricular"].value_counts()
    log("Категориальный признак `Extracurricular`: "
        + ", ".join(f"{k} — {v}" for k, v in cat.items()) + "\n")

    fig, ax = plt.subplots(figsize=(11, 2.6))
    ax.axis("off")
    t = ax.table(cellText=stats.round(2).values, rowLabels=stats.index,
                 colLabels=stats.columns, loc="center", cellLoc="center")
    t.auto_set_font_size(False)
    t.set_fontsize(9)
    t.scale(1, 1.4)
    ax.set_title("Описательная статистика числовых признаков")
    fig.tight_layout()
    fig.savefig(FIG / "01_stats_table.png", dpi=150)
    plt.close(fig)

    fig, axes = plt.subplots(2, 3, figsize=(13, 7))
    for ax, col in zip(axes.flat, num.columns):
        ax.hist(num[col], bins=min(30, num[col].nunique()),
                color="#4C72B0", edgecolor="white")
        ax.axvline(num[col].mean(), color="#C44E52", lw=2, label="среднее")
        ax.axvline(num[col].median(), color="#55A868", lw=2, ls="--",
                   label="медиана")
        ax.set_title(col)
    axes.flat[0].legend()
    fig.suptitle("Распределения числовых признаков")
    fig.tight_layout()
    fig.savefig(FIG / "02_histograms.png", dpi=150)
    plt.close(fig)

    fig, ax = plt.subplots(figsize=(10, 4.5))
    z = (num - num.mean()) / num.std()
    ax.boxplot([z[c] for c in z.columns], whis=(0, 100))
    ax.set_xticks(range(1, len(z.columns) + 1), z.columns)
    ax.set_ylabel("z-оценка")
    ax.set_title("Ящики с усами: min, Q1, медиана, Q3, max (после z-нормировки)")
    ax.grid(axis="y", alpha=0.3)
    fig.tight_layout()
    fig.savefig(FIG / "03_boxplots.png", dpi=150)
    plt.close(fig)

    fig, axes = plt.subplots(1, 2, figsize=(10, 4))
    axes[0].bar(cat.index, cat.values, color=["#4C72B0", "#DD8452"])
    axes[0].set_title("Extracurricular: количество")
    groups = [df.loc[df["Extracurricular"] == k, TARGET.split()[0]]
              for k in cat.index]
    axes[1].boxplot(groups)
    axes[1].set_xticks(range(1, len(cat) + 1), cat.index)
    axes[1].set_title("Performance по группам Extracurricular")
    fig.tight_layout()
    fig.savefig(FIG / "04_categorical.png", dpi=150)
    plt.close(fig)


def preprocess(df):
    log("## Предобработка\n")
    df = df.copy()

    missing = df.isna().sum()
    log(f"- Пропусков всего: {int(missing.sum())}. Числовые пропуски "
        "заполняются медианой, категориальные — модой.")
    for col in df.columns:
        if df[col].isna().any():
            fill = (df[col].median() if pd.api.types.is_numeric_dtype(df[col])
                    else df[col].mode()[0])
            df[col] = df[col].fillna(fill)

    dups = int(df.duplicated().sum())
    df = df.drop_duplicates().reset_index(drop=True)
    log(f"- Полных дубликатов удалено: {dups}, осталось строк: {len(df)}.")

    df["Extracurricular"] = df["Extracurricular"].map({"No": 0, "Yes": 1})
    if df["Extracurricular"].isna().any():
        raise ValueError("неожиданное значение в Extracurricular")
    log("- `Extracurricular` закодирован: No → 0, Yes → 1 (бинарное кодирование).")

    df["Hours_x_Previous"] = df["Hours"] * df["Previous"]
    df["Hours_sq"] = df["Hours"] ** 2
    log("- Синтетические признаки: `Hours_x_Previous` = Hours · Previous, "
        "`Hours_sq` = Hours².")
    log("- Нормировка: z-score, среднее и std считаются только по "
        "обучающей выборке и применяются к тестовой.\n")

    corr = df.drop(columns=["Hours_x_Previous", "Hours_sq"]).corr()
    fig, ax = plt.subplots(figsize=(7, 6))
    im = ax.imshow(corr, cmap="coolwarm", vmin=-1, vmax=1)
    ax.set_xticks(range(len(corr)), corr.columns, rotation=40, ha="right")
    ax.set_yticks(range(len(corr)), corr.columns)
    for i in range(len(corr)):
        for j in range(len(corr)):
            ax.text(j, i, f"{corr.iloc[i, j]:.2f}", ha="center", va="center",
                    fontsize=9)
    fig.colorbar(im)
    ax.set_title("Корреляционная матрица (Пирсон)")
    fig.tight_layout()
    fig.savefig(FIG / "05_correlation.png", dpi=150)
    plt.close(fig)

    log("Корреляция признаков с целевой переменной:\n")
    c = corr[TARGET.split()[0]].drop(TARGET.split()[0]).sort_values(
        key=abs, ascending=False).to_frame("r")
    log(md_table(c) + "\n")

    target = TARGET.split()[0]
    fig, axes = plt.subplots(1, 5, figsize=(18, 3.8), sharey=True)
    sample = df.sample(1500, random_state=SEED)
    for ax, col in zip(axes, BASE):
        jitter = np.random.default_rng(SEED).normal(0, 0.08, len(sample))
        ax.scatter(sample[col] + jitter, sample[target], s=4, alpha=0.4)
        ax.set_xlabel(col)
    axes[0].set_ylabel(target)
    fig.suptitle("Зависимость Performance от каждого признака (1500 случайных точек)")
    fig.tight_layout()
    fig.savefig(FIG / "06_scatter.png", dpi=150)
    plt.close(fig)
    return df


def run_models(df):
    target = TARGET.split()[0]
    all_feats = BASE + ["Hours_x_Previous", "Hours_sq"]
    x = df[all_feats].to_numpy(float)
    y = df[target].to_numpy(float)
    x_tr, x_te, y_tr, y_te = train_test_split(x, y, test_size=0.2, seed=SEED)
    log("## Разбиение\n")
    log(f"Обучающая выборка: {len(y_tr)} строк, тестовая: {len(y_te)} "
        f"(80/20, seed={SEED}).\n")

    results = {}
    for name, feats in {**MODELS, **BONUS}.items():
        idx = [all_feats.index(f) for f in feats]
        scaler = StandardScaler().fit(x_tr[:, idx])
        a, b = scaler.transform(x_tr[:, idx]), scaler.transform(x_te[:, idx])
        model = LinearRegressionOLS().fit(a, y_tr)
        p_tr, p_te = model.predict(a), model.predict(b)
        r2_te = r2_score(y_te, p_te)
        b0, raw = destandardize(model, scaler)
        results[name] = dict(
            feats=feats, model=model, pred=p_te, b0=b0, raw=raw,
            r2_train=r2_score(y_tr, p_tr), r2_test=r2_te,
            adj=adjusted_r2(r2_te, len(y_te), len(feats)),
            rmse=rmse(y_te, p_te))

        log(f"### {name}\n")
        log(f"Признаки: {', '.join(f'`{f}`' for f in feats)}\n")
        eq = f"{target} = {b0:.3f} " + " ".join(
            f"{'+' if c >= 0 else '−'} {abs(c):.4f}·{f}"
            for f, c in zip(feats, raw))
        log(f"Уравнение в исходных единицах: `{eq}`\n")
        coef = pd.DataFrame(
            {"β (норм.)": model.coef_, "β (исх. ед.)": raw}, index=feats)
        coef = coef.reindex(coef["β (норм.)"].abs().sort_values(
            ascending=False).index)
        log(md_table(coef) + "\n")
        log(f"R² train = {results[name]['r2_train']:.4f}, "
            f"R² test = {r2_te:.4f}, adj. R² test = {results[name]['adj']:.4f}, "
            f"RMSE test = {results[name]['rmse']:.3f}\n")

    log("## Сравнение моделей\n")
    cmp = pd.DataFrame({
        "признаков": [str(len(r["feats"])) for r in results.values()],
        "R² train": [r["r2_train"] for r in results.values()],
        "R² test": [r["r2_test"] for r in results.values()],
        "adj. R² test": [r["adj"] for r in results.values()],
        "RMSE test": [r["rmse"] for r in results.values()],
    }, index=list(results))
    log(md_table(cmp) + "\n")

    log("## Однофакторные модели (R² на тесте)\n")
    single = {}
    for f in all_feats:
        i = all_feats.index(f)
        s = StandardScaler().fit(x_tr[:, [i]])
        m = LinearRegressionOLS().fit(s.transform(x_tr[:, [i]]), y_tr)
        single[f] = r2_score(y_te, m.predict(s.transform(x_te[:, [i]])))
    single = pd.Series(single).sort_values(ascending=False)
    log(md_table(single.to_frame("R² test")) + "\n")

    plots(results, y_te, single)
    return results


def plots(results, y_te, single):
    names = list(results)
    short = [n.split(" — ")[0] for n in names]

    fig, axes = plt.subplots(1, len(names), figsize=(4.6 * len(names), 4.4))
    for ax, n, s in zip(axes, names, short):
        r = results[n]
        ax.scatter(y_te, r["pred"], s=4, alpha=0.35)
        lo, hi = y_te.min(), y_te.max()
        ax.plot([lo, hi], [lo, hi], color="#C44E52", lw=1.5)
        ax.set_title(f"{s}: R² = {r['r2_test']:.4f}")
        ax.set_xlabel("факт")
        ax.set_ylabel("прогноз")
    fig.suptitle("Прогноз против факта на тестовой выборке")
    fig.tight_layout()
    fig.savefig(FIG / "07_pred_vs_true.png", dpi=150)
    plt.close(fig)

    fig, axes = plt.subplots(1, len(names), figsize=(4.6 * len(names), 3.6),
                             sharey=True)
    for ax, n, s in zip(axes, names, short):
        res = y_te - results[n]["pred"]
        ax.hist(res, bins=40, color="#8172B2", edgecolor="white")
        ax.set_title(f"{s}: σ остатков = {res.std():.2f}")
        ax.set_xlabel("y − ŷ")
    fig.suptitle("Распределение остатков на тестовой выборке")
    fig.tight_layout()
    fig.savefig(FIG / "08_residuals.png", dpi=150)
    plt.close(fig)

    fig, ax = plt.subplots(figsize=(8, 4))
    vals = [results[n]["r2_test"] for n in names]
    bars = ax.bar(short, vals, color=["#4C72B0", "#55A868", "#DD8452",
                                      "#8172B2"][:len(names)])
    for b, v in zip(bars, vals):
        ax.text(b.get_x() + b.get_width() / 2, v + 0.01, f"{v:.4f}",
                ha="center")
    ax.set_ylim(0, 1.08)
    ax.set_ylabel("R² (тест)")
    ax.set_title("Сравнение моделей по коэффициенту детерминации")
    fig.tight_layout()
    fig.savefig(FIG / "09_r2_comparison.png", dpi=150)
    plt.close(fig)

    fig, axes = plt.subplots(1, len(names), figsize=(4.6 * len(names), 3.8))
    for ax, n, s in zip(axes, names, short):
        r = results[n]
        order = np.argsort(np.abs(r["model"].coef_))
        ax.barh(np.array(r["feats"])[order], r["model"].coef_[order],
                color="#4C72B0")
        ax.set_title(s)
        ax.axvline(0, color="black", lw=0.8)
    fig.suptitle("Нормированные коэффициенты (изменение Performance на 1σ признака)")
    fig.tight_layout()
    fig.savefig(FIG / "10_coefficients.png", dpi=150)
    plt.close(fig)

    fig, ax = plt.subplots(figsize=(8, 3.8))
    ax.barh(single.index[::-1], single.values[::-1], color="#55A868")
    for i, v in enumerate(single.values[::-1]):
        ax.text(max(v, 0) + 0.01, i, f"{v:.3f}", va="center")
    ax.set_xlim(min(0, single.min()) - 0.02, 1.05)
    ax.set_xlabel("R² (тест)")
    ax.set_title("Однофакторные модели: объяснительная сила каждого признака")
    fig.tight_layout()
    fig.savefig(FIG / "11_single_feature_r2.png", dpi=150)
    plt.close(fig)


def main():
    FIG.mkdir(exist_ok=True)
    df = load()
    statistics(df)
    df = preprocess(df)
    run_models(df)
    RESULTS.write_text("\n".join(out) + "\n", encoding="utf-8")
    print(f"\nСохранено: {RESULTS.name}, графики в {FIG.name}/")


if __name__ == "__main__":
    main()
