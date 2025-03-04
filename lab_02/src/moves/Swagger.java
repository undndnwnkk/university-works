package moves;

import ru.ifmo.se.pokemon.*;

import java.lang.String;

public class Swagger extends StatusMove {
    public Swagger() {
        super(Type.NORMAL, 0, 85);
    }

    @Override
    protected String describe() {
        return "uses Swagger";
    }

    @Override
    protected void applyOppEffects(Pokemon enemy) {
        Effect.confuse(enemy);
        enemy.setMod(Stat.ATTACK, +2);
    }
}
