package it.unicam.cs.mpgc.rpg195945.persistence;

/**
 * Errore durante il salvataggio o il caricamento della partita.
 * <p>
 * È un'eccezione non controllata che "avvolge" gli errori di basso livello
 * (per esempio di lettura del file): così chi usa un {@link GameRepository}
 * non dipende dal modo in cui la partita viene salvata.
 */
public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
