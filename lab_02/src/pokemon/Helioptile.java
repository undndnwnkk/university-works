package pokemon;

import moves.QuickAttack;
import moves.Swagger;
import moves.WildCharge;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Helioptile extends Pokemon {
    public Helioptile(String name, int level) {
        super(name, level);
        super.setType(Type.NORMAL, Type.ELECTRIC);
        super.setStats(44, 38, 33, 61, 43, 70);
        Swagger swagger = new Swagger();
        WildCharge wildCharge = new WildCharge();
        QuickAttack quickAttack = new QuickAttack();

        super.setMove(swagger, wildCharge, quickAttack);
    }
}
