package it.unicam.cs.mpgc.rpg195945.controller;

import it.unicam.cs.mpgc.rpg195945.engine.duel.Duel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementalRule;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
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

    @Test
    void cannotDuelWithoutAStudent() {
        assertThrows(IllegalStateException.class, () -> new GameController().startDuel(Opponent.EMBER_GOLEM));
    }

    @Test
    void startsADuelAgainstTheChosenOpponent() {
        GameController controller = new GameController();
        controller.createStudent("Lira", Affinity.WATER);
        Duel duel = controller.startDuel(Opponent.EMBER_GOLEM);
        assertEquals("Lira", duel.student().name());
        assertEquals("Golem di Brace", duel.opponent().name());
        assertEquals(duel, controller.duel().orElseThrow());
        assertThrows(IllegalStateException.class, () -> controller.startDuel(Opponent.STORM_HAWK));
    }

    @Test
    void cannotFinishADuelThatIsStillGoing() {
        GameController controller = new GameController();
        controller.createStudent("Lira", Affinity.WATER);
        controller.startDuel(Opponent.EMBER_GOLEM);
        assertThrows(IllegalStateException.class, controller::finishDuel);
    }

    @Test
    void winningGivesTheOpponentsExperience() {
        // una regola che rende gli incantesimi devastanti: lo studente vince al primo colpo
        ElementalRule overwhelming = (attack, target) -> 100.0;
        GameController controller = new GameController(overwhelming);
        Student student = controller.createStudent("Lira", Affinity.AIR);
        Duel duel = controller.startDuel(Opponent.STONE_SENTINEL);
        duel.cast(student.spells().get(0));
        assertTrue(duel.studentWon());
        assertEquals(Opponent.STONE_SENTINEL.experienceReward(), controller.finishDuel());
        assertEquals(Opponent.STONE_SENTINEL.experienceReward(), student.experience());
        assertTrue(controller.duel().isEmpty());
    }

    @Test
    void losingGivesNoExperience() {
        GameController controller = new GameController();
        Student student = controller.createStudent("Lira", Affinity.EARTH);
        Duel duel = controller.startDuel(Opponent.STORM_HAWK);
        // lo studente si concentra e basta, senza mai attaccare: prima o poi perde
        while (!duel.isOver()) {
            duel.concentrate();
        }
        assertEquals(0, controller.finishDuel());
        assertEquals(0, student.experience());
    }
}
