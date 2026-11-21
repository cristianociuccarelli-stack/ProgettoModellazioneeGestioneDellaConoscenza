package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
import it.unicam.cs.mpgc.rpg195945.engine.duel.Combatant;
import it.unicam.cs.mpgc.rpg195945.engine.duel.Duel;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Schermata del duello: in alto i due sfidanti con vita, mana e scudo, al
 * centro la cronaca dei turni, in basso un pulsante per ogni incantesimo e uno
 * per concentrarsi.
 * <p>
 * Ogni pulsante chiede al duello di giocare un turno e poi aggiorna la
 * schermata. Le regole sono tutte nel motore: qui si mostra soltanto.
 */
public class DuelView {

    private final GameController controller;
    private final Duel duel;
    private final Runnable onClose;

    private final BorderPane root = new BorderPane();
    private final CombatantPanel studentPanel;
    private final CombatantPanel opponentPanel;
    private final ListView<String> log = new ListView<>();
    private final List<Button> spellButtons = new ArrayList<>();
    private final Button concentrateButton = new Button("Concentrati (+" + Duel.CONCENTRATION_MANA + " mana)");
    private final VBox bottom = new VBox(10);

    /**
     * @param controller il controller, con un duello già iniziato
     * @param onClose    azione da eseguire quando il giocatore lascia il duello
     */
    public DuelView(GameController controller, Runnable onClose) {
        this.controller = Objects.requireNonNull(controller, "controller");
        this.onClose = Objects.requireNonNull(onClose, "onClose");
        this.duel = controller.duel()
                .orElseThrow(() -> new IllegalStateException("Nessun duello in corso"));

        studentPanel = new CombatantPanel(duel.student());
        opponentPanel = new CombatantPanel(duel.opponent());
        Label versus = new Label("contro");
        versus.getStyleClass().addAll("subtitle", "muted");
        HBox top = new HBox(40, studentPanel.root, versus, opponentPanel.root);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20));
        root.setTop(top);

        log.setCellFactory(list -> new LogCell(duel.student().name()));
        log.getItems().add("Il duello contro " + duel.opponent().name() + " ha inizio!");
        BorderPane.setMargin(log, new Insets(0, 20, 0, 20));
        root.setCenter(log);

        FlowPane actions = new FlowPane(10, 10);
        actions.setAlignment(Pos.CENTER);
        for (Spell spell : duel.student().spells()) {
            Button button = ElementStyle.paint(new Button(ElementStyle.symbol(spell.element()) + " "
                    + spell.name() + " (" + spell.manaCost() + " mana)"), spell.element());
            button.setOnAction(e -> castSpell(spell));
            spellButtons.add(button);
            actions.getChildren().add(button);
        }
        concentrateButton.setOnAction(e -> concentrate());
        actions.getChildren().add(concentrateButton);

        bottom.getChildren().add(actions);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(20));
        root.setBottom(bottom);

        refresh();
    }

    public Parent getRoot() {
        return root;
    }

    /** Chiamato quando si preme il pulsante di un incantesimo. */
    private void castSpell(Spell spell) {
        showTurn(duel.cast(spell));
    }

    /** Chiamato quando si preme "Concentrati". */
    private void concentrate() {
        showTurn(duel.concentrate());
    }

    /** Aggiunge la cronaca del turno e aggiorna la schermata. */
    private void showTurn(List<String> turn) {
        log.getItems().add("— Turno " + duel.round() + " —");
        log.getItems().addAll(turn);
        log.scrollTo(log.getItems().size() - 1);
        refresh();
        if (duel.isOver()) {
            showResult();
        }
    }

    /** Aggiorna le barre e disattiva gli incantesimi che non si possono lanciare. */
    private void refresh() {
        studentPanel.refresh();
        opponentPanel.refresh();
        List<Spell> spells = duel.student().spells();
        for (int i = 0; i < spells.size(); i++) {
            spellButtons.get(i).setDisable(!duel.student().canCast(spells.get(i)));
        }
    }

    /** Fine del duello: niente più mosse, si mostra il risultato. */
    private void showResult() {
        for (Button button : spellButtons) {
            button.setDisable(true);
        }
        concentrateButton.setDisable(true);

        boolean won = duel.studentWon();
        int reward = controller.finishDuel();
        String text;
        if (!won) {
            text = "Sconfitta... Allenati e riprova.";
        } else if (reward > 0) {
            text = "Vittoria! Hai guadagnato " + reward + " punti esperienza.";
        } else {
            text = "Vittoria!";
        }
        Label result = new Label(text);
        result.getStyleClass().add("result");
        Button back = new Button("Continua");
        back.getStyleClass().add("primary");
        back.setOnAction(e -> onClose.run());
        bottom.getChildren().addAll(result, back);
    }

    /**
     * Una riga della cronaca, colorata in base a chi ha agito: verde lo
     * studente, rosso l'avversario, oro i turni e la vittoria. I colpi
     * super efficaci sono in grassetto.
     */
    private static class LogCell extends ListCell<String> {

        private static final List<String> LOG_CLASSES =
                List.of("log-turn", "log-student", "log-opponent", "log-strong", "log-win", "log-lose");

        private final String studentName;

        LogCell(String studentName) {
            this.studentName = studentName;
        }

        @Override
        protected void updateItem(String line, boolean empty) {
            super.updateItem(line, empty);
            getStyleClass().removeAll(LOG_CLASSES);
            if (empty || line == null) {
                setText(null);
                return;
            }
            setText(line);
            if (line.startsWith("—") || line.startsWith("Il duello")) {
                getStyleClass().add("log-turn");
            } else if (line.contains("Hai vinto")) {
                getStyleClass().add("log-win");
            } else if (line.startsWith("Sei stato sconfitto")) {
                getStyleClass().add("log-lose");
            } else if (line.startsWith(studentName)) {
                getStyleClass().add("log-student");
            } else {
                getStyleClass().add("log-opponent");
            }
            if (line.contains("super efficace")) {
                getStyleClass().add("log-strong");
            }
        }
    }

    /** Il riquadro di uno dei due sfidanti. */
    private static class CombatantPanel {

        private static final double BAR_WIDTH = 220;

        private final Combatant combatant;
        private final VBox root;
        private final ProgressBar healthBar = new ProgressBar();
        private final Label healthText = new Label();
        private final ProgressBar manaBar = new ProgressBar();
        private final Label manaText = new Label();
        private final Label shieldText = new Label();

        CombatantPanel(Combatant combatant) {
            this.combatant = combatant;
            Label name = new Label(combatant.name());
            name.getStyleClass().add("heading");
            Label element = ElementStyle.paint(new Label(ElementStyle.name(combatant.element())),
                    combatant.element());
            healthBar.setPrefWidth(BAR_WIDTH);
            healthBar.getStyleClass().add("health-bar");
            manaBar.setPrefWidth(BAR_WIDTH);
            manaBar.getStyleClass().add("mana-bar");
            root = new VBox(6, name, element, healthBar, healthText, manaBar, manaText, shieldText);
            root.setAlignment(Pos.CENTER);
        }

        void refresh() {
            int maxHealth = combatant.stats().health();
            int maxMana = combatant.stats().mana();
            healthBar.setProgress((double) combatant.health() / maxHealth);
            healthText.setText("Vita " + combatant.health() + " / " + maxHealth);
            manaBar.setProgress(maxMana == 0 ? 0 : (double) combatant.mana() / maxMana);
            manaText.setText("Mana " + combatant.mana() + " / " + maxMana);
            shieldText.setText(combatant.shield() > 0 ? "Scudo " + combatant.shield() : " ");
        }
    }
}
