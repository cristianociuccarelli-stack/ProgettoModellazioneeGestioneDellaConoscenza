package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.controller.GameController;
import it.unicam.cs.mpgc.rpg195945.engine.activity.Training;
import it.unicam.cs.mpgc.rpg195945.engine.duel.Duel;
import it.unicam.cs.mpgc.rpg195945.model.calendar.AcademicYear;
import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * La guida del gioco: spiega come sono bilanciate le cose (la ruota degli
 * elementi, i danni, le rune, le affinità, i guardiani).
 * <p>
 * Tutti i numeri sono letti dal codice del gioco e non scritti a mano: se si
 * cambia una regola o un valore, la guida si aggiorna da sola.
 */
public class GuideView {

    private static final double TEXT_WIDTH = 760;

    private final GameController controller;
    private final ScrollPane root;

    /**
     * @param controller il controller, con lo studente già creato
     * @param onBack     azione per tornare alla schermata principale
     */
    public GuideView(GameController controller, Runnable onBack) {
        this.controller = Objects.requireNonNull(controller, "controller");
        Objects.requireNonNull(onBack, "onBack");

        Button back = new Button("← Torna all'Accademia");
        back.setOnAction(e -> onBack.run());
        Label title = new Label("Guida dell'Accademia Arcana");
        title.getStyleClass().add("title");

        VBox content = new VBox(10, back, title);
        content.setPadding(new Insets(24, 40, 40, 40));
        content.setAlignment(Pos.TOP_LEFT);
        addElements(content);
        addDamage(content);
        addRunes(content);
        addAffinities(content);
        addYear(content);
        addOpponents(content);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
    }

    public Parent getRoot() {
        return root;
    }

    /** La ruota degli elementi, ricavata dalla regola usata nei duelli. */
    private void addElements(VBox content) {
        section(content, "La ruota degli elementi");
        List<Label> strong = new ArrayList<>();
        List<String> neutral = new ArrayList<>();
        Element[] elements = Element.values();
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < elements.length; j++) {
                Element attack = elements[i];
                Element target = elements[j];
                double multiplier = controller.elementalMultiplier(attack, target);
                if (multiplier > 1) {
                    Label line = new Label(ElementStyle.name(attack) + " batte " + ElementStyle.name(target)
                            + ": danni × " + number(multiplier)
                            + " (al contrario × " + number(controller.elementalMultiplier(target, attack)) + ")");
                    strong.add(ElementStyle.paint(line, attack));
                } else if (j > i && multiplier == 1 && controller.elementalMultiplier(target, attack) == 1) {
                    neutral.add(ElementStyle.name(attack) + " e " + ElementStyle.name(target));
                }
            }
        }
        content.getChildren().addAll(strong);
        text(content, "Coppie senza vantaggio (danni × 1): " + String.join(", ", neutral)
                + ". Anche due incantesimi dello stesso elemento sono neutri.\n"
                + "I simboli sono quelli degli antichi alchimisti: ▲ Fuoco, ▼ Acqua, △ Aria, ▽ Terra.");
    }

    private void addDamage(VBox content) {
        section(content, "Come si calcolano i danni");
        text(content, "Danni = (potenza della runa + tua potenza) × vantaggio elementale"
                + " × bonus affinità − metà della difesa dell'avversario.\n"
                + "• Bonus affinità: × " + number(Duel.AFFINITY_BONUS)
                + " se lanci un incantesimo del tuo elemento.\n"
                + "• Un colpo fa sempre almeno 1 danno.\n"
                + "• Scudo = potenza della runa + metà della tua difesa; assorbe solo il colpo successivo.\n"
                + "• Cura = potenza della runa + metà della tua potenza.\n"
                + "• Concentrarsi: salti l'attacco e recuperi " + Duel.CONCENTRATION_MANA + " mana.\n"
                + "• Agisce per primo chi ha più velocità; a parità, tu.\n"
                + "• Vita e mana persi in un duello tornano al massimo dopo il duello.");
    }

    private void addRunes(VBox content) {
        section(content, "Le rune");
        text(content, "Ogni runa si combina con ogni elemento che conosci: "
                + "con 2 elementi e 3 rune hai 6 incantesimi.");
        GridPane table = table("Runa", "Effetto", "Potenza", "Mana");
        int row = 1;
        for (Rune rune : Rune.all()) {
            table.addRow(row++, new Label(rune.name()), new Label(rune.effect().toString()),
                    new Label(String.valueOf(rune.power())), new Label(String.valueOf(rune.manaCost())));
        }
        content.getChildren().add(table);
    }

    private void addAffinities(VBox content) {
        section(content, "Le affinità");
        GridPane table = table("Affinità", "Elemento", "Caratteristica");
        int row = 1;
        for (Affinity affinity : Affinity.all()) {
            Label description = new Label(affinity.description());
            description.setWrapText(true);
            description.setMaxWidth(480);
            table.addRow(row++, new Label(affinity.title()), elementLabel(affinity.element()), description);
        }
        content.getChildren().add(table);
    }

    private void addYear(VBox content) {
        section(content, "Settimane e Prove dell'Equinozio");
        text(content, "• Il percorso dura " + AcademicYear.LAST_YEAR + " anni; ogni anno ha "
                + AcademicYear.TOTAL_WEEKS + " settimane e in ogni settimana fai una sola attività.\n"
                + "• Allenamento: +" + Training.EXPERIENCE + " esperienza, sempre disponibile.\n"
                + "• Lezione di rune / di elementi: impari la prima runa o il primo elemento che non conosci.\n"
                + "• Duello: se vinci guadagni l'esperienza dell'avversario, se perdi nulla.\n"
                + "• A fine anno affronti il guardiano: se vinci passi all'anno dopo, "
                + "se perdi ripeti l'anno senza perdere livello né incantesimi.\n"
                + "• Consiglio: guarda l'elemento del guardiano e impara a lezione l'elemento che lo batte.");
    }

    private void addOpponents(VBox content) {
        section(content, "Avversari dei duelli");
        GridPane opponents = table("Avversario", "Elemento", "Esperienza");
        int row = 1;
        for (Opponent opponent : controller.opponents()) {
            opponents.addRow(row++, new Label(opponent.name()), elementLabel(opponent.element()),
                    new Label("+" + opponent.experienceReward()));
        }
        content.getChildren().add(opponents);

        section(content, "I guardiani");
        GridPane guardians = table("Anno", "Guardiano", "Elemento", "Vita", "Potenza", "Velocità");
        row = 1;
        for (Opponent guardian : controller.guardians()) {
            guardians.addRow(row, new Label(String.valueOf(row)), new Label(guardian.name()),
                    elementLabel(guardian.element()), new Label(String.valueOf(guardian.stats().health())),
                    new Label(String.valueOf(guardian.stats().power())),
                    new Label(String.valueOf(guardian.stats().speed())));
            row++;
        }
        content.getChildren().add(guardians);
    }

    private static void section(VBox content, String title) {
        Label label = new Label(title);
        label.getStyleClass().add("subtitle");
        label.setPadding(new Insets(14, 0, 0, 0));
        content.getChildren().add(label);
    }

    private static void text(VBox content, String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(TEXT_WIDTH);
        content.getChildren().add(label);
    }

    /** Una tabella con la riga di intestazione in grassetto. */
    private static GridPane table(String... headers) {
        GridPane table = new GridPane();
        table.setHgap(24);
        table.setVgap(4);
        for (int column = 0; column < headers.length; column++) {
            Label header = new Label(headers[column]);
            header.getStyleClass().addAll("strong", "muted");
            table.add(header, column, 0);
        }
        return table;
    }

    private static Label elementLabel(Element element) {
        return ElementStyle.paint(new Label(ElementStyle.name(element)), element);
    }

    /** 1.5 diventa "1,5": i numeri scritti all'italiana. */
    private static String number(double value) {
        return String.valueOf(value).replace('.', ',');
    }
}
