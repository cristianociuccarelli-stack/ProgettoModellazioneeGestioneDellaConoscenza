package it.unicam.cs.mpgc.rpg195945.engine.activity;

import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainingTest {

    @Test
    void givesExperience() {
        Student student = new Student("Lira", Affinity.FIRE);
        new Training().perform(student);
        assertEquals(Training.EXPERIENCE, student.experience());
    }

    @Test
    void isAlwaysAvailable() {
        assertTrue(new Training().isAvailableFor(new Student("Lira", Affinity.FIRE)));
    }

    @Test
    void tellsWhenTheStudentLevelsUp() {
        Student student = new Student("Lira", Affinity.FIRE);
        student.gainExperience(80);
        String message = new Training().perform(student);
        assertEquals(2, student.level());
        assertTrue(message.contains("livello 2"), message);
    }
}
