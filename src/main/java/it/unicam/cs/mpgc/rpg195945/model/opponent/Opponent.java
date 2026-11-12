package it.unicam.cs.mpgc.rpg195945.model.opponent;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Un avversario da affrontare in duello.
 * <p>
 * Come le affinità e le rune, gli avversari sono contenuti: se ne aggiungono
 * di nuovi creando altri oggetti, senza modificare questa classe. Ogni
 * avversario ha un elemento, che decide quali incantesimi sono efficaci
 * contro di lui.
 *
 * @param name             nome dell'avversario
 * @param description      breve descrizione da mostrare al giocatore
 * @param element          il suo elemento
 * @param stats            le sue statistiche
 * @param spells           gli incantesimi che può lanciare
 * @param experienceReward esperienza guadagnata dallo studente se vince
 */
public record Opponent(String name, String description, Element element, Stats stats,
                       List<Spell> spells, int experienceReward) {

    public static final Opponent EMBER_GOLEM = new Opponent("Golem di Brace",
            "Un ammasso di carbone ardente: colpisce forte, ma è lento.",
            Element.FIRE, new Stats(90, 40, 12, 6, 6),
            spellsOf(Element.FIRE, Rune.BOLT, Rune.LANCE), 40);

    public static final Opponent TIDE_SPIRIT = new Opponent("Spirito della Marea",
            "Sfuggente come un'onda: quando è ferito si cura.",
            Element.WATER, new Stats(80, 60, 9, 8, 8),
            spellsOf(Element.WATER, Rune.BOLT, Rune.SPRING), 40);

    public static final Opponent STONE_SENTINEL = new Opponent("Sentinella di Pietra",
            "Il guardiano delle scale dell'Accademia: resistentissimo.",
            Element.EARTH, new Stats(120, 30, 8, 12, 3),
            spellsOf(Element.EARTH, Rune.BOLT, Rune.WARD), 40);

    public static final Opponent STORM_HAWK = new Opponent("Falco di Tempesta",
            "Piomba dal cielo prima che tu possa reagire, ma è fragile.",
            Element.AIR, new Stats(70, 50, 11, 5, 14),
            spellsOf(Element.AIR, Rune.BOLT, Rune.LANCE), 40);

    public Opponent {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(element, "element");
        Objects.requireNonNull(stats, "stats");
        Objects.requireNonNull(spells, "spells");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Un avversario deve avere un nome");
        }
        if (spells.isEmpty()) {
            throw new IllegalArgumentException("Un avversario deve conoscere almeno un incantesimo");
        }
        if (experienceReward < 0) {
            throw new IllegalArgumentException("La ricompensa non può essere negativa");
        }
        spells = List.copyOf(spells);
    }

    /**
     * @return tutti gli avversari dei duelli di allenamento
     */
    public static List<Opponent> all() {
        return List.of(EMBER_GOLEM, TIDE_SPIRIT, STONE_SENTINEL, STORM_HAWK);
    }

    @Override
    public String toString() {
        return name + " (" + element.displayName() + ")";
    }

    /** Crea gli incantesimi di un elemento con le rune indicate. */
    private static List<Spell> spellsOf(Element element, Rune... runes) {
        List<Spell> spells = new ArrayList<>();
        for (Rune rune : runes) {
            spells.add(new Spell(element, rune));
        }
        return spells;
    }
}
