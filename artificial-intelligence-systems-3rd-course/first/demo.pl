% Демонстрационный сценарий запросов к базе знаний.
% Запуск: swipl -q -s demo.pl

:- encoding(utf8).
:- consult('knowledge_base.pl').
:- initialization(main, main).

% print_solutions(+Title, :Goal, +Template)
% Печатает заголовок, затем список всех различных решений Goal.
print_solutions(Title, Goal, Template) :-
    findall(Template, Goal, RawAnswers),
    sort(RawAnswers, Answers),
    format('~n~w~n  ~q~n', [Title, Answers]).

main :-
    format('Лабораторная работа 1: запросы к базе знаний о видеоиграх~n'),

    % Простой запрос конкретного факта.
    (   game(portal_2)
    ->  format('~n1. game(portal_2)~n  true~n')
    ;   format('~n1. game(portal_2)~n  false~n')
    ),

    % Запрос с переменной к унарному факту.
    print_solutions(
        '2. Все независимые игры:',
        indie(Game),
        Game
    ),

    % Логическое «и»: игра одновременно одиночная и сюжетная.
    print_solutions(
        '3. Одиночные И сюжетные игры:',
        (singleplayer(Game), story_driven(Game)),
        Game
    ),

    % Логическое «или».
    print_solutions(
        '4. Спокойные ИЛИ соревновательные игры:',
        (cozy(Game); competitive(Game)),
        Game
    ),

    % Логическое отрицание; game/1 вызывается первым, чтобы множество
    % проверяемых объектов было конечным и отрицание было безопасным.
    print_solutions(
        '5. Игры БЕЗ мультиплеера:',
        (game(Game), \+ multiplayer(Game)),
        Game
    ),

    % Запрос к бинарному отношению с переменной.
    print_solutions(
        '6. Игры студии Valve:',
        developed_by(Game, valve),
        Game
    ),

    % Следующие запросы требуют применения правил.
    print_solutions(
        '7. Независимые одиночные игры (правило indie_singleplayer/1):',
        indie_singleplayer(Game),
        Game
    ),
    print_solutions(
        '8. Сюжетные игры без мультиплеера (правило narrative_solo/1):',
        narrative_solo(Game),
        Game
    ),
    print_solutions(
        '9. Пары разных игр одного разработчика (правило same_developer/2):',
        same_developer(Game1, Game2),
        Game1-Game2
    ),
    print_solutions(
        '10. Рекомендации новичку (правило recommended_for_beginner/1):',
        recommended_for_beginner(Game),
        Game
    ).
