package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
import it.unicam.cs.mpgc.rpg195945.model.calendar.AcademicYear;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Schermata finale: lo studente ha battuto tutti i guardiani e si è
 * diplomato. Riassume com'è cresciuto durante il percorso.
 */
public class EndView {

    private final VBox root;

    /**
     * @param controller il controller, con il percorso concluso
     * @param onNewGame  azione per iniziare una nuova partita
     * @param onExit     azione per uscire dal gioco
     */
    public EndView(GameController controller, Runnable onNewGame, Runnable onExit) {
        Objects.requireNonNull(controller, "controller");
        Objects.requireNonNull(onNewGame, "onNewGame");
        Objects.requireNonNull(onExit, "onExit");
        Student student = controller.student().orElseThrow();

        Label title = new Label("Ti sei diplomato all'Accademia Arcana!");
        title.getStyleClass().add("title");
        Label text = new Label("Hai sconfitto tutti e " + AcademicYear.LAST_YEAR
                + " i guardiani delle Prove dell'Equinozio. Il tuo percorso è concluso.");
        text.setWrapText(true);
        text.setMaxWidth(560);
        Label summary = new Label(student.name() + ", " + student.affinity().title()
                + " di livello " + student.level() + ", con " + student.spells().size() + " incantesimi"
                + (controller.failedTrials() == 0
                        ? ", senza mai fallire una Prova."
                        : ". Prove fallite lungo il percorso: " + controller.failedTrials() + "."));
        summary.setWrapText(true);
        summary.setMaxWidth(560);
        summary.getStyleClass().add("muted");

        Button newGame = new Button("Nuova partita");
        newGame.getStyleClass().add("primary");
        newGame.setOnAction(e -> onNewGame.run());
        Button exit = new Button("Esci");
        exit.setOnAction(e -> onExit.run());
        HBox buttons = new HBox(12, newGame, exit);
        buttons.setAlignment(Pos.CENTER);

        root = new VBox(16, title, text, summary, buttons);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
    }

    public Parent getRoot() {
        return root;
    }
}
