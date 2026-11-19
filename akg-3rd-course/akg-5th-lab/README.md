# АКГ, ЛР-5 — Формирование изображения методом трассировки путей

Выполнили: Коршун Артем, Быков Лев (P3319). Проверил: Жданов Дмитрий Дмитриевич.

Обратная трассировка путей с локальным освещением: сцена из треугольников (в т.ч. OBJ),
своя ускоряющая структура BVH (SAH) + готовая библиотека Intel Embree (`embreex`),
Ламберт / Фонг / Блинн–Фонг, зеркальное отражение, преломление, тени, антиалиасинг,
нормировка, гамма 2.2, вывод в PPM.

## Запуск

```bash
python3 -m venv .venv
.venv/bin/pip install -r requirements.txt
.venv/bin/python main.py scenes/room.json                     # интерактивный режим (help — список команд)
.venv/bin/python main.py scenes/room.json --render            # построить и выйти
.venv/bin/python main.py scenes/outdoor.json --set render.spp=4 --set render.backend=bvh --render
.venv/bin/python main.py scenes/room.json --commands tests/demo_commands.txt
.venv/bin/python verify.py                                    # доказательство корректности -> output/verify.txt
.venv/bin/python experiments.py                               # картинки и таблицы для отчёта -> report/
```

Первый запуск дольше на ~10 с: `numba` компилирует функции пересечения (потом берёт из кэша).

## Состав

* `main.py` — консольный интерфейс; `scene.py` — сцена, треугольники, OBJ; `accel.py` — Мёллер–Трумбор,
  BVH (SAH), перебор, Embree; `render.py` — камера, освещение, тени, отражение/преломление, тон-маппинг, PPM.
* `verify.py` — проверки корректности; `experiments.py` — иллюстрации и замеры; `make_models.py` — генерация `models/knot.obj`.
* `scenes/` — сцены в JSON; `tests/demo_commands.txt` — пример ввода команд из файла.
* `report/` — отчёт: `report.tex` (+ картинки и `code/` рядом, собирается pdfLaTeX в Overleaf), `report.pdf`.
* `EXPLANATION.md` — объяснение лабы простым языком, термины, вопросы к защите.
