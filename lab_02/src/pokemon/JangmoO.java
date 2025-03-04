package pokemon;

import moves.Astonish;
import moves.DoubleTeam;
import moves.FlashCannon;
import moves.ShadowBall;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class JangmoO extends Pokemon {
    public JangmoO(String name, int level) {
        super(name, level);
        super.setType(Type.DRAGON);
        super.setStats(45, 55, 65, 45, 45, 45);

        FlashCannon flashCannon = new FlashCannon();
        ShadowBall shadowBall = new ShadowBall();
        Astonish astonish = new Astonish();
        DoubleTeam doubleTeam = new DoubleTeam();

        super.setMove(flashCannon, shadowBall, astonish, doubleTeam);
    }
}
