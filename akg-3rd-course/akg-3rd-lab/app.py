"""
Графический интерфейс ЛР-4 (tkinter).

Слева - ввод параметров сцены, справа - вкладки с результатами:
  * «Изображения»      - image_A (ЛР-4) и image_1 / image_2 (ЛР-3), щелчок по image_A
                          показывает яркость в пикселе;
  * «Спектрограммы»    - графики исходных и восстановленных спектров;
  * «Таблица спектров» - те же спектры в числах;
  * «Числа»            - RGB-сцена, коэффициенты, яркость в точках, max/min, сравнения.

Запуск:  python app.py
Все расчёты выполняет модуль lab4.py.
"""
import os
import tkinter as tk
from tkinter import filedialog, messagebox, ttk
from tkinter.scrolledtext import ScrolledText

from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg, NavigationToolbar2Tk
from matplotlib.figure import Figure
from PIL import Image, ImageTk

import lab4

PREVIEW = 330  # начальный размер превью изображений, пикс (подстраивается под окно)


class LabApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("АКГ, ЛР-4: моделирование цветных изображений (построение спектра по цвету)")
        self.geometry("1500x900")
        self.result = None
        self._photos = {}  # ссылки на картинки, иначе tkinter их «потеряет»
        self.preview = PREVIEW

        self.vars = {}  # имя параметра -> tk-переменная
        left = ttk.Frame(self, padding=8)
        left.pack(side=tk.LEFT, fill=tk.Y)
        right = ttk.Frame(self, padding=(0, 8, 8, 8))
        right.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

        self._build_inputs(left)
        self._build_outputs(right)

        self.status = tk.StringVar(value="Задайте параметры и нажмите «Рассчитать».")
        ttk.Label(self, textvariable=self.status, relief=tk.SUNKEN, anchor="w",
                  padding=3).pack(side=tk.BOTTOM, fill=tk.X, before=left)

    # ------------------------------------------------------------------ ввод
    def _field(self, parent, key, label, value, row, col):
        """Подпись + поле ввода в сетке (по две пары в строке)."""
        var = tk.StringVar(value=str(value))
        self.vars[key] = var
        ttk.Label(parent, text=label).grid(row=row, column=2 * col, sticky="e", padx=(4, 2), pady=2)
        ttk.Entry(parent, textvariable=var, width=9).grid(row=row, column=2 * col + 1, sticky="w", pady=2)

    def _combo(self, parent, key, label, values, value, row):
        var = tk.StringVar(value=value)
        self.vars[key] = var
        ttk.Label(parent, text=label).grid(row=row, column=0, sticky="e", padx=(4, 2), pady=2)
        cb = ttk.Combobox(parent, textvariable=var, values=values, state="readonly", width=28)
        cb.grid(row=row, column=1, columnspan=3, sticky="we", pady=2)
        return cb

    def _build_inputs(self, parent):
        d = lab4.Scene()  # значения по умолчанию

        f = ttk.LabelFrame(parent, text="Экран и наблюдатель", padding=4)
        f.pack(fill=tk.X, pady=3)
        self._field(f, "W", "W, мм", d.W, 0, 0)
        self._field(f, "H", "H, мм", d.H, 0, 1)
        self._field(f, "Wres", "Wres, пикс", d.Wres, 1, 0)
        self._field(f, "zO", "zO, мм", d.zO, 1, 1)
        ttk.Label(f, text="Hres = Wres·H/W (квадратные пиксели).\n"
                          "Наблюдатель (0, 0, zO) смотрит вдоль −Z,\nэкран в плоскости z = 0.",
                  foreground="gray").grid(row=2, column=0, columnspan=4, sticky="w")

        f = ttk.LabelFrame(parent, text="Сфера (ламбертовский отражатель)", padding=4)
        f.pack(fill=tk.X, pady=3)
        self._field(f, "xC", "xC, мм", d.xC, 0, 0)
        self._field(f, "yC", "yC, мм", d.yC, 0, 1)
        self._field(f, "zC", "zC, мм", d.zC, 1, 0)
        self._field(f, "r", "r, мм", d.r, 1, 1)
        self._field(f, "rho", "ρ (0..1)", d.rho, 2, 0)
        self._field(f, "sphere_lam0", "λ0, нм", d.sphere_lam0, 2, 1)
        self._combo(f, "sphere_kind", "kd(λ)", lab4.SPHERE_KINDS, d.sphere_kind, 3)

        for n, L in enumerate(d.lights, 1):
            f = ttk.LabelFrame(parent, text=f"Источник {n} (ламбертовский, ось вниз −Z)", padding=4)
            f.pack(fill=tk.X, pady=3)
            self._field(f, f"L{n}x", "x, мм", L.x, 0, 0)
            self._field(f, f"L{n}y", "y, мм", L.y, 0, 1)
            self._field(f, f"L{n}z", "z, мм", L.z, 1, 0)
            self._field(f, f"L{n}i0", "I0, Вт/ср", L.i0, 1, 1)
            self._combo(f, f"L{n}kind", "Спектр", lab4.LIGHT_KINDS, L.kind, 2)
            self._field(f, f"L{n}param", "T, K / λ0, нм", L.param, 3, 0)

        f = ttk.LabelFrame(parent, text="Построение спектра по цвету", padding=4)
        f.pack(fill=tk.X, pady=3)
        self._combo(f, "basis", "Базис F1..F3", list(lab4.BASES), d.basis_name, 0)
        self.vars["gamma"] = tk.BooleanVar(value=d.gamma)
        self.vars["white_balance"] = tk.BooleanVar(value=d.white_balance)
        ttk.Checkbutton(f, text="гамма-коррекция 1/2.2", variable=self.vars["gamma"]) \
            .grid(row=1, column=0, columnspan=4, sticky="w")
        ttk.Checkbutton(f, text="баланс белого по D65 (белое → серое)",
                        variable=self.vars["white_balance"]).grid(row=2, column=0, columnspan=4, sticky="w")

        b = ttk.Frame(parent)
        b.pack(fill=tk.X, pady=6)
        ttk.Button(b, text="Рассчитать", command=self.calculate).pack(fill=tk.X, pady=2)
        ttk.Button(b, text="Белые источники (D65) — для сравнения с ЛР-3",
                   command=self.set_white_lights).pack(fill=tk.X, pady=2)
        ttk.Button(b, text="Сохранить результаты в папку…", command=self.save).pack(fill=tk.X, pady=2)

    def read_scene(self) -> lab4.Scene:
        """Собирает объект Scene из полей ввода (с проверкой, что это числа)."""
        v = self.vars

        def num(key):
            text = v[key].get().strip().replace(",", ".")
            try:
                return float(text)
            except ValueError:
                raise ValueError(f"Поле «{key}»: «{text}» - не число")

        lights = [lab4.Light(num(f"L{n}x"), num(f"L{n}y"), num(f"L{n}z"), num(f"L{n}i0"),
                             v[f"L{n}kind"].get(), num(f"L{n}param")) for n in (1, 2)]
        return lab4.Scene(
            W=num("W"), H=num("H"), Wres=int(num("Wres")), zO=num("zO"),
            xC=num("xC"), yC=num("yC"), zC=num("zC"), r=num("r"), rho=num("rho"),
            sphere_kind=v["sphere_kind"].get(), sphere_lam0=num("sphere_lam0"),
            lights=lights, basis_name=v["basis"].get(),
            gamma=v["gamma"].get(), white_balance=v["white_balance"].get())

    def set_white_lights(self):
        for n in (1, 2):
            self.vars[f"L{n}kind"].set(lab4.LIGHT_KINDS[0])
        self.vars["sphere_kind"].set(lab4.SPHERE_KINDS[0])
        self.calculate()

    # ----------------------------------------------------------------- вывод
    def _build_outputs(self, parent):
        nb = ttk.Notebook(parent)
        nb.pack(fill=tk.BOTH, expand=True)

        # Вкладка 1: изображения
        tab = ttk.Frame(nb, padding=6)
        nb.add(tab, text="Изображения")
        tab.bind("<Configure>", self.on_resize)
        titles = {"image_A": "image_A (ЛР-4: RGB → спектр → RGB)",
                  "image_1": "image_1 (ЛР-3: спектральный расчёт)",
                  "image_2": "image_2 (ЛР-3: RGB-расчёт)"}
        self.img_labels = {}
        for col, (key, title) in enumerate(titles.items()):
            ttk.Label(tab, text=title, font=("TkDefaultFont", 10, "bold")).grid(row=0, column=col, pady=3)
            lbl = tk.Label(tab, bg="black", cursor="crosshair")
            lbl.grid(row=1, column=col, padx=4)
            self.img_labels[key] = lbl
        self.img_labels["image_A"].bind("<Button-1>", self.on_image_click)
        self.pixel_info = tk.StringVar(value="Щёлкните по image_A, чтобы узнать яркость в пикселе.")
        self.pixel_label = ttk.Label(tab, textvariable=self.pixel_info, foreground="#004080",
                                     wraplength=3 * PREVIEW)
        self.pixel_label.grid(row=2, column=0, columnspan=3, sticky="w", pady=6)
        self.compare_text = ScrolledText(tab, height=14, font=("Monospace", 10), wrap=tk.WORD)
        self.compare_text.grid(row=3, column=0, columnspan=3, sticky="nsew")
        tab.rowconfigure(3, weight=1)
        for c in range(3):
            tab.columnconfigure(c, weight=1)

        # Вкладка 2: графики спектрограмм
        tab = ttk.Frame(nb)
        nb.add(tab, text="Спектрограммы")
        self.fig = Figure(figsize=(11, 4.5), dpi=100)
        self.canvas = FigureCanvasTkAgg(self.fig, master=tab)
        NavigationToolbar2Tk(self.canvas, tab)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

        # Вкладка 3: таблица спектров
        tab = ttk.Frame(nb)
        nb.add(tab, text="Таблица спектров")
        self.table = ttk.Treeview(tab, show="headings")
        sb = ttk.Scrollbar(tab, orient=tk.VERTICAL, command=self.table.yview)
        self.table.configure(yscrollcommand=sb.set)
        sb.pack(side=tk.RIGHT, fill=tk.Y)
        self.table.pack(fill=tk.BOTH, expand=True)

        # Вкладка 4: все числа
        tab = ttk.Frame(nb)
        nb.add(tab, text="Числа")
        self.summary = ScrolledText(tab, font=("Monospace", 10), wrap=tk.WORD)
        self.summary.pack(fill=tk.BOTH, expand=True)

    def calculate(self):
        try:
            scene = self.read_scene()
            self.status.set("Считаю...")
            self.config(cursor="watch")
            self.update_idletasks()
            self.result = lab4.run_lab(scene)
        except ValueError as e:
            messagebox.showerror("Неверные параметры", str(e))
            self.status.set("Ошибка во входных данных.")
            return
        finally:
            self.config(cursor="")
        self.show_result()
        sc = self.result.scene
        self.status.set(f"Готово: {sc.Wres}×{sc.Hres} пикс, пиксель {sc.pixel:.3f} мм, "
                        f"базис «{sc.basis_name}».")

    def on_resize(self, event):
        """Подгоняем размер превью под ширину окна (три картинки в ряд)."""
        size = max(120, min(420, (event.width - 40) // 3))
        if abs(size - self.preview) > 8:
            self.preview = size
            self.pixel_label.configure(wraplength=3 * size)
            self.show_images()

    def show_images(self):
        if self.result is None:
            return
        for key in ("image_A", "image_1", "image_2"):
            img = Image.fromarray(getattr(self.result, key))
            img.thumbnail((self.preview, self.preview), Image.LANCZOS)
            self._photos[key] = ImageTk.PhotoImage(img)
            self.img_labels[key].configure(image=self._photos[key])

    def show_result(self):
        res = self.result
        self.show_images()

        text = lab4.text_summary(res)
        # в первую вкладку - только самое важное: сравнение и яркость
        short = text[text.index("=== Яркость в трёх"):]
        for widget, content in ((self.compare_text, short), (self.summary, text)):
            widget.delete("1.0", tk.END)
            widget.insert(tk.END, content)

        lab4.draw_spectra(self.fig, res)
        self.canvas.draw()

        header, rows = lab4.spectra_table(res)
        self.table.delete(*self.table.get_children())
        self.table["columns"] = header
        for h in header:
            self.table.heading(h, text=h)
            self.table.column(h, width=80, anchor="e")
        for row in rows:
            self.table.insert("", tk.END, values=[f"{row[0]:.0f}"] + [f"{x:.5g}" for x in row[1:]])

    def on_image_click(self, event):
        if self.result is None:
            return
        res = self.result
        h, w = res.mask.shape
        shown = self._photos["image_A"]
        col = int(event.x * w / shown.width())
        row = int(event.y * h / shown.height())
        if not (0 <= row < h and 0 <= col < w):
            return
        if not res.mask[row, col]:
            self.pixel_info.set(f"Пиксель ({col}, {row}): фон, сфера сюда не попала.")
            return
        p = lab4.pixel_brightness(res, row, col)
        self.pixel_info.set(
            f"Пиксель ({col}, {row}):  Le = {p['Le']:.5g} Вт/(ср·м²),  Lv = {p['Lv']:.5g} кд/м²,  "
            f"RGB абс. = {lab4.fmt_vec(p['rgb_A'])},  RGB монитор = {tuple(int(x) for x in p['monitor_A'])};  "
            f"Lv в image_1 = {p['Lv_1']:.5g} кд/м²")

    def save(self):
        if self.result is None:
            messagebox.showinfo("Нет результата", "Сначала нажмите «Рассчитать».")
            return
        start = os.path.join(os.path.dirname(os.path.abspath(__file__)), "results")
        folder = filedialog.askdirectory(initialdir=start if os.path.isdir(start) else None,
                                         title="Куда сохранить image_A, image_1, image_2, таблицы")
        if folder:
            files = lab4.save_results(self.result, folder)
            messagebox.showinfo("Сохранено", "\n".join(files))


if __name__ == "__main__":
    app = LabApp()
    app.after(100, app.calculate)  # сразу показать расчёт для значений по умолчанию
    app.mainloop()
