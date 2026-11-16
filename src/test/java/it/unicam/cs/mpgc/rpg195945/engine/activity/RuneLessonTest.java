package it.unicam.cs.mpgc.rpg195945.engine.activity;

import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuneLessonTest {

    private final RuneLesson lesson = new RuneLesson();

    @Test
    void teachesTheFirstUnknownRune() {
        Student student = new Student("Lira", Affinity.FIRE);
        // conosce Dardo e Barriera: la prima che manca, nell'ordine di Rune.all(), è la Lancia
        lesson.perform(student);
        assertTrue(student.knows(Rune.LANCE));
        assertEquals(3, student.spells().size());
    }

    @Test
    void isNotAvailableWhenAllRunesAreKnown() {
        Student student = new Student("Lira", Affinity.FIRE);
        assertTrue(lesson.isAvailableFor(student));
        lesson.perform(student);
        lesson.perform(student);
        assertFalse(lesson.isAvailableFor(student));
        assertThrows(IllegalStateException.class, () -> lesson.perform(student));
    }
}
