package it.unicam.cs.mpgc.rpg195945.engine.trial;

import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementWheel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementalRule;
import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EquinoxTrialTest {

    private final ElementalRule wheel = ElementWheel.standard();

    @Test
    void guardianUsesTheOppositeElement() {
        assertEquals(Element.EARTH, EquinoxTrial.guardianFor(new Student("Lira", Affinity.FIRE), wheel).element());
        assertEquals(Element.FIRE, EquinoxTrial.guardianFor(new Student("Lira", Affinity.EARTH), wheel).element());
        assertEquals(Element.AIR, EquinoxTrial.guardianFor(new Student("Lira", Affinity.WATER), wheel).element());
        assertEquals(Element.WATER, EquinoxTrial.guardianFor(new Student("Lira", Affinity.AIR), wheel).element());
    }

    @Test
    void guardianKnowsEveryRuneOfItsElement() {
        Opponent guardian = EquinoxTrial.guardianFor(new Student("Lira", Affinity.FIRE), wheel);
        assertEquals(Rune.all().size(), guardian.spells().size());
        assertEquals(EquinoxTrial.GUARDIAN_STATS, guardian.stats());
    }

    @Test
    void withARuleWithoutAdvantagesTheGuardianUsesAnotherElement() {
        ElementalRule noAdvantages = (attack, target) -> 1.0;
        assertEquals(Element.WATER, EquinoxTrial.guardianElement(Element.FIRE, noAdvantages));
    }

    @Test
    void ifNoElementIsNeutralTheGuardianUsesTheStudentsOwn() {
        ElementalRule alwaysStrong = (attack, target) -> 2.0;
        assertEquals(Element.AIR, EquinoxTrial.guardianElement(Element.AIR, alwaysStrong));
    }
}
