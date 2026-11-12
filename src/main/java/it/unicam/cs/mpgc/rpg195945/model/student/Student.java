package it.unicam.cs.mpgc.rpg195945.model.student;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spellbook;

import java.util.List;
import java.util.Objects;

/**
 * Lo studente dell'Accademia: il personaggio controllato dal giocatore.
 * <p>
 * Mette insieme un nome, un'affinità elementale (scelta all'inizio, non cambia
 * più), una progressione di livello e un libro degli incantesimi. Le statistiche non sono salvate in un
 * campo: vengono calcolate ogni volta a partire dall'affinità e dal livello,
 * così non possono mai essere "sbagliate" rispetto a questi due dati.
 */
public final class Student {

    /** Statistiche di partenza, uguali per tutti gli studenti. */
    public static final Stats BASE_STATS = new Stats(100, 50, 10, 10, 10);

    private final String name;
    private final Affinity affinity;
    private final Progression progression;
    private final Spellbook spellbook;

    /**
     * Crea un nuovo studente al livello 1, che conosce solo l'elemento della
     * sua affinità e le rune iniziali.
     */
    public Student(String name, Affinity affinity) {
        this(name, affinity, new Progression(),
                new Spellbook(Objects.requireNonNull(affinity, "affinity").element()));
    }

    /**
     * Costruttore completo, utile per ricaricare uno studente salvato.
     *
     * @throws IllegalArgumentException se il nome è vuoto
     */
    public Student(String name, Affinity affinity, Progression progression, Spellbook spellbook) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Il nome dello studente non può essere vuoto");
        }
        this.name = name.strip();
        this.affinity = Objects.requireNonNull(affinity, "affinity");
        this.progression = Objects.requireNonNull(progression, "progression");
        this.spellbook = Objects.requireNonNull(spellbook, "spellbook");
    }

    public String name() {
        return name;
    }

    public Affinity affinity() {
        return affinity;
    }

    public int level() {
        return progression.level();
    }

    public int experience() {
        return progression.experience();
    }

    public int experienceForNextLevel() {
        return progression.experienceForNextLevel();
    }

    /**
     * Statistiche attuali: base + bonus dell'affinità + crescita per ogni
     * livello oltre il primo.
     */
    public Stats stats() {
        return BASE_STATS
                .plus(affinity.bonus())
                .plus(affinity.growth().times(level() - 1));
    }

    /**
     * @return quanti livelli sono stati guadagnati
     */
    public int gainExperience(int amount) {
        return progression.gain(amount);
    }

    /**
     * @return gli incantesimi che lo studente sa lanciare
     */
    public List<Spell> spells() {
        return spellbook.spells();
    }

    public boolean canCast(Spell spell) {
        return spellbook.canCast(spell);
    }

    /**
     * @return {@code true} se la runa è nuova
     */
    public boolean learn(Rune rune) {
        return spellbook.learn(rune);
    }

    /**
     * @return {@code true} se l'elemento è nuovo
     */
    public boolean learn(Element element) {
        return spellbook.learn(element);
    }

    @Override
    public String toString() {
        return name + ", " + affinity.title() + " di livello " + level();
    }
}
