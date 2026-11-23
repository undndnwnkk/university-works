import unittest

from main import (ParseError, build_query, fallback, load_ontology, parse,
                  query_text, recommend)

GAMES = load_ontology()


class OntologyTests(unittest.TestCase):
    def test_loaded_from_owx(self):
        self.assertEqual(len(GAMES), 20)
        hades = GAMES["hades"]
        self.assertEqual(hades["label"], "Hades")
        self.assertEqual(hades["minAge"], 12)
        self.assertIn("IndieGame", hades["classes"])
        self.assertEqual(hades["hasGenre"], {"action"})

    def test_every_game_has_age(self):
        for name, game in GAMES.items():
            with self.subTest(game=name):
                self.assertGreater(game["minAge"], 0)


class ParseTests(unittest.TestCase):
    def test_example_from_task(self):
        self.assertEqual(parse("Мне 13 лет, мне нравятся: RPG, инди-игры"),
                         (13, ["rpg", "indie"]))

    def test_age_word_forms_and_case(self):
        self.assertEqual(parse("мне 21 год, мне нравятся: шутеры")[0], 21)
        self.assertEqual(parse("МНЕ 3 ГОДА, МНЕ НРАВЯТСЯ: ЭКШЕН")[0], 3)

    def test_extra_spaces_and_dot(self):
        self.assertEqual(parse("  Мне  30 лет,  мне нравятся:  стратегии ,шутеры. "),
                         (30, ["strategy", "shooter"]))

    def test_synonyms_are_not_duplicated(self):
        self.assertEqual(parse("Мне 20 лет, мне нравятся: RPG, рпг, ролевые игры")[1],
                         ["rpg"])

    def test_bad_inputs(self):
        for line in [
            "",
            "Привет",
            "Мне тринадцать лет, мне нравятся: RPG",
            "Мне 13 лет мне нравятся: RPG",
            "Мне 13 лет, мне нравятся:",
            "Мне 13 лет, мне нравятся: RPG,",
            "Мне 13 лет, мне нравятся: футбол",
            "Мне 13 лет, мне нравятся: ролевые",
            "Мне 0 лет, мне нравятся: RPG",
            "Мне -5 лет, мне нравятся: RPG",
        ]:
            with self.subTest(line=line), self.assertRaises(ParseError):
                parse(line)


class QueryTests(unittest.TestCase):
    def test_query_text(self):
        self.assertEqual(
            query_text(*build_query(13, ["rpg", "indie"], "solo", True)),
            "Game and minAge <= 13 and SingleplayerGame and not CompetitiveGame"
            " and not HardGame and (hasGenre value rpg or IndieGame)")


class RecommendTests(unittest.TestCase):
    def labels(self, *args):
        return [label for label, _ in recommend(GAMES, *args)]

    def test_example_from_task(self):
        result = recommend(GAMES, 13, ["rpg", "indie"], "solo", True)
        self.assertEqual(result[0], ("Undertale", ["rpg", "indie"]))
        self.assertIn("Hades", [label for label, _ in result])

    def test_age_limit(self):
        self.assertNotIn("The Witcher 3", self.labels(13, ["rpg"], "any", False))
        self.assertIn("The Witcher 3", self.labels(18, ["rpg"], "any", False))

    def test_beginner_excludes_hard_and_competitive(self):
        labels = self.labels(30, ["action", "competitive"], "any", True)
        self.assertNotIn("Dark Souls III", labels)
        self.assertNotIn("Dota 2", labels)
        self.assertIn("Hades", labels)

    def test_mode(self):
        self.assertNotIn("Disco Elysium", self.labels(30, ["rpg"], "friends", False))
        self.assertNotIn("Dota 2", self.labels(30, ["moba"], "solo", False))

    def test_sorted_by_matches(self):
        result = recommend(GAMES, 30, ["indie", "story", "platformer"], "any", False)
        counts = [len(m) for _, m in result]
        self.assertEqual(counts, sorted(counts, reverse=True))
        self.assertEqual(result[0][0], "Celeste")

    def test_nothing_found_and_fallback(self):
        self.assertEqual(recommend(GAMES, 8, ["strategy"], "any", True), [])
        self.assertEqual(fallback(GAMES, 8, "any", True),
                         ["Minecraft", "Stardew Valley"])


if __name__ == "__main__":
    unittest.main()
