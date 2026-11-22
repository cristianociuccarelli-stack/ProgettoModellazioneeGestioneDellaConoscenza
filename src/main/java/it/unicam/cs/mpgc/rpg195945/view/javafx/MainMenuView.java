package it.unicam.cs.mpgc.rpg195945.view.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Menu principale: nuova partita, continua la partita salvata, esci.
 * <p>
 * Come le altre schermate non sa cosa succede dopo: riceve le azioni da
 * eseguire per ogni pulsante.
 */
public class MainMenuView {

    private static final double BUTTON_WIDTH = 260;

    private final VBox root;

    /**
     * @param canContinue {@code true} se c'è una partita salvata da continuare
     * @param onNewGame   azione per iniziare una nuova partita
     * @param onContinue  azione per continuare la partita salvata
     * @param onExit      azione per uscire dal gioco
     */
    public MainMenuView(boolean canContinue, Runnable onNewGame, Runnable onContinue, Runnable onExit) {
        Objects.requireNonNull(onNewGame, "onNewGame");
        Objects.requireNonNull(onContinue, "onContinue");
        Objects.requireNonNull(onExit, "onExit");

        Label title = new Label("Accademia Arcana");
        title.getStyleClass().add("title");
        title.setStyle("-fx-font-size: 44px;");
        Label subtitle = new Label("▲ ▼ △ ▽   Cinque anni, cinque guardiani, un solo diploma   ▲ ▼ △ ▽");
        subtitle.getStyleClass().add("muted");

        Button continueGame = button("Continua", onContinue);
        continueGame.getStyleClass().add("primary");
        continueGame.setDisable(!canContinue);
        Button newGame = button("Nuova partita", onNewGame);
        Button exit = button("Esci", onExit);
        Label hint = new Label(canContinue
                ? "La partita si salva da sola alla fine di ogni settimana."
                : "Nessuna partita salvata: inizia una nuova partita.");
        hint.getStyleClass().add("small");

        root = new VBox(14, title, subtitle, spacer(), continueGame, newGame, exit, hint);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
    }

    public Parent getRoot() {
        return root;
    }

    private static Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setOnAction(e -> action.run());
        return button;
    }

    private static Label spacer() {
        Label spacer = new Label();
        spacer.setMinHeight(20);
        return spacer;
    }
}
