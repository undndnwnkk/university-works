# Часть 2. Онтология видеоигр в Protege

- `video_games.ttl` — онтология OWL 2 (Turtle), перевод `../knowledge_base.pl`;
- `queries.sparql` — 10 SPARQL-запросов для вкладки SPARQL Query.

Открыть: **Protege → File → Open → `video_games.ttl`**.

## Соответствие Prolog → OWL

| Prolog | OWL | Как переведено |
|---|---|---|
| `game/1` (20 фактов) | класс `Game` + 20 индивидов | каждый атом стал именованным индивидом |
| `singleplayer/1`, `indie/1`, `story_driven/1`, `cozy/1` | подклассы `Game` | унарный факт — принадлежность индивида классу |
| `multiplayer/1`, `competitive/1` | подклассы `Game` + `owl:oneOf` | список закрыт перечислением, чтобы работало отрицание (см. ниже) |
| `has_genre/2` (7) | объектное свойство `hasGenre`: `Game → Genre` | второй аргумент — индивид класса `Genre` |
| `developed_by/2` (8) | функциональное свойство `developedBy`: `Game → Studio` | второй аргумент — индивид класса `Studio` |
| `action_game/1` | определённый класс `ActionGame` | `Game and (hasGenre value action or hasGenre value action_rpg)` |
| `social_game/1` | определённый класс `SocialGame` | `Game and MultiplayerGame` |
| `indie_singleplayer/1` | определённый класс `IndieSingleplayerGame` | `IndieGame and SingleplayerGame` |
| `narrative_solo/1` | определённый класс `NarrativeSoloGame` | `StoryDrivenGame and not MultiplayerGame` |
| `game_by_studio/2` | обратное свойство `develops` | `inverse(developedBy)` |
| `same_developer/2` | свойство `sameDeveloperAs` + SWRL-правило | `developedBy(?g1,?s) ^ developedBy(?g2,?s) ^ differentFrom(?g1,?g2) -> sameDeveloperAs(?g1,?g2)` |
| `recommended_for_beginner/1` | определённый класс `BeginnerFriendlyGame` | `Game and not CompetitiveGame and (CozyGame or hasGenre value puzzle)` |

Логические операторы: «и» (`,`) → `and` / `owl:intersectionOf`,
«или» (`;`) → `or` / `owl:unionOf`, «не» (`\+`) → `not` / `owl:complementOf`.

Иерархия классов:

```
owl:Thing
├── Game
│   ├── SingleplayerGame
│   │   └── IndieSingleplayerGame        (выводится reasoner'ом)
│   ├── MultiplayerGame ≡ SocialGame     (эквивалентность выводится reasoner'ом)
│   ├── IndieGame
│   ├── StoryDrivenGame
│   │   └── NarrativeSoloGame            (выводится reasoner'ом)
│   ├── CompetitiveGame
│   ├── CozyGame
│   ├── ActionGame
│   └── BeginnerFriendlyGame
├── Genre    (action, action_rpg, puzzle, moba, simulation, sandbox, platformer)
└── Studio   (valve, mojang, cd_projekt_red, ...)
```

`Game`, `Genre` и `Studio` объявлены попарно непересекающимися.

### Замкнутый и открытый мир

Prolog работает в **предположении замкнутого мира**: `\+ multiplayer(hades)`
истинно, потому что такого факта в базе нет. OWL работает в **предположении
открытого мира**: отсутствие утверждения не означает его ложность, и
reasoner не выведет, что Hades — не многопользовательская игра.

Чтобы правила с отрицанием работали так же, как в Prolog, в онтологии:

1. классы `MultiplayerGame` и `CompetitiveGame` объявлены эквивалентными
   перечислению своих индивидов (`owl:oneOf`) — «других таких игр нет»;
2. все индивиды объявлены попарно различными (`owl:AllDifferent`) — аналог
   уникальности имён в Prolog, где разные атомы всегда разные объекты.

Без этих аксиом классы `NarrativeSoloGame` и `BeginnerFriendlyGame`
остались бы пустыми.

## Демонстрация в Protege

1. **Reasoner → HermiT → Start reasoner** (при изменениях — Synchronize reasoner).
2. **Entities → Classes**: в режиме *Inferred* `NarrativeSoloGame`
   становится подклассом `StoryDrivenGame`, `IndieSingleplayerGame` —
   подклассом `SingleplayerGame` и `IndieGame`, а `SocialGame` —
   эквивалентным `MultiplayerGame`.
3. **Entities → Individuals → `hades`**: в *Description* жёлтым выделены
   выведенные типы `ActionGame`, `NarrativeSoloGame`, `IndieSingleplayerGame`.
   У `portal_2` в *Property assertions* выведено `sameDeveloperAs dota_2`
   и `sameDeveloperAs half_life_2`, у `valve` — `develops`.
4. **Визуализация**: Window → Tabs → **OntoGraf** (граф классов, индивидов и
   связей `hasGenre` / `developedBy`) или **OWLViz** (иерархия классов,
   asserted и inferred).
5. **Запросы**: вкладки **DL Query** и **SPARQL Query** (ниже).

### DL Query (Window → Tabs → DL Query, отметить *Instances*)

Работают после запуска reasoner'а, то есть используют выведенные знания.

| # | Запрос | Аналог в Prolog | Ожидаемый ответ |
|---|---|---|---|
| 1 | `IndieGame` | `indie(G)` | among_us, celeste, disco_elysium, hades, hollow_knight, outer_wilds, stardew_valley, terraria, undertale |
| 2 | `SingleplayerGame and StoryDrivenGame` | `singleplayer(G), story_driven(G)` | 11 игр, включая portal_2 и baldurs_gate_3 |
| 3 | `CozyGame or CompetitiveGame` | `cozy(G) ; competitive(G)` | among_us, counter_strike_2, dota_2, minecraft, rocket_league, stardew_valley |
| 4 | `Game and not MultiplayerGame` | `game(G), \+ multiplayer(G)` | celeste, disco_elysium, hades, half_life_2, hollow_knight, outer_wilds, stardew_valley, the_witcher_3, undertale |
| 5 | `developedBy value valve` | `developed_by(G, valve)` | dota_2, half_life_2, portal_2 |
| 6 | `ActionGame` | `action_game(G)` | hades, the_witcher_3 |
| 7 | `IndieSingleplayerGame` | `indie_singleplayer(G)` | celeste, disco_elysium, hades, hollow_knight, outer_wilds, stardew_valley, terraria, undertale |
| 8 | `NarrativeSoloGame` | `narrative_solo(G)` | celeste, disco_elysium, hades, half_life_2, hollow_knight, outer_wilds, the_witcher_3, undertale |
| 9 | `sameDeveloperAs value portal_2` | `same_developer(G, portal_2)` | dota_2, half_life_2 |
| 10 | `BeginnerFriendlyGame` | `recommended_for_beginner(G)` | minecraft, portal_2, stardew_valley |
| 11 | `Studio and develops some (hasGenre value puzzle)` | `developed_by(G, S), has_genre(G, puzzle)` | valve |

### SPARQL Query

Запросы из `queries.sparql` вставляются по одному вместе с блоком `PREFIX`.
Вкладка SPARQL Query работает только с явно заданными фактами, поэтому
правила там записаны самим запросом: «и» — несколько триплетов, «или» —
`UNION`, «не» — `FILTER NOT EXISTS`. Есть также запрос с агрегацией
`COUNT` / `GROUP BY` (число игр у студии).

## Проверка

Ответы HermiT по определённым классам и свойствам, а также результаты всех
SPARQL-запросов совпадают с ответами `demo.pl` и `tests.pl` из первой части.
