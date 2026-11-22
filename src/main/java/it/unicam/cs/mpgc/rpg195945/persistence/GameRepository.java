package it.unicam.cs.mpgc.rpg195945.persistence;

import java.util.Optional;

/**
 * Dove si salva e da dove si carica la partita.
 * <p>
 * È un'interfaccia: il controller conosce solo questa, non sa se dietro c'è un
 * file, un database o la memoria. Per salvare in un altro modo basta scrivere
 * una nuova implementazione, senza toccare il resto del gioco.
 */
public interface GameRepository {

    /**
     * Salva la partita, sostituendo il salvataggio precedente.
     *
     * @throws PersistenceException se il salvataggio non riesce
     */
    void save(SavedGame game);

    /**
     * @return la partita salvata, oppure vuoto se non ce n'è nessuna
     * @throws PersistenceException se il salvataggio esiste ma non si riesce a leggerlo
     */
    Optional<SavedGame> load();

    /**
     * Cancella il salvataggio, se c'è (per esempio quando la partita è finita).
     *
     * @throws PersistenceException se non si riesce a cancellarlo
     */
    void delete();
}
