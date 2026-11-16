package it.unicam.cs.mpgc.rpg195945.model.calendar;

/**
 * L'anno accademico: un numero fisso di settimane, alla fine delle quali c'è
 * la Prova dell'Equinozio.
 * <p>
 * Ogni settimana lo studente svolge una sola attività (una lezione, un
 * allenamento o un duello); poi si passa alla settimana successiva.
 */
public final class AcademicYear {

    /** Settimane di scuola prima della Prova dell'Equinozio. */
    public static final int TOTAL_WEEKS = 10;

    private int week;

    /** Un anno appena iniziato: prima settimana. */
    public AcademicYear() {
        this(1);
    }

    /**
     * Costruttore completo, utile per ricaricare una partita salvata.
     *
     * @param week la settimana corrente; {@code TOTAL_WEEKS + 1} vuol dire che
     *             le settimane di scuola sono finite
     * @throws IllegalArgumentException se la settimana non è valida
     */
    public AcademicYear(int week) {
        if (week < 1 || week > TOTAL_WEEKS + 1) {
            throw new IllegalArgumentException("Settimana non valida: " + week);
        }
        this.week = week;
    }

    public int week() {
        return week;
    }

    /** @return quante settimane di scuola restano, compresa quella corrente */
    public int weeksLeft() {
        return TOTAL_WEEKS - week + 1;
    }

    /** @return {@code true} quando è il momento della Prova dell'Equinozio */
    public boolean isOver() {
        return week > TOTAL_WEEKS;
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
}
