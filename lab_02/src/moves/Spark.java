package moves;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import java.lang.String;

public class Spark extends PhysicalMove {
    public Spark() {
        super(Type.ELECTRIC, 65, 100);
    }

    @Override
    protected String describe() {
        return "uses Spark";
    }

    @Override
    protected void applyOppEffects(Pokemon enemy) {
        if(Math.random() < 0.3) {
            Effect.paralyze(enemy);
        }
    }
}
