package moves;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.Type;

public class ShadowBall extends SpecialMove {
    public ShadowBall() {
        super(Type.GHOST, 80, 100);
    }

    @Override
    protected String describe() {
        return "uses Shadow Ball";
    }

    @Override
    protected void applyOppEffects(Pokemon enemy) {
        if(Math.random() < 0.2) {
            enemy.setMod(Stat.SPECIAL_DEFENSE, -1);
        }
    }
}
