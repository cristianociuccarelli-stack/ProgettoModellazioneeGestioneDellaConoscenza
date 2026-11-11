package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Scheda dello studente: nome, affinità, livello, esperienza e statistiche.
 * <p>
 * Mostra soltanto i dati: non modifica lo studente.
 */
public class StudentSheetView {

    private final VBox root;

    public StudentSheetView(Student student) {
        Objects.requireNonNull(student, "student");

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

        root = new VBox(10, name, affinity, level, experienceBar, experience, statsTable(student.stats()));
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
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
