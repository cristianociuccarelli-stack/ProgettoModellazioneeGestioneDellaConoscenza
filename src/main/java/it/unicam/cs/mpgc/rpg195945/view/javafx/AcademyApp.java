package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Applicazione JavaFX: crea la finestra, il controller e decide quale
 * schermata mostrare.
 * <p>
 * La finestra ha una sola {@link Scene}: per cambiare schermata se ne
 * sostituisce il contenuto con {@code scene.setRoot(...)}.
 */
public class AcademyApp extends Application {

    private static final String TITLE = "Accademia Arcana";
    private static final double WIDTH = 1000;
    private static final double HEIGHT = 700;

    private final GameController controller = new GameController();
    private Scene scene;

    @Override
    public void start(Stage stage) {
        scene = new Scene(new StackPane(), WIDTH, HEIGHT);
        showNewStudent();
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private void showNewStudent() {
        scene.setRoot(new NewStudentView(controller, this::showStudentSheet).getRoot());
    }

    private void showStudentSheet(Student student) {
        scene.setRoot(new StudentSheetView(student).getRoot());
    }
}
