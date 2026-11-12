package it.unicam.cs.mpgc.rpg195945.model.student;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentTest {

    @Test
    void newStudentStartsAtLevelOneWithBaseStatsAndBonus() {
        Student student = new Student("Lira", Affinity.FIRE);
        assertEquals(1, student.level());
        // base (100, 50, 10, 10, 10) + bonus del Piromante (0, 0, 5, 0, 1)
        assertEquals(new Stats(100, 50, 15, 10, 11), student.stats());
    }

    @Test
    void statsGrowWhenLevelingUp() {
        Student student = new Student("Lira", Affinity.FIRE);
        student.gainExperience(100);
        assertEquals(2, student.level());
        // + crescita del Piromante (8, 4, 3, 1, 1) per un livello
        assertEquals(new Stats(108, 54, 18, 11, 12), student.stats());
    }

    @Test
    void gainExperienceReturnsLevelsGained() {
        Student student = new Student("Lira", Affinity.WATER);
        assertEquals(2, student.gainExperience(300));
        assertEquals(3, student.level());
    }

    @Test
    void nameIsTrimmed() {
        assertEquals("Lira", new Student("  Lira  ", Affinity.AIR).name());
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Student("   ", Affinity.EARTH));
    }

    @Test
    void rejectsMissingAffinity() {
        assertThrows(NullPointerException.class, () -> new Student("Lira", null));
    }

    @Test
    void newStudentKnowsOnlyTheSpellsOfItsElement() {
        Student student = new Student("Lira", Affinity.FIRE);
        assertEquals(List.of(
                new Spell(Element.FIRE, Rune.BOLT),
                new Spell(Element.FIRE, Rune.WARD)), student.spells());
        assertFalse(student.canCast(new Spell(Element.WATER, Rune.BOLT)));
    }

    @Test
    void learningUnlocksNewSpells() {
        Student student = new Student("Lira", Affinity.WATER);
        assertTrue(student.learn(Rune.SPRING));
        assertTrue(student.learn(Element.AIR));
        // 2 elementi x 3 rune
        assertEquals(6, student.spells().size());
        assertTrue(student.canCast(new Spell(Element.AIR, Rune.SPRING)));
    }
}
