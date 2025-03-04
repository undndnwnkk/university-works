package moves;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Astonish extends PhysicalMove {
    public Astonish() {
        super(Type.GHOST, 30, 100);
    }

    @Override
    protected String describe() {
        return "uses Astonish";
    }

    @Override
    protected void applyOppEffects(Pokemon enemy) {
        if(Math.random() < 0.3) {
            Effect.flinch(enemy);
        }
    }
}
