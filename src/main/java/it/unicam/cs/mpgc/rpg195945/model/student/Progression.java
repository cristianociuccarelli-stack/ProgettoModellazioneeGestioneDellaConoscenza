package it.unicam.cs.mpgc.rpg195945.model.student;

/**
 * Livello ed esperienza di un personaggio.
 * <p>
 * Per passare dal livello N al livello N+1 servono N * {@link #EXPERIENCE_PER_LEVEL}
 * punti esperienza: ogni livello è un po' più difficile da raggiungere del precedente.
 * L'esperienza in eccesso non va persa ma conta per il livello successivo.
 */
public final class Progression {

    public static final int EXPERIENCE_PER_LEVEL = 100;
    public static final int MAX_LEVEL = 20;

    private int level;
    private int experience;

    /** Un personaggio nuovo: livello 1, nessuna esperienza. */
    public Progression() {
        this(1, 0);
    }

    /**
     * Costruttore completo, usato ad esempio quando si carica una partita salvata.
     */
    public Progression(int level, int experience) {
        if (level < 1 || level > MAX_LEVEL || experience < 0) {
            throw new IllegalArgumentException("Livello o esperienza non validi");
        }
        this.level = level;
        this.experience = experience;
    }

    public int level() {
        return level;
    }

    public int experience() {
        return experience;
    }

    /**
     * @return esperienza totale necessaria per completare il livello attuale
     */
    public int experienceForNextLevel() {
        return level * EXPERIENCE_PER_LEVEL;
    }

    public boolean isMaxLevel() {
        return level == MAX_LEVEL;
    }

    /**
     * Aggiunge esperienza, salendo di uno o più livelli se necessario.
     *
     * @return quanti livelli sono stati guadagnati (di solito 0 o 1)
     */
    public int gain(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("L'esperienza guadagnata non può essere negativa");
        }
        if (isMaxLevel()) {
            return 0;
        }
        experience += amount;
        int levelsGained = 0;
        while (!isMaxLevel() && experience >= experienceForNextLevel()) {
            experience -= experienceForNextLevel();
            level++;
            levelsGained++;
        }
        if (isMaxLevel()) {
            experience = 0;
        }
        return levelsGained;
    }
}
