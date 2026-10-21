% Лабораторная работа 1. База знаний о видеоиграх.
%
% Все имена объектов записаны строчными буквами, потому что в Prolog
% идентификатор со строчной буквы является атомом, а с прописной — переменной.

% -----------------------------------------------------------------------------
% Унарные факты: игры и их свойства
% -----------------------------------------------------------------------------

% game(Game) — Game является видеоигрой (20 фактов).
game(the_witcher_3).
game(hades).
game(portal_2).
game(dota_2).
game(stardew_valley).
game(minecraft).
game(celeste).
game(half_life_2).
game(disco_elysium).
game(hollow_knight).
game(outer_wilds).
game(undertale).
game(counter_strike_2).
game(terraria).
game(civilization_6).
game(among_us).
game(rocket_league).
game(dark_souls_3).
game(red_dead_redemption_2).
game(baldurs_gate_3).

% singleplayer(Game) — в игре есть полноценный одиночный режим.
singleplayer(the_witcher_3).
singleplayer(hades).
singleplayer(portal_2).
singleplayer(stardew_valley).
singleplayer(minecraft).
singleplayer(celeste).
singleplayer(half_life_2).
singleplayer(disco_elysium).
singleplayer(hollow_knight).
singleplayer(outer_wilds).
singleplayer(undertale).
singleplayer(terraria).
singleplayer(civilization_6).
singleplayer(dark_souls_3).
singleplayer(red_dead_redemption_2).
singleplayer(baldurs_gate_3).

% multiplayer(Game) — игра поддерживает совместную или соревновательную игру.
multiplayer(portal_2).
multiplayer(dota_2).
multiplayer(minecraft).
multiplayer(counter_strike_2).
multiplayer(terraria).
multiplayer(civilization_6).
multiplayer(among_us).
multiplayer(rocket_league).
multiplayer(dark_souls_3).
multiplayer(red_dead_redemption_2).
multiplayer(baldurs_gate_3).

% indie(Game) — игра создана независимой студией или небольшой командой.
indie(hades).
indie(stardew_valley).
indie(celeste).
indie(disco_elysium).
indie(hollow_knight).
indie(outer_wilds).
indie(undertale).
indie(terraria).
indie(among_us).

% story_driven(Game) — сюжет является одной из основных составляющих игры.
story_driven(the_witcher_3).
story_driven(hades).
story_driven(portal_2).
story_driven(celeste).
story_driven(half_life_2).
story_driven(disco_elysium).
story_driven(hollow_knight).
story_driven(outer_wilds).
story_driven(undertale).
story_driven(red_dead_redemption_2).
story_driven(baldurs_gate_3).

% competitive(Game) — основной игровой процесс предполагает соревнование.
competitive(dota_2).
competitive(counter_strike_2).
competitive(among_us).
competitive(rocket_league).

% cozy(Game) — игра подходит для спокойного, ненапряжённого прохождения.
cozy(stardew_valley).
cozy(minecraft).

% -----------------------------------------------------------------------------
% Бинарные факты: отношения между играми, жанрами и разработчиками
% Всего в этом разделе ровно 15 фактов с двумя аргументами.
% -----------------------------------------------------------------------------

% has_genre(Game, Genre) — игра Game относится к жанру Genre (7 фактов).
has_genre(the_witcher_3, action_rpg).
has_genre(hades, action).
has_genre(portal_2, puzzle).
has_genre(dota_2, moba).
has_genre(stardew_valley, simulation).
has_genre(minecraft, sandbox).
has_genre(celeste, platformer).

% developed_by(Game, Studio) — игра Game создана студией Studio (8 фактов).
developed_by(the_witcher_3, cd_projekt_red).
developed_by(hades, supergiant_games).
developed_by(portal_2, valve).
developed_by(dota_2, valve).
developed_by(stardew_valley, concernedape).
developed_by(minecraft, mojang).
developed_by(celeste, extremely_ok_games).
developed_by(half_life_2, valve).

% -----------------------------------------------------------------------------
% Правила логического вывода (7 правил)
% -----------------------------------------------------------------------------

% 1. action_game(Game) — жанр игры равен action ИЛИ action_rpg.
% Точка с запятой в Prolog обозначает логическое «или».
action_game(Game) :-
    game(Game),
    (has_genre(Game, action); has_genre(Game, action_rpg)).

% 2. social_game(Game) — это игра И она поддерживает мультиплеер.
% Запятая в теле правила обозначает логическое «и».
social_game(Game) :-
    game(Game),
    multiplayer(Game).

% 3. indie_singleplayer(Game) — независимая одиночная игра.
indie_singleplayer(Game) :-
    game(Game),
    indie(Game),
    singleplayer(Game).

% 4. narrative_solo(Game) — сюжетная игра без мультиплеера.
% \+ Goal означает «Goal не доказана» (логическое отрицание как неудача).
narrative_solo(Game) :-
    game(Game),
    story_driven(Game),
    \+ multiplayer(Game).

% 5. game_by_studio(Game, Studio) — получить игру указанной студии.
game_by_studio(Game, Studio) :-
    game(Game),
    developed_by(Game, Studio).

% 6. same_developer(Game1, Game2) — разные игры одного разработчика.
% Стандартный предикат dif/2 требует, чтобы аргументы не были одинаковыми.
same_developer(Game1, Game2) :-
    developed_by(Game1, Studio),
    developed_by(Game2, Studio),
    dif(Game1, Game2).

% 7. recommended_for_beginner(Game) — не соревновательная игра, которая
% либо относится к спокойным, либо является головоломкой.
% Правило одновременно демонстрирует «и», «или» и «не».
recommended_for_beginner(Game) :-
    game(Game),
    \+ competitive(Game),
    (cozy(Game); has_genre(Game, puzzle)).
