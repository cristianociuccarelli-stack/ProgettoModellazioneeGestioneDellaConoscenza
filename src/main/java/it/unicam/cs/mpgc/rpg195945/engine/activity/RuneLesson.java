package it.unicam.cs.mpgc.rpg195945.engine.activity;

import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;

import java.util.Optional;

/**
 * Lezione di rune: lo studente impara la prima runa del gioco che non conosce
 * ancora. Con ogni runa nuova sblocca un incantesimo per ogni elemento che sa
 * usare.
 */
public final class RuneLesson implements Activity {

    @Override
    public String name() {
        return "Lezione di rune";
    }

    @Override
    public String description() {
        return "Il maestro delle rune ti insegna una runa che non conosci ancora.";
    }

    @Override
    public boolean isAvailableFor(Student student) {
        return nextRune(student).isPresent();
    }

    /**
     * @throws IllegalStateException se lo studente conosce già tutte le rune
     */
    @Override
    public String perform(Student student) {
        Rune rune = nextRune(student)
                .orElseThrow(() -> new IllegalStateException("Conosci già tutte le rune"));
        student.learn(rune);
        return "Hai imparato la runa " + rune.name() + " (" + rune.effect() + " " + rune.power()
                + ", " + rune.manaCost() + " mana): ora hai " + student.spells().size() + " incantesimi.";
    }

    /** @return la prima runa del gioco che lo studente non conosce, se c'è */
    private static Optional<Rune> nextRune(Student student) {
        for (Rune rune : Rune.all()) {
            if (!student.knows(rune)) {
                return Optional.of(rune);
            }
        }
        return Optional.empty();
    }
}
