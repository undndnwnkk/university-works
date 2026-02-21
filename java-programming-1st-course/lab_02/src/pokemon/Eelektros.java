package pokemon;

import moves.Headbutt;
import ru.ifmo.se.pokemon.Type;

public class Eelektros extends Eelektrik{
    public Eelektros(String name, int level) {
        super(name, level);
        super.setType(Type.ELECTRIC);
        super.setStats(85, 115, 80, 105, 80, 50);

        Headbutt headbutt = new Headbutt();
        super.addMove(headbutt);
    }
}
