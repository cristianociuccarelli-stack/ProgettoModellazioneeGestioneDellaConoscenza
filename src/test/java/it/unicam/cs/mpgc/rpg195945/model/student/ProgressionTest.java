package it.unicam.cs.mpgc.rpg195945.model.student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProgressionTest {

    @Test
    void startsAtLevelOne() {
        Progression progression = new Progression();
        assertEquals(1, progression.level());
        assertEquals(0, progression.experience());
    }

    @Test
    void levelsUpAndKeepsExtraExperience() {
        Progression progression = new Progression();
        // dal livello 1 al 2 servono 100 punti: ne restano 30
        assertEquals(1, progression.gain(130));
        assertEquals(2, progression.level());
        assertEquals(30, progression.experience());
    }

    @Test
    void canGainSeveralLevelsAtOnce() {
        Progression progression = new Progression();
        // 100 per il livello 2, 200 per il livello 3
        assertEquals(2, progression.gain(300));
        assertEquals(3, progression.level());
        assertEquals(0, progression.experience());
    }

    @Test
    void stopsAtMaxLevel() {
        Progression progression = new Progression(Progression.MAX_LEVEL - 1, 0);
        progression.gain(1_000_000);
        assertEquals(Progression.MAX_LEVEL, progression.level());
        assertEquals(0, progression.gain(500));
    }

    @Test
    void rejectsNegativeExperience() {
        assertThrows(IllegalArgumentException.class, () -> new Progression().gain(-5));
    }
}
