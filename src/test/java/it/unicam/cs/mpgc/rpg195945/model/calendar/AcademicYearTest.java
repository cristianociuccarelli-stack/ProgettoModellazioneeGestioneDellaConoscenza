package it.unicam.cs.mpgc.rpg195945.model.calendar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcademicYearTest {

    /** Un anno arrivato alla Prova: tutte le settimane passate. */
    private static AcademicYear finishedYear(int year) {
        return new AcademicYear(year, AcademicYear.TOTAL_WEEKS + 1);
    }

    @Test
    void startsInTheFirstWeekOfTheFirstYear() {
        AcademicYear year = new AcademicYear();
        assertEquals(1, year.year());
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
        AcademicYear year = new AcademicYear(1, AcademicYear.TOTAL_WEEKS);
        assertFalse(year.isOver());
        assertEquals(1, year.weeksLeft());
    }

    @Test
    void passingTheTrialStartsTheNextYear() {
        AcademicYear year = finishedYear(2);
        year.nextYear();
        assertEquals(3, year.year());
        assertEquals(1, year.week());
    }

    @Test
    void failingTheTrialRepeatsTheSameYear() {
        AcademicYear year = finishedYear(2);
        year.repeatYear();
        assertEquals(2, year.year());
        assertEquals(1, year.week());
    }

    @Test
    void cannotChangeYearBeforeTheTrial() {
        AcademicYear year = new AcademicYear();
        assertThrows(IllegalStateException.class, year::nextYear);
        assertThrows(IllegalStateException.class, year::repeatYear);
    }

    @Test
    void thereIsNoYearAfterTheLast() {
        AcademicYear year = finishedYear(AcademicYear.LAST_YEAR);
        assertTrue(year.isLastYear());
        assertThrows(IllegalStateException.class, year::nextYear);
    }

    @Test
    void rejectsInvalidYearsAndWeeks() {
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear(AcademicYear.LAST_YEAR + 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear(1, 0));
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear(1, AcademicYear.TOTAL_WEEKS + 2));
    }
}
