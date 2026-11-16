package it.unicam.cs.mpgc.rpg195945.model.calendar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcademicYearTest {

    @Test
    void startsInTheFirstWeek() {
        AcademicYear year = new AcademicYear();
        assertEquals(1, year.week());
        assertEquals(AcademicYear.TOTAL_WEEKS, year.weeksLeft());
        assertFalse(year.isOver());
    }

    @Test
    void endsAfterTheLastWeek() {
        AcademicYear year = new AcademicYear();
        for (int i = 0; i < AcademicYear.TOTAL_WEEKS; i++) {
            year.advance();
        }
        assertTrue(year.isOver());
        assertEquals(0, year.weeksLeft());
        assertThrows(IllegalStateException.class, year::advance);
    }

    @Test
    void lastWeekIsNotOverYet() {
        AcademicYear year = new AcademicYear(AcademicYear.TOTAL_WEEKS);
        assertFalse(year.isOver());
        assertEquals(1, year.weeksLeft());
    }

    @Test
    void rejectsInvalidWeeks() {
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear(0));
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear(AcademicYear.TOTAL_WEEKS + 2));
    }
}
