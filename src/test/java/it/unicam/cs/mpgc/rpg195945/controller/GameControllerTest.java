package it.unicam.cs.mpgc.rpg195945.controller;

import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerTest {

    @Test
    void thereIsNoStudentAtTheBeginning() {
        assertTrue(new GameController().student().isEmpty());
    }

    @Test
    void createsAndRemembersTheStudent() {
        GameController controller = new GameController();
        Student created = controller.createStudent("Lira", Affinity.AIR);
        assertEquals(created, controller.student().orElseThrow());
        assertEquals("Lira", created.name());
    }

    @Test
    void offersAllAffinities() {
        assertEquals(Affinity.all(), new GameController().affinities());
    }

    @Test
    void blankNameIsRejectedAndNoStudentIsCreated() {
        GameController controller = new GameController();
        assertThrows(IllegalArgumentException.class, () -> controller.createStudent(" ", Affinity.FIRE));
        assertTrue(controller.student().isEmpty());
    }

    @Test
    void startingStatsAreBaseStatsPlusBonus() {
        assertEquals(new Stats(130, 50, 10, 14, 10), new GameController().startingStats(Affinity.EARTH));
    }

    @Test
    void previewDoesNotCreateTheStudent() {
        GameController controller = new GameController();
        controller.startingStats(Affinity.EARTH);
        assertTrue(controller.student().isEmpty());
    }
}
