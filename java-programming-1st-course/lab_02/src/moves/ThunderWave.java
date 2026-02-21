package moves;

import ru.ifmo.se.pokemon.*;

import java.lang.String;

public class ThunderWave extends StatusMove {
    public ThunderWave() {
        super(Type.ELECTRIC, 0, 90);
    }

    @Override
    protected String describe() {
        return "uses Thunder Wave";
    }

    @Override
    protected void applyOppEffects(Pokemon enemy) {
        Effect.paralyze(enemy);
    }
}
