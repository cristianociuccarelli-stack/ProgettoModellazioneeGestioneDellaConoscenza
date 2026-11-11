package it.unicam.cs.mpgc.rpg195945.model.student;

import java.util.Objects;

/**
 * Lo studente dell'Accademia: il personaggio controllato dal giocatore.
 * <p>
 * Mette insieme un nome, un'affinità elementale (scelta all'inizio, non cambia
 * più) e una progressione di livello. Le statistiche non sono salvate in un
 * campo: vengono calcolate ogni volta a partire dall'affinità e dal livello,
 * così non possono mai essere "sbagliate" rispetto a questi due dati.
 */
public final class Student {

    /** Statistiche di partenza, uguali per tutti gli studenti. */
    public static final Stats BASE_STATS = new Stats(100, 50, 10, 10, 10);

    private final String name;
    private final Affinity affinity;
    private final Progression progression;

    /**
     * Crea un nuovo studente al livello 1.
     */
    public Student(String name, Affinity affinity) {
        this(name, affinity, new Progression());
    }

    /**
     * Costruttore completo, utile per ricaricare uno studente salvato.
     *
     * @throws IllegalArgumentException se il nome è vuoto
     */
    public Student(String name, Affinity affinity, Progression progression) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Il nome dello studente non può essere vuoto");
        }
        this.name = name.strip();
        this.affinity = Objects.requireNonNull(affinity, "affinity");
        this.progression = Objects.requireNonNull(progression, "progression");
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

    @Override
    public String toString() {
        return name + ", " + affinity.title() + " di livello " + level();
    }
}
