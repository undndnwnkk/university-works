% Автоматические тесты базы знаний для SWI-Prolog.
% Запуск: swipl -q -s tests.pl -g run_tests,halt

:- encoding(utf8).
:- consult('knowledge_base.pl').
:- begin_tests(game_knowledge_base).

test(known_game) :-
    game(portal_2).

test(unknown_game, [fail]) :-
    game(unknown_game).

test(binary_fact) :-
    developed_by(portal_2, valve).

test(action_rule, set(Game == [hades, the_witcher_3])) :-
    action_game(Game).

test(indie_singleplayer_rule,
     set(Game == [celeste, disco_elysium, hades, hollow_knight,
                  outer_wilds, stardew_valley, terraria, undertale])) :-
    indie_singleplayer(Game).

test(narrative_solo_uses_negation,
     set(Game == [celeste, disco_elysium, hades, half_life_2,
                  hollow_knight, outer_wilds, the_witcher_3, undertale])) :-
    narrative_solo(Game).

test(valve_games,
     set(Game == [dota_2, half_life_2, portal_2])) :-
    game_by_studio(Game, valve).

test(same_developer_rule,
     set(Pair == [dota_2-half_life_2, dota_2-portal_2,
                  half_life_2-dota_2, half_life_2-portal_2,
                  portal_2-dota_2, portal_2-half_life_2])) :-
    same_developer(Game1, Game2),
    Pair = Game1-Game2.

test(beginner_recommendation,
     set(Game == [minecraft, portal_2, stardew_valley])) :-
    recommended_for_beginner(Game).

:- end_tests(game_knowledge_base).
