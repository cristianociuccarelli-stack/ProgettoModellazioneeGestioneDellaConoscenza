package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
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
 * Schermata di fine anno: dice se lo studente ha superato la Prova
 * dell'Equinozio e riassume com'è cresciuto durante l'anno.
 */
public class EndView {

    private final VBox root;

    /**
     * @param controller il controller, con la Prova già affrontata
     * @param onNewGame  azione per iniziare una nuova partita
     * @param onExit     azione per uscire dal gioco
     */
    public EndView(GameController controller, Runnable onNewGame, Runnable onExit) {
        Objects.requireNonNull(controller, "controller");
        Objects.requireNonNull(onNewGame, "onNewGame");
        Objects.requireNonNull(onExit, "onExit");
        Student student = controller.student().orElseThrow();

        Label title = new Label(controller.isTrialPassed()
                ? "Prova dell'Equinozio superata!"
                : "Prova dell'Equinozio fallita");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        Label text = new Label(controller.isTrialPassed()
                ? "Hai sconfitto il Guardiano: il tuo primo anno all'Accademia Arcana è concluso con successo."
                : "Il Guardiano era troppo forte. L'anno prossimo andrà meglio!");
        text.setWrapText(true);
        text.setMaxWidth(560);
        Label summary = new Label(student.name() + ", " + student.affinity().title()
                + " di livello " + student.level() + ", con " + student.spells().size() + " incantesimi.");
        summary.setStyle("-fx-text-fill: #555;");

        Button newGame = new Button("Nuova partita");
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
