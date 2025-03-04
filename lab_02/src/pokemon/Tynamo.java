package pokemon;

import moves.ChargeBeam;
import moves.Spark;
import moves.ThunderWave;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Tynamo extends Pokemon {
    public Tynamo(String name, int level) {
        super(name, level);
        super.setType(Type.ELECTRIC);
        super.setStats(35, 55, 40, 45, 40, 60);

        ThunderWave thunderWave = new ThunderWave();
        ChargeBeam chargeBeam = new ChargeBeam();
        super.setMove(thunderWave, chargeBeam);

        Spark spark = new Spark();
        super.addMove(spark);
    }
}
