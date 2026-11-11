package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Schermata di iscrizione: il giocatore sceglie il nome e l'affinità
 * elementale del suo studente.
 * <p>
 * Non sa cosa succede dopo la creazione: lo decide chi la usa, passando
 * l'azione {@code onCreated}. Così questa schermata resta indipendente dalle
 * altre.
 */
public class NewStudentView {

    private static final double FIELD_WIDTH = 300;
    private static final double TEXT_WIDTH = 420;

    private final GameController controller;
    private final Consumer<Student> onCreated;
    private final VBox root;
    private final TextField nameField = new TextField();
    private final ComboBox<Affinity> affinityBox = new ComboBox<>();
    private final Label description = new Label();
    private final Label statsPreview = new Label();
    private final Label error = new Label();

    /**
     * @param controller il controller del gioco
     * @param onCreated  azione da eseguire con lo studente appena creato
     */
    public NewStudentView(GameController controller, Consumer<Student> onCreated) {
        this.controller = Objects.requireNonNull(controller, "controller");
        this.onCreated = Objects.requireNonNull(onCreated, "onCreated");

        Label title = new Label("Iscrizione all'Accademia");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        nameField.setPromptText("Il tuo nome");
        nameField.setMaxWidth(FIELD_WIDTH);

        affinityBox.getItems().setAll(controller.affinities());
        affinityBox.getSelectionModel().selectFirst();
        affinityBox.setOnAction(e -> showSelectedAffinity());

        description.setWrapText(true);
        description.setMaxWidth(TEXT_WIDTH);
        statsPreview.setStyle("-fx-text-fill: #555;");
        error.setStyle("-fx-text-fill: #c0392b;");

        Button start = new Button("Inizia l'anno");
        start.setDefaultButton(true);
        start.setOnAction(e -> createStudent());

        root = new VBox(10, title,
                new Label("Nome"), nameField,
                new Label("Affinità elementale"), affinityBox,
                description, statsPreview,
                start, error);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        showSelectedAffinity();
    }

    public Parent getRoot() {
        return root;
    }

    /** Chiamato quando si preme "Inizia l'anno". */
    private void createStudent() {
        try {
            Student student = controller.createStudent(nameField.getText(), affinityBox.getValue());
            onCreated.accept(student);
        } catch (IllegalArgumentException ex) {
            error.setText(ex.getMessage());
        }
    }

    /** Chiamato quando si cambia affinità nel menu. */
    private void showSelectedAffinity() {
        Affinity affinity = affinityBox.getValue();
        description.setText(affinity.description());
        Stats stats = controller.startingStats(affinity);
        statsPreview.setText("Vita " + stats.health()
                + "  ·  Mana " + stats.mana()
                + "  ·  Potenza " + stats.power()
                + "  ·  Difesa " + stats.defense()
                + "  ·  Velocità " + stats.speed());
    }
}
