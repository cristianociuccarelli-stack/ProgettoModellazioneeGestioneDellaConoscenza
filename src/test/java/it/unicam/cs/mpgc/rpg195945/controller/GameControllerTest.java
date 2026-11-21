package it.unicam.cs.mpgc.rpg195945.controller;

import it.unicam.cs.mpgc.rpg195945.engine.activity.RuneLesson;
import it.unicam.cs.mpgc.rpg195945.engine.activity.Training;
import it.unicam.cs.mpgc.rpg195945.engine.duel.Duel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementWheel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementalRule;
import it.unicam.cs.mpgc.rpg195945.model.calendar.AcademicYear;
import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void theYearStartsWhenTheStudentIsCreated() {
        GameController controller = new GameController();
        assertThrows(IllegalStateException.class, controller::week);
        controller.createStudent("Lira", Affinity.FIRE);
        assertEquals(1, controller.week());
        assertEquals(AcademicYear.TOTAL_WEEKS, controller.weeksLeft());
    }

    @Test
    void anActivityChangesTheStudentAndTakesTheWholeWeek() {
        GameController controller = new GameController();
        Student student = controller.createStudent("Lira", Affinity.FIRE);
        controller.doActivity(new RuneLesson());
        assertTrue(student.knows(Rune.LANCE));
        assertEquals(2, controller.week());
    }

    @Test
    void anUnavailableActivityIsRejectedAndTheWeekDoesNotPass() {
        GameController controller = new GameController();
        controller.createStudent("Lira", Affinity.FIRE);
        controller.doActivity(new RuneLesson());
        controller.doActivity(new RuneLesson());
        assertThrows(IllegalArgumentException.class, () -> controller.doActivity(new RuneLesson()));
        assertEquals(3, controller.week());
    }

    @Test
    void aDuelTakesTheWholeWeek() {
        GameController controller = new GameController();
        controller.createStudent("Lira", Affinity.EARTH);
        Duel duel = controller.startDuel(Opponent.STORM_HAWK);
        assertThrows(IllegalStateException.class, () -> controller.doActivity(new Training()));
        while (!duel.isOver()) {
            duel.concentrate();
        }
        controller.finishDuel();
        assertEquals(2, controller.week());
    }

    @Test
    void nothingCanBeDoneWhenTheYearIsOver() {
        GameController controller = new GameController();
        controller.createStudent("Lira", Affinity.FIRE);
        trainUntilTheTrial(controller);
        assertTrue(controller.isYearOver());
        assertThrows(IllegalStateException.class, () -> controller.doActivity(new Training()));
        assertThrows(IllegalStateException.class, () -> controller.startDuel(Opponent.EMBER_GOLEM));
    }

    /** Regola che rende devastanti gli incantesimi d'Acqua: lo studente (Idromante) vince al primo colpo. */
    private static final ElementalRule WATER_IS_DEVASTATING =
            (attack, target) -> attack == Element.WATER ? 100.0 : 1.0;

    /** Un controller con lo studente creato e le settimane del primo anno passate ad allenarsi. */
    private static GameController controllerAtTheEndOfTheYear(ElementalRule rule) {
        GameController controller = new GameController(rule);
        controller.createStudent("Lira", Affinity.WATER);
        trainUntilTheTrial(controller);
        return controller;
    }

    private static void trainUntilTheTrial(GameController controller) {
        while (!controller.isYearOver()) {
            controller.doActivity(new Training());
        }
    }

    /** Gioca la Prova: con {@code attack} lo studente attacca, altrimenti si concentra soltanto. */
    private static void playTrial(GameController controller, boolean attack) {
        Duel trial = controller.startTrial();
        Student student = controller.student().orElseThrow();
        while (!trial.isOver()) {
            if (attack) {
                trial.cast(student.spells().get(0));
            } else {
                trial.concentrate();
            }
        }
        controller.finishDuel();
    }

    @Test
    void theTrialCanOnlyBeTakenAtTheEndOfTheYear() {
        GameController controller = new GameController();
        controller.createStudent("Lira", Affinity.WATER);
        assertThrows(IllegalStateException.class, controller::startTrial);
    }

    @Test
    void eachYearHasItsOwnGuardian() {
        GameController controller = controllerAtTheEndOfTheYear(ElementWheel.standard());
        assertEquals("Guardiano della Soglia", controller.trialGuardian().name());
    }

    @Test
    void winningTheTrialStartsTheNextYearWithTheSameStudent() {
        GameController controller = controllerAtTheEndOfTheYear(WATER_IS_DEVASTATING);
        Student student = controller.student().orElseThrow();
        playTrial(controller, true);
        assertEquals(2, controller.year());
        assertEquals(1, controller.week());
        assertEquals(student, controller.student().orElseThrow());
        assertEquals("Guardiana delle Maree", controller.trialGuardian().name());
    }

    @Test
    void losingTheTrialRepeatsTheYearAndKeepsTheProgress() {
        GameController controller = controllerAtTheEndOfTheYear(ElementWheel.standard());
        int level = controller.student().orElseThrow().level();
        playTrial(controller, false);
        assertEquals(1, controller.year());
        assertEquals(1, controller.week());
        assertEquals(1, controller.failedTrials());
        assertEquals(level, controller.student().orElseThrow().level());
    }

    @Test
    void beatingAllTheGuardiansCompletesTheGame() {
        GameController controller = new GameController(WATER_IS_DEVASTATING);
        controller.createStudent("Lira", Affinity.WATER);
        for (int year = 1; year <= AcademicYear.LAST_YEAR; year++) {
            trainUntilTheTrial(controller);
            playTrial(controller, true);
        }
        assertTrue(controller.isCompleted());
        assertEquals(AcademicYear.LAST_YEAR, controller.year());
        assertThrows(IllegalStateException.class, controller::startTrial);
    }

    @Test
    void aNewStudentStartsANewGame() {
        GameController controller = controllerAtTheEndOfTheYear(ElementWheel.standard());
        playTrial(controller, false);
        controller.createStudent("Nuovo", Affinity.FIRE);
        assertEquals(1, controller.year());
        assertEquals(1, controller.week());
        assertEquals(0, controller.failedTrials());
        assertFalse(controller.isCompleted());
    }

    @Test
    void explainsTheElementalRule() {
        GameController controller = new GameController();
        assertEquals(ElementWheel.STRONG, controller.elementalMultiplier(Element.WATER, Element.FIRE));
        assertEquals(ElementWheel.WEAK, controller.elementalMultiplier(Element.FIRE, Element.WATER));
    }

    @Test
    void listsTheGuardiansOfAllTheYears() {
        GameController controller = new GameController();
        controller.createStudent("Lira", Affinity.FIRE);
        assertEquals(AcademicYear.LAST_YEAR, controller.guardians().size());
        assertEquals("Guardiano della Soglia", controller.guardians().get(0).name());
    }
}
