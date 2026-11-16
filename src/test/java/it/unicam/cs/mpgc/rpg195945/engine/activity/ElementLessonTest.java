package it.unicam.cs.mpgc.rpg195945.engine.activity;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElementLessonTest {

    private final ElementLesson lesson = new ElementLesson();

    @Test
    void teachesTheFirstUnknownElement() {
        Student student = new Student("Lira", Affinity.FIRE);
        // conosce già il Fuoco: il primo che manca è l'Acqua
        lesson.perform(student);
        assertTrue(student.knows(Element.WATER));
        // 2 elementi x 2 rune iniziali
        assertEquals(4, student.spells().size());
    }

    @Test
    void isNotAvailableWhenAllElementsAreKnown() {
        Student student = new Student("Lira", Affinity.AIR);
        for (int i = 0; i < 3; i++) {
            lesson.perform(student);
        }
        assertFalse(lesson.isAvailableFor(student));
        assertThrows(IllegalStateException.class, () -> lesson.perform(student));
    }
}
