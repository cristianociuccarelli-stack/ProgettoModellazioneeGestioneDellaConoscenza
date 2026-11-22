package it.unicam.cs.mpgc.rpg195945.persistence;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileGameRepositoryTest {

    private static final SavedGame GAME = new SavedGame("Cristiano", Element.WATER, 3, 40,
            List.of(Element.WATER, Element.FIRE), List.of(Rune.BOLT, Rune.WARD, Rune.LANCE), 2, 4, 1);

    /** Un file in una cartella temporanea nuova, che ancora non esiste. */
    private static Path newFile() throws IOException {
        return Files.createTempDirectory("accademia").resolve("salvataggi").resolve("partita.properties");
    }

    @Test
    void thereIsNothingToLoadAtTheBeginning() throws IOException {
        assertTrue(new FileGameRepository(newFile()).load().isEmpty());
    }

    @Test
    void savedGameIsLoadedBackTheSame() throws IOException {
        Path file = newFile();
        new FileGameRepository(file).save(GAME);
        // un repository nuovo sullo stesso file: come riaprire il gioco il giorno dopo
        assertEquals(GAME, new FileGameRepository(file).load().orElseThrow());
    }

    @Test
    void aNewSaveReplacesTheOldOne() throws IOException {
        FileGameRepository repository = new FileGameRepository(newFile());
        repository.save(GAME);
        SavedGame later = new SavedGame("Cristiano", Element.WATER, 4, 0,
                List.of(Element.WATER), List.of(Rune.BOLT, Rune.WARD), 3, 1, 1);
        repository.save(later);
        assertEquals(later, repository.load().orElseThrow());
    }

    @Test
    void deleteRemovesTheSave() throws IOException {
        FileGameRepository repository = new FileGameRepository(newFile());
        repository.save(GAME);
        repository.delete();
        assertTrue(repository.load().isEmpty());
    }

    @Test
    void aDamagedFileIsReportedClearly() throws IOException {
        Path file = newFile();
        Files.createDirectories(file.getParent());
        Files.writeString(file, "studente.nome=Cristiano\nstudente.livello=tre\n");
        assertThrows(PersistenceException.class, () -> new FileGameRepository(file).load());
    }
}
