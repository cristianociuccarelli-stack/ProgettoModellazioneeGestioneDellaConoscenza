package it.unicam.cs.mpgc.rpg195945.model.magic;

import it.unicam.cs.mpgc.rpg195945.model.student.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Il libro degli incantesimi: gli elementi e le rune che uno studente conosce.
 * <p>
 * Gli incantesimi non vengono salvati uno per uno: si ottengono combinando
 * ogni elemento conosciuto con ogni runa conosciuta. Imparando una sola runa
 * nuova, quindi, si sblocca un incantesimo per ogni elemento.
 */
public final class Spellbook {

    /** Le rune che ogni studente conosce fin dal primo giorno. */
    public static final List<Rune> STARTING_RUNES = List.of(Rune.BOLT, Rune.WARD);

    private final Set<Element> elements = new LinkedHashSet<>();
    private final Set<Rune> runes = new LinkedHashSet<>();

    /**
     * Libro di un nuovo studente: conosce solo l'elemento della sua affinità
     * e le rune iniziali.
     */
    public Spellbook(Element firstElement) {
        this(List.of(firstElement), STARTING_RUNES);
    }

    /**
     * Costruttore completo, utile per ricaricare un libro salvato.
     *
     * @throws IllegalArgumentException se non c'è almeno un elemento e una runa
     */
    public Spellbook(Collection<Element> elements, Collection<Rune> runes) {
        Objects.requireNonNull(elements, "elements");
        Objects.requireNonNull(runes, "runes");
        if (elements.isEmpty() || runes.isEmpty()) {
            throw new IllegalArgumentException("Il libro deve contenere almeno un elemento e una runa");
        }
        for (Element element : elements) {
            learn(element);
        }
        for (Rune rune : runes) {
            learn(rune);
        }
    }

    /**
     * @return {@code true} se l'elemento è nuovo, {@code false} se era già conosciuto
     */
    public boolean learn(Element element) {
        return elements.add(Objects.requireNonNull(element, "element"));
    }

    /**
     * @return {@code true} se la runa è nuova, {@code false} se era già conosciuta
     */
    public boolean learn(Rune rune) {
        return runes.add(Objects.requireNonNull(rune, "rune"));
    }

    public boolean knows(Element element) {
        return elements.contains(element);
    }

    public boolean knows(Rune rune) {
        return runes.contains(rune);
    }

    /**
     * @return {@code true} se conosce sia l'elemento sia la runa dell'incantesimo
     */
    public boolean canCast(Spell spell) {
        return knows(spell.element()) && knows(spell.rune());
    }

    /** @return gli elementi conosciuti, nell'ordine in cui sono stati imparati */
    public List<Element> elements() {
        return List.copyOf(elements);
    }

    /** @return le rune conosciute, nell'ordine in cui sono state imparate */
    public List<Rune> runes() {
        return List.copyOf(runes);
    }

    /**
     * @return tutti gli incantesimi lanciabili: ogni elemento con ogni runa
     */
    public List<Spell> spells() {
        List<Spell> spells = new ArrayList<>();
        for (Element element : elements) {
            for (Rune rune : runes) {
                spells.add(new Spell(element, rune));
            }
        }
        return spells;
    }
}
