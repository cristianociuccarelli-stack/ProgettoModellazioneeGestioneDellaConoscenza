package it.unicam.cs.mpgc.rpg195945.engine.activity;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;

import java.util.Optional;

/**
 * Lezione di elementi: lo studente impara a usare il primo elemento che non
 * conosce ancora. Ogni elemento nuovo si combina con tutte le rune che già
 * conosce e permette di colpire gli avversari nel loro punto debole.
 */
public final class ElementLesson implements Activity {

    @Override
    public String name() {
        return "Lezione di elementi";
    }

    @Override
    public String description() {
        return "Studi un cristallo elementale e impari a usare un nuovo elemento.";
    }

    @Override
    public boolean isAvailableFor(Student student) {
        return nextElement(student).isPresent();
    }

    /**
     * @throws IllegalStateException se lo studente conosce già tutti gli elementi
     */
    @Override
    public String perform(Student student) {
        Element element = nextElement(student)
                .orElseThrow(() -> new IllegalStateException("Conosci già tutti gli elementi"));
        student.learn(element);
        return "Hai imparato a usare l'elemento " + element + ": ora hai "
                + student.spells().size() + " incantesimi.";
    }

    /** @return il primo elemento che lo studente non conosce, se c'è */
    private static Optional<Element> nextElement(Student student) {
        for (Element element : Element.values()) {
            if (!student.knows(element)) {
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }
}
