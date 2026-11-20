package it.unicam.cs.mpgc.rpg195945.engine.trial;

import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementWheel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementalRule;
import it.unicam.cs.mpgc.rpg195945.model.calendar.AcademicYear;
import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquinoxTrialTest {

    private final ElementalRule wheel = ElementWheel.standard();
    private final Student student = new Student("Lira", Affinity.FIRE);

    @Test
    void thereIsADifferentGuardianForEveryYear() {
        Set<String> names = new HashSet<>();
        for (int year = 1; year <= AcademicYear.LAST_YEAR; year++) {
            names.add(EquinoxTrial.guardianOfYear(year, student, wheel).name());
        }
        assertEquals(AcademicYear.LAST_YEAR, names.size());
        assertThrows(IllegalArgumentException.class, () -> EquinoxTrial.guardianOfYear(0, student, wheel));
        assertThrows(IllegalArgumentException.class,
                () -> EquinoxTrial.guardianOfYear(AcademicYear.LAST_YEAR + 1, student, wheel));
    }

    @Test
    void guardiansGetStrongerEveryYear() {
        for (int year = 2; year <= AcademicYear.LAST_YEAR; year++) {
            Opponent previous = EquinoxTrial.guardianOfYear(year - 1, student, wheel);
            Opponent current = EquinoxTrial.guardianOfYear(year, student, wheel);
            assertTrue(current.stats().health() > previous.stats().health(), current.name());
        }
    }

    @Test
    void theLastGuardianUsesTheOppositeElement() {
        assertEquals(Element.EARTH, EquinoxTrial.equinoxGuardianFor(new Student("Lira", Affinity.FIRE), wheel).element());
        assertEquals(Element.FIRE, EquinoxTrial.equinoxGuardianFor(new Student("Lira", Affinity.EARTH), wheel).element());
        assertEquals(Element.AIR, EquinoxTrial.equinoxGuardianFor(new Student("Lira", Affinity.WATER), wheel).element());
        assertEquals(Element.WATER, EquinoxTrial.equinoxGuardianFor(new Student("Lira", Affinity.AIR), wheel).element());
    }

    @Test
    void theLastGuardianKnowsEveryRune() {
        Opponent guardian = EquinoxTrial.guardianOfYear(AcademicYear.LAST_YEAR, student, wheel);
        assertEquals(Rune.all().size(), guardian.spells().size());
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
