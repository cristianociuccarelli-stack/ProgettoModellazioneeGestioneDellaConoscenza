package it.unicam.cs.mpgc.rpg195945.persistence;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryGameRepositoryTest {

    @Test
    void savesLoadsAndDeletes() {
        InMemoryGameRepository repository = new InMemoryGameRepository();
        assertTrue(repository.load().isEmpty());
        SavedGame game = new SavedGame("Lira", Element.FIRE, 1, 0,
                List.of(Element.FIRE), List.of(Rune.BOLT, Rune.WARD), 1, 1, 0);
        repository.save(game);
        assertEquals(game, repository.load().orElseThrow());
        repository.delete();
        assertTrue(repository.load().isEmpty());
    }
}
