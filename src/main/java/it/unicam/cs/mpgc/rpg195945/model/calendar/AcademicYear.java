package it.unicam.cs.mpgc.rpg195945.model.calendar;

/**
 * Il calendario dell'Accademia: il percorso dura {@link #LAST_YEAR} anni e ogni
 * anno ha {@link #TOTAL_WEEKS} settimane di scuola, alla fine delle quali c'è
 * la Prova dell'Equinozio contro il guardiano di quell'anno.
 * <p>
 * Ogni settimana lo studente svolge una sola attività (una lezione, un
 * allenamento o un duello); poi si passa alla settimana successiva. Se supera
 * la Prova passa all'anno dopo, altrimenti ripete lo stesso anno.
 */
public final class AcademicYear {

    /** Settimane di scuola prima di ogni Prova dell'Equinozio. */
    public static final int TOTAL_WEEKS = 5;

    /** Numero di anni (e quindi di guardiani) del percorso. */
    public static final int LAST_YEAR = 5;

    private int year;
    private int week;

    /** Il primo giorno di scuola: anno 1, settimana 1. */
    public AcademicYear() {
        this(1, 1);
    }

    /**
     * Costruttore completo, utile per ricaricare una partita salvata.
     *
     * @param year l'anno corrente, da 1 a {@link #LAST_YEAR}
     * @param week la settimana corrente; {@code TOTAL_WEEKS + 1} vuol dire che
     *             le settimane di scuola dell'anno sono finite
     * @throws IllegalArgumentException se l'anno o la settimana non sono validi
     */
    public AcademicYear(int year, int week) {
        if (year < 1 || year > LAST_YEAR) {
            throw new IllegalArgumentException("Anno non valido: " + year);
        }
        if (week < 1 || week > TOTAL_WEEKS + 1) {
            throw new IllegalArgumentException("Settimana non valida: " + week);
        }
        this.year = year;
        this.week = week;
    }

    public int year() {
        return year;
    }

    public int week() {
        return week;
    }

    /** @return quante settimane di scuola restano nell'anno, compresa quella corrente */
    public int weeksLeft() {
        return TOTAL_WEEKS - week + 1;
    }

    /** @return {@code true} quando è il momento della Prova dell'Equinozio */
    public boolean isOver() {
        return week > TOTAL_WEEKS;
    }

    /** @return {@code true} se è l'ultimo anno del percorso */
    public boolean isLastYear() {
        return year == LAST_YEAR;
    }

    /**
     * Passa alla settimana successiva.
     *
     * @throws IllegalStateException se le settimane di scuola sono già finite
     */
    public void advance() {
        if (isOver()) {
            throw new IllegalStateException("Le settimane di scuola sono finite");
        }
        week++;
    }

    /**
     * Prova superata: inizia l'anno successivo.
     *
     * @throws IllegalStateException se l'anno non è finito o era l'ultimo
     */
    public void nextYear() {
        requireOver();
        if (isLastYear()) {
            throw new IllegalStateException("Era l'ultimo anno");
        }
        year++;
        week = 1;
    }

    /**
     * Prova fallita: si ripete lo stesso anno dalla prima settimana.
     *
     * @throws IllegalStateException se l'anno non è finito
     */
    public void repeatYear() {
        requireOver();
        week = 1;
    }

    private void requireOver() {
        if (!isOver()) {
            throw new IllegalStateException("L'anno non è ancora finito");
        }
    }
}
