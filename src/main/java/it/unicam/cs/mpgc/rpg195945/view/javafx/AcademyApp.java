package it.unicam.cs.mpgc.rpg195945.view.javafx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Applicazione JavaFX: crea la finestra principale.
 * <p>
 * Per ora mostra solo il titolo; nelle prossime tappe sarà il punto in cui
 * vengono collegati motore di gioco, controller e schermate.
 */
public class AcademyApp extends Application {

    private static final String TITLE = "Accademia Arcana";
    private static final double WIDTH = 1000;
    private static final double HEIGHT = 700;

    @Override
    public void start(Stage stage) {
        Label title = new Label(TITLE);
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        Label subtitle = new Label("Il tuo primo anno sta per cominciare...");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        VBox root = new VBox(12, title, subtitle);
        root.setAlignment(Pos.CENTER);

        stage.setTitle(TITLE);
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.show();
    }
}
