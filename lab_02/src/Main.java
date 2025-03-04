import ru.ifmo.se.pokemon.*;

import pokemon.JangmoO;
import pokemon.Heliolisk;

public class Main {
    public static void main(String[] args) {
        Battle b = new Battle();

        Pokemon p1 = new JangmoO("Чужой", 1);
        Pokemon p2 = new Heliolisk("Хищник", 1);

        b.addAlly(p1);
        b.addFoe(p2);

        b.go();
    }
}
