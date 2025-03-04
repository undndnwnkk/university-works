package moves;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import java.lang.String;

public class Headbutt extends PhysicalMove {
    public Headbutt() {
        super(Type.NORMAL, 70, 100);
    }

    @Override
    protected String describe() {
        return "uses Headbutt";
    }

    @Override
    protected void applyOppEffects(Pokemon enemy) {
        if(Math.random() < 0.3) {
            Effect.flinch(enemy);
        }
    }
}
