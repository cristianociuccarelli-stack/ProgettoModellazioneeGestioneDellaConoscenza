package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementWheel;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.persistence.FileGameRepository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Applicazione JavaFX: crea la finestra, il controller e decide quale
 * schermata mostrare.
 * <p>
 * È l'unico punto in cui si scelgono le implementazioni concrete (la ruota
 * degli elementi, il salvataggio su file): il resto del gioco dipende solo
 * dalle interfacce.
 * <p>
 * La finestra ha una sola {@link Scene}: per cambiare schermata se ne
 * sostituisce il contenuto con {@code scene.setRoot(...)}.
 */
public class AcademyApp extends Application {

    private static final String TITLE = "Accademia Arcana";
    private static final double WIDTH = 1000;
    private static final double HEIGHT = 700;

    private final GameController controller =
            new GameController(ElementWheel.standard(), FileGameRepository.inUserHome());
    private Stage stage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        scene = new Scene(new StackPane(), WIDTH, HEIGHT);
        scene.getStylesheets().add(AcademyApp.class.getResource("academy.css").toExternalForm());
        showMenu();
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private void showMenu() {
        scene.setRoot(new MainMenuView(controller.hasSavedGame(),
                this::showNewStudent, this::continueGame, stage::close).getRoot());
    }

    private void showNewStudent() {
        scene.setRoot(new NewStudentView(controller, student -> showHome(), this::showMenu).getRoot());
    }

    private void continueGame() {
        controller.continueGame();
        showHome();
    }

    private void showHome() {
        scene.setRoot(new HomeView(controller, this::showDuel, this::showTrial, this::showGuide).getRoot());
    }

    private void showGuide() {
        scene.setRoot(new GuideView(controller, this::showHome).getRoot());
    }

    private void showDuel(Opponent opponent) {
        controller.startDuel(opponent);
        scene.setRoot(new DuelView(controller, this::showHome).getRoot());
    }

    private void showTrial() {
        controller.startTrial();
        scene.setRoot(new DuelView(controller, this::afterTrial).getRoot());
    }

    /** Dopo la Prova: se era l'ultimo guardiano si è finito, altrimenti si continua. */
    private void afterTrial() {
        if (controller.isCompleted()) {
            showEnd();
        } else {
            showHome();
        }
    }

    private void showEnd() {
        scene.setRoot(new EndView(controller, this::showNewStudent, this::showMenu).getRoot());
    }
}
