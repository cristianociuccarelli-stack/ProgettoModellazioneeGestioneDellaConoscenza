package it.unicam.cs.mpgc.rpg195945.model.student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
