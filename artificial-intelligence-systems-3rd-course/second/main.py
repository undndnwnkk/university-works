import re
import xml.etree.ElementTree as ET
from pathlib import Path

ONTOLOGY = Path(__file__).with_name("video_games.owx")
OWL = "{http://www.w3.org/2002/07/owl#}"

PATTERN = re.compile(
    r"^мне (\d+) (?:год|года|лет), мне нравятся: (.+?)\.?$", re.IGNORECASE
)

INTERESTS = {
    "rpg": "rpg", "рпг": "rpg", "ролевые игры": "rpg",
    "инди": "indie", "инди-игры": "indie",
    "сюжетные игры": "story",
    "соревновательные игры": "competitive", "киберспорт": "competitive",
    "уютные игры": "cozy", "спокойные игры": "cozy",
    "экшен": "action", "экшены": "action",
    "головоломки": "puzzle",
    "платформеры": "platformer",
    "песочницы": "sandbox",
    "симуляторы": "simulation",
    "стратегии": "strategy",
    "шутеры": "shooter",
    "приключения": "adventure",
    "спортивные игры": "sport",
    "moba": "moba",
}

CONDITIONS = {
    "indie": "IndieGame",
    "story": "StoryDrivenGame",
    "competitive": "CompetitiveGame",
    "cozy": "CozyGame",
}

INTEREST_NAMES = {
    "rpg": "RPG", "indie": "инди", "story": "сюжет", "competitive": "соревнования",
    "cozy": "уют", "action": "экшен", "puzzle": "головоломка",
    "platformer": "платформер", "sandbox": "песочница", "simulation": "симулятор",
    "strategy": "стратегия", "shooter": "шутер", "adventure": "приключения",
    "sport": "спорт", "moba": "MOBA",
}

MODES = {"один": "solo", "с друзьями": "friends", "неважно": "any"}
YES_NO = {"да": True, "нет": False}


class ParseError(Exception):
    pass


def short(iri):
    return iri.split("#")[-1]


def load_ontology(path=ONTOLOGY):
    root = ET.parse(path).getroot()
    items = {}

    def item(name):
        return items.setdefault(name, {"label": name, "classes": set(),
                                       "hasGenre": set(), "minAge": 0})

    for ax in root.iter(OWL + "ClassAssertion"):
        cls, ind = ax[0].get("IRI"), ax[1].get("IRI")
        if cls and ind:
            item(short(ind))["classes"].add(short(cls))

    for ax in root.iter(OWL + "ObjectPropertyAssertion"):
        prop, subj, obj = (short(e.get("IRI")) for e in ax[:3])
        if prop == "hasGenre":
            item(subj)["hasGenre"].add(obj)

    for ax in root.iter(OWL + "DataPropertyAssertion"):
        prop, subj = short(ax[0].get("IRI")), short(ax[1].get("IRI"))
        if prop == "minAge":
            item(subj)["minAge"] = int(ax[2].text)

    for ax in root.iter(OWL + "AnnotationAssertion"):
        name = short(ax[1].text or "")
        if ax[0].get("abbreviatedIRI") == "rdfs:label" and name in items:
            items[name]["label"] = ax[2].text

    return {n: g for n, g in items.items() if "Game" in g["classes"]}


def parse(line):
    match = PATTERN.match(" ".join(line.split()))
    if not match:
        raise ParseError("Формат: Мне <N> лет, мне нравятся: <интерес>, <интерес>")
    age = int(match.group(1))
    if not 1 <= age <= 120:
        raise ParseError("Возраст должен быть от 1 до 120")
    interests = []
    for word in match.group(2).split(","):
        word = word.strip().lower()
        if word not in INTERESTS:
            raise ParseError(f"Неизвестный интерес «{word}». Доступны: "
                             + ", ".join(INTERESTS))
        if INTERESTS[word] not in interests:
            interests.append(INTERESTS[word])
    return age, interests


def build_query(age, interests, mode, beginner):
    must = [f"minAge <= {age}"]
    if mode == "solo":
        must.append("SingleplayerGame")
    elif mode == "friends":
        must.append("MultiplayerGame")
    if beginner:
        must += ["not CompetitiveGame", "not HardGame"]
    wanted = {i: CONDITIONS.get(i, f"hasGenre value {i}") for i in interests}
    return must, wanted


def query_text(must, wanted):
    return " and ".join(["Game"] + must) + " and (" + " or ".join(wanted.values()) + ")"


def holds(game, cond):
    if cond.startswith("not "):
        return not holds(game, cond[4:])
    if cond.startswith("minAge <= "):
        return game["minAge"] <= int(cond.split()[-1])
    if cond.startswith("hasGenre value "):
        return cond.split()[-1] in game["hasGenre"]
    return cond in game["classes"]


def recommend(games, age, interests, mode, beginner):
    must, wanted = build_query(age, interests, mode, beginner)
    result = []
    for game in games.values():
        if all(holds(game, c) for c in must):
            matched = [i for i, c in wanted.items() if holds(game, c)]
            if matched:
                result.append((game["label"], matched))
    result.sort(key=lambda r: (-len(r[1]), r[0]))
    return result


def fallback(games, age, mode, beginner):
    must, _ = build_query(age, [], mode, beginner)
    return sorted(g["label"] for g in games.values()
                  if all(holds(g, c) for c in must))


def ask(question, options):
    while True:
        answer = input(f"{question} ({' / '.join(options)}): ").strip().lower()
        if answer in options:
            return options[answer]
        print("Не понял ответ, выберите один из вариантов.")


def main():
    games = load_ontology()
    print(f"Онтология {ONTOLOGY.name} загружена: {len(games)} игр.")
    print("Пример: Мне 13 лет, мне нравятся: RPG, инди-игры. Пустая строка — выход.")
    while True:
        line = input("> ").strip()
        if not line:
            return
        try:
            age, interests = parse(line)
            break
        except ParseError as e:
            print("Ошибка:", e)

    mode = ask("Как вы любите играть?", MODES)
    beginner = ask("Вы новичок в играх?", YES_NO)

    must, wanted = build_query(age, interests, mode, beginner)
    print(f"\nЗапрос к онтологии: {query_text(must, wanted)}")

    result = recommend(games, age, interests, mode, beginner)
    if result:
        print("Рекомендую:")
        for label, matched in result:
            why = ", ".join(INTEREST_NAMES[m] for m in matched)
            print(f"  - {label} (совпало: {why})")
    else:
        print("По вашим интересам подходящих игр нет.")
        other = fallback(games, age, mode, beginner)
        if other:
            print("Но под ваш возраст и режим подойдут:", ", ".join(other))
        else:
            print("Попробуйте выбрать другой режим игры.")


if __name__ == "__main__":
    try:
        main()
    except (EOFError, KeyboardInterrupt):
        print()
