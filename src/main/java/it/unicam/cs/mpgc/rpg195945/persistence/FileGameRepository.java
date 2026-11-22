package it.unicam.cs.mpgc.rpg195945.persistence;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Salva la partita in un file di testo nel formato "chiave=valore"
 * ({@link Properties} di Java), facile da leggere anche a mano:
 * <pre>
 * studente.nome=Cristiano
 * studente.affinita=WATER
 * studente.livello=3
 * studente.elementi=WATER,FIRE
 * studente.rune=Dardo,Barriera,Lancia
 * percorso.anno=2
 * ...
 * </pre>
 * Gli elementi si salvano con il loro nome nell'enum, le rune con il loro
 * nome nel gioco; al caricamento si ritrovano tra quelli esistenti.
 */
public class FileGameRepository implements GameRepository {

    private final Path file;

    /**
     * @param file il file in cui salvare; le cartelle mancanti vengono create
     */
    public FileGameRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    /**
     * @return il repository che salva nella cartella dell'utente, in
     *         {@code ~/.accademia-arcana/salvataggio.properties}
     */
    public static FileGameRepository inUserHome() {
        return new FileGameRepository(Path.of(System.getProperty("user.home"),
                ".accademia-arcana", "salvataggio.properties"));
    }

    @Override
    public void save(SavedGame game) {
        Objects.requireNonNull(game, "game");
        Properties properties = new Properties();
        properties.setProperty("studente.nome", game.studentName());
        properties.setProperty("studente.affinita", game.affinityElement().name());
        properties.setProperty("studente.livello", String.valueOf(game.level()));
        properties.setProperty("studente.esperienza", String.valueOf(game.experience()));
        properties.setProperty("studente.elementi", joinElements(game.elements()));
        properties.setProperty("studente.rune", joinRunes(game.runes()));
        properties.setProperty("percorso.anno", String.valueOf(game.year()));
        properties.setProperty("percorso.settimana", String.valueOf(game.week()));
        properties.setProperty("percorso.prove-fallite", String.valueOf(game.failedTrials()));
        try {
            Path folder = file.toAbsolutePath().getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                properties.store(writer, "Accademia Arcana - partita salvata");
            }
        } catch (IOException e) {
            throw new PersistenceException("Impossibile salvare la partita in " + file, e);
        }
    }

    @Override
    public Optional<SavedGame> load() {
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
            return Optional.of(new SavedGame(
                    required(properties, "studente.nome"),
                    Element.valueOf(required(properties, "studente.affinita")),
                    Integer.parseInt(required(properties, "studente.livello")),
                    Integer.parseInt(required(properties, "studente.esperienza")),
                    splitElements(required(properties, "studente.elementi")),
                    splitRunes(required(properties, "studente.rune")),
                    Integer.parseInt(required(properties, "percorso.anno")),
                    Integer.parseInt(required(properties, "percorso.settimana")),
                    Integer.parseInt(required(properties, "percorso.prove-fallite"))));
        } catch (IOException | IllegalArgumentException e) {
            throw new PersistenceException("Il salvataggio in " + file + " non è leggibile", e);
        }
    }

    @Override
    public void delete() {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new PersistenceException("Impossibile cancellare il salvataggio " + file, e);
        }
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Manca il valore " + key);
        }
        return value;
    }

    private static String joinElements(List<Element> elements) {
        List<String> names = new ArrayList<>();
        for (Element element : elements) {
            names.add(element.name());
        }
        return String.join(",", names);
    }

    private static String joinRunes(List<Rune> runes) {
        List<String> names = new ArrayList<>();
        for (Rune rune : runes) {
            names.add(rune.name());
        }
        return String.join(",", names);
    }

    private static List<Element> splitElements(String text) {
        List<Element> elements = new ArrayList<>();
        for (String name : text.split(",")) {
            elements.add(Element.valueOf(name.strip()));
        }
        return elements;
    }

    /** Ritrova le rune del gioco a partire dal loro nome. */
    private static List<Rune> splitRunes(String text) {
        List<Rune> runes = new ArrayList<>();
        for (String name : text.split(",")) {
            runes.add(runeNamed(name.strip()));
        }
        return runes;
    }

    private static Rune runeNamed(String name) {
        for (Rune rune : Rune.all()) {
            if (rune.name().equals(name)) {
                return rune;
            }
        }
        throw new IllegalArgumentException("Runa sconosciuta: " + name);
    }
}
