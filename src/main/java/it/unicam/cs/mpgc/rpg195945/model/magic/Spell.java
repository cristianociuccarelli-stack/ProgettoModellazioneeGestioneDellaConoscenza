package it.unicam.cs.mpgc.rpg195945.model.magic;

import it.unicam.cs.mpgc.rpg195945.model.student.Element;

import java.util.Objects;

/**
 * Un incantesimo: un elemento combinato con una runa.
 * <p>
 * L'elemento dice di che cosa è fatta la magia (e quindi quali avversari
 * mette in difficoltà); la runa dice che forma prende: effetto, potenza e
 * costo. Questi tre valori non sono copiati qui, ma chiesti alla runa.
 *
 * @param element l'elemento dell'incantesimo
 * @param rune    la runa che gli dà forma
 */
public record Spell(Element element, Rune rune) {

    public Spell {
        Objects.requireNonNull(element, "element");
        Objects.requireNonNull(rune, "rune");
    }

    /**
     * @return il nome completo, per esempio "Dardo di Fuoco" o "Dardo d'Acqua"
     */
    public String name() {
        String elementName = element.displayName();
        String preposition = startsWithVowel(elementName) ? "d'" : "di ";
        return rune.name() + " " + preposition + elementName;
    }

    public Effect effect() {
        return rune.effect();
    }

    public int power() {
        return rune.power();
    }

    public int manaCost() {
        return rune.manaCost();
    }

    @Override
    public String toString() {
        return name();
    }

    private static boolean startsWithVowel(String word) {
        return "AEIOU".indexOf(Character.toUpperCase(word.charAt(0))) >= 0;
    }
}
