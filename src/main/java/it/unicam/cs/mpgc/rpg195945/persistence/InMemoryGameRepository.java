package it.unicam.cs.mpgc.rpg195945.persistence;

import java.util.Optional;

/**
 * Salvataggio in memoria: la partita resta salvata solo finché il programma è
 * aperto. È utile nei test e quando non si vuole scrivere niente su disco.
 */
public class InMemoryGameRepository implements GameRepository {

    private SavedGame saved;

    @Override
    public void save(SavedGame game) {
        saved = game;
    }

    @Override
    public Optional<SavedGame> load() {
        return Optional.ofNullable(saved);
    }

    @Override
    public void delete() {
        saved = null;
    }
}
