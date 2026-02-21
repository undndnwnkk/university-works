package pokemon;

import moves.Spark;
import ru.ifmo.se.pokemon.Type;

public class Eelektrik extends Tynamo{
    public Eelektrik(String name, int level) {
        super(name, level);
        super.setType(Type.ELECTRIC);
        super.setStats(65, 85, 70, 75, 70, 40);

        Spark spark = new Spark();
        super.addMove(spark);
    }

}
