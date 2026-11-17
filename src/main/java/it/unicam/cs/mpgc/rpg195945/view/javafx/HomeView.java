package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
import it.unicam.cs.mpgc.rpg195945.engine.activity.Activity;
import it.unicam.cs.mpgc.rpg195945.model.calendar.AcademicYear;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Schermata principale dell'anno: a sinistra la scheda dello studente, a
 * destra la settimana corrente con le attività e i duelli tra cui scegliere.
 * <p>
 * Dopo ogni attività la schermata si ridisegna, così scheda e settimana sono
 * sempre aggiornate.
 */
public class HomeView {

    private static final double PANEL_WIDTH = 420;

    private final GameController controller;
    private final Consumer<Opponent> onChallenge;
    private final BorderPane root = new BorderPane();
    private String lastMessage = "";

    /**
     * @param controller  il controller, con lo studente già creato
     * @param onChallenge azione da eseguire quando si sfida un avversario
     */
    public HomeView(GameController controller, Consumer<Opponent> onChallenge) {
        this.controller = Objects.requireNonNull(controller, "controller");
        this.onChallenge = Objects.requireNonNull(onChallenge, "onChallenge");
        refresh();
    }

    public Parent getRoot() {
        return root;
    }

    /** Ridisegna la scheda e il pannello della settimana. */
    private void refresh() {
        Student student = controller.student().orElseThrow();
        root.setCenter(new StudentSheetView(student).getRoot());
        root.setRight(weekPanel());
    }

    /** Chiamato quando si preme il pulsante di un'attività. */
    private void doActivity(Activity activity) {
        lastMessage = controller.doActivity(activity);
        refresh();
    }

    private VBox weekPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(40, 30, 40, 10));
        panel.setPrefWidth(PANEL_WIDTH);
        panel.setAlignment(Pos.TOP_CENTER);

        if (controller.isYearOver()) {
            Label title = title("Fine delle lezioni");
            Label text = new Label("Le settimane di scuola sono finite: "
                    + "è il momento della Prova dell'Equinozio!");
            text.setWrapText(true);
            panel.getChildren().addAll(title, text, messageLabel());
            return panel;
        }

        Label title = title("Settimana " + controller.week() + " di " + AcademicYear.TOTAL_WEEKS);
        Label left = new Label(controller.weeksLeft() == 1
                ? "Ultima settimana prima della Prova dell'Equinozio"
                : "Mancano " + controller.weeksLeft() + " settimane alla Prova dell'Equinozio");
        left.setStyle("-fx-text-fill: #555;");
        panel.getChildren().addAll(title, left, messageLabel(), subtitle("Attività della settimana"));

        Student student = controller.student().orElseThrow();
        for (Activity activity : controller.activities()) {
            Button button = new Button(activity.name());
            button.setMaxWidth(Double.MAX_VALUE);
            button.setDisable(!activity.isAvailableFor(student));
            button.setOnAction(e -> doActivity(activity));
            Label description = new Label(activity.description());
            description.setWrapText(true);
            description.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");
            panel.getChildren().addAll(button, description);
        }

        ComboBox<Opponent> opponentBox = new ComboBox<>();
        opponentBox.getItems().setAll(controller.opponents());
        opponentBox.getSelectionModel().selectFirst();
        opponentBox.setMaxWidth(Double.MAX_VALUE);
        Label opponentDescription = new Label(opponentBox.getValue().description());
        opponentDescription.setWrapText(true);
        opponentDescription.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");
        opponentBox.setOnAction(e -> opponentDescription.setText(opponentBox.getValue().description()));
        Button challenge = new Button("Sfida");
        challenge.setMaxWidth(Double.MAX_VALUE);
        challenge.setOnAction(e -> onChallenge.accept(opponentBox.getValue()));
        panel.getChildren().addAll(subtitle("Duello"), opponentBox, opponentDescription, challenge);
        return panel;
    }

    /** L'esito dell'ultima attività, evidenziato. */
    private Label messageLabel() {
        Label message = new Label(lastMessage);
        message.setWrapText(true);
        message.setStyle("-fx-text-fill: #1e8449; -fx-font-weight: bold;");
        return message;
    }

    private static Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        return label;
    }

    private static Label subtitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        label.setPadding(new Insets(10, 0, 0, 0));
        return label;
    }
}
