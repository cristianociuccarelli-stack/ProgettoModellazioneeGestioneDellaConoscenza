package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        scene.setRoot(new NewStudentView(controller, this::showWelcome).getRoot());
    }

    /** Provvisoria: nel prossimo pezzo diventerà la scheda dello studente. */
    private void showWelcome(Student student) {
        Label welcome = new Label("Benvenuto all'Accademia, " + student.name() + "!");
        welcome.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        VBox root = new VBox(12, welcome, new Label(student.toString()));
        root.setAlignment(Pos.CENTER);
        scene.setRoot(root);
    }
}
