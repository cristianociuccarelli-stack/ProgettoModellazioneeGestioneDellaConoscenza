package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Scheda dello studente: nome, affinità, livello, esperienza, statistiche e
 * incantesimi conosciuti. In fondo si sceglie un avversario da sfidare.
 * <p>
 * Mostra soltanto i dati: non modifica lo studente. Cosa succede quando si
 * lancia una sfida lo decide chi la usa, con l'azione {@code onChallenge}.
 */
public class StudentSheetView {

    private final ScrollPane root;

    /**
     * @param student     lo studente da mostrare
     * @param opponents   gli avversari che si possono sfidare
     * @param onChallenge azione da eseguire con l'avversario scelto
     */
    public StudentSheetView(Student student, List<Opponent> opponents, Consumer<Opponent> onChallenge) {
        Objects.requireNonNull(student, "student");
        Objects.requireNonNull(opponents, "opponents");
        Objects.requireNonNull(onChallenge, "onChallenge");

        Label name = new Label(student.name());
        name.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        Label affinity = new Label(student.affinity().toString());
        affinity.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        Label level = new Label("Livello " + student.level());
        level.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        ProgressBar experienceBar = new ProgressBar(
                (double) student.experience() / student.experienceForNextLevel());
        experienceBar.setPrefWidth(300);
        Label experience = new Label(student.experience() + " / "
                + student.experienceForNextLevel() + " esperienza");

        Label spellsTitle = new Label("Incantesimi");
        spellsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        spellsTitle.setPadding(new Insets(16, 0, 0, 0));
        VBox spells = new VBox(4);
        spells.setAlignment(Pos.CENTER);
        for (Spell spell : student.spells()) {
            spells.getChildren().add(new Label(spell.name() + "  ·  " + spell.effect()
                    + " " + spell.power() + "  ·  " + spell.manaCost() + " mana"));
        }

        Label duelTitle = new Label("Duelli di allenamento");
        duelTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        duelTitle.setPadding(new Insets(16, 0, 0, 0));
        ComboBox<Opponent> opponentBox = new ComboBox<>();
        opponentBox.getItems().setAll(opponents);
        opponentBox.getSelectionModel().selectFirst();
        Label opponentDescription = new Label(opponentBox.getValue().description());
        opponentDescription.setStyle("-fx-text-fill: #555;");
        opponentBox.setOnAction(e -> opponentDescription.setText(opponentBox.getValue().description()));
        Button challenge = new Button("Sfida");
        challenge.setOnAction(e -> onChallenge.accept(opponentBox.getValue()));

        VBox content = new VBox(10, name, affinity, level, experienceBar, experience,
                statsTable(student.stats()), spellsTitle, spells,
                duelTitle, opponentBox, opponentDescription, challenge);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        root = new ScrollPane(content);
        root.setFitToWidth(true);
    }

    public Parent getRoot() {
        return root;
    }

    /** Tabella a due colonne: nome della statistica e valore. */
    private static GridPane statsTable(Stats stats) {
        GridPane table = new GridPane();
        table.setHgap(24);
        table.setVgap(6);
        table.setAlignment(Pos.CENTER);
        table.setPadding(new Insets(16, 0, 0, 0));
        addRow(table, 0, "Vita", stats.health());
        addRow(table, 1, "Mana", stats.mana());
        addRow(table, 2, "Potenza", stats.power());
        addRow(table, 3, "Difesa", stats.defense());
        addRow(table, 4, "Velocità", stats.speed());
        return table;
    }

    private static void addRow(GridPane table, int row, String label, int value) {
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setStyle("-fx-font-weight: bold;");
        table.addRow(row, new Label(label), valueLabel);
    }
}
