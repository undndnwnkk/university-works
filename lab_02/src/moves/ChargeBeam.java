package moves;

import ru.ifmo.se.pokemon.*;

public class ChargeBeam extends SpecialMove {
    public ChargeBeam() {
        super(Type.ELECTRIC, 50, 90);
    }

    @Override
    protected String describe() {
        return "uses Charge Beam";
    }

    @Override
    protected void applySelfEffects(Pokemon self) {
        if(Math.random() < 0.7) {
            self.setMod(Stat.SPECIAL_ATTACK, 1);
        }
    }
}
