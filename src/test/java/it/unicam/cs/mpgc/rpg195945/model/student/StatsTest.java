package it.unicam.cs.mpgc.rpg195945.model.student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatsTest {

    @Test
    void plusAddsEachStatistic() {
        Stats base = new Stats(100, 50, 10, 8, 6);
        Stats bonus = new Stats(20, 10, 3, 2, 1);
        assertEquals(new Stats(120, 60, 13, 10, 7), base.plus(bonus));
    }

    @Test
    void plusDoesNotChangeTheOriginal() {
        Stats base = new Stats(100, 50, 10, 8, 6);
        base.plus(new Stats(20, 10, 3, 2, 1));
        assertEquals(new Stats(100, 50, 10, 8, 6), base);
    }

    @Test
    void addingZeroChangesNothing() {
        Stats stats = new Stats(100, 50, 10, 8, 6);
        assertEquals(stats, Stats.ZERO.plus(stats));
        assertEquals(stats, stats.plus(Stats.ZERO));
    }

    @Test
    void rejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> new Stats(-1, 0, 0, 0, 0));
    }

    @Test
    void timesMultipliesEachStatistic() {
        Stats growth = new Stats(8, 4, 3, 1, 1);
        assertEquals(new Stats(24, 12, 9, 3, 3), growth.times(3));
    }

    @Test
    void timesZeroGivesZero() {
        assertEquals(Stats.ZERO, new Stats(8, 4, 3, 1, 1).times(0));
    }

    @Test
    void timesRejectsNegativeFactor() {
        assertThrows(IllegalArgumentException.class, () -> new Stats(8, 4, 3, 1, 1).times(-1));
    }
}
