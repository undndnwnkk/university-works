# Лабораторная работа №1 — база знаний в Prolog

Тема базы знаний: **видеоигры, их свойства, жанры и разработчики**.

## Состав проекта

- `knowledge_base.pl` — база знаний: факты и правила с комментариями;
- `demo.pl` — 10 демонстрационных запросов разной сложности;
- `tests.pl` — автоматические тесты результатов;
- `REPORT.md` — краткое описание для показа и защиты работы;
- `ontology/` — часть 2: онтология для Protege (`video_games.ttl`),
  SPARQL-запросы и описание перевода (`ontology/README.md`);
- `report/report.pdf` — отчёт по шаблону модуля (исходник — `report/report.html`);
- `GUIDE.md` — объяснение Prolog и Protege с нуля, разбор файлов и вопросы к защите.

В базе знаний содержатся:

- 20 фактов `game/1` и дополнительные унарные факты-свойства;
- ровно 15 бинарных фактов: 7 `has_genre/2` и 8 `developed_by/2`;
- 7 правил логического вывода;
- примеры логических операций «и» (`,`), «или» (`;`) и «не» (`\+`).

## Запуск

Нужен [SWI-Prolog](https://www.swi-prolog.org/).

Запустить готовую демонстрацию всех запросов:

```bash
swipl -q -s demo.pl
```

Запустить автоматические тесты:

```bash
swipl -q -s tests.pl -g run_tests,halt
```

Открыть базу знаний в интерактивном режиме:

```bash
swipl -s knowledge_base.pl
```

После этого запросы вводятся в приглашении `?-`, например:

```prolog
?- game(portal_2).
true.

?- developed_by(Game, valve).
Game = portal_2 ;
Game = dota_2 ;
Game = half_life_2.

?- indie(Game), singleplayer(Game).
Game = hades ;
...

?- game(Game), \+ multiplayer(Game).
Game = the_witcher_3 ;
...

?- recommended_for_beginner(Game).
Game = portal_2 ;
Game = stardew_valley ;
Game = minecraft.
```

Клавиша `;` запрашивает следующее решение, `Enter` завершает текущий запрос.
