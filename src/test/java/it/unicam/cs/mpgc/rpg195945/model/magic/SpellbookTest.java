package it.unicam.cs.mpgc.rpg195945.model.magic;

import it.unicam.cs.mpgc.rpg195945.model.student.Element;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpellbookTest {

    @Test
    void newBookKnowsOneElementAndTheStartingRunes() {
        Spellbook book = new Spellbook(Element.FIRE);
        assertEquals(List.of(Element.FIRE), book.elements());
        assertEquals(Spellbook.STARTING_RUNES, book.runes());
        assertEquals(List.of(
                new Spell(Element.FIRE, Rune.BOLT),
                new Spell(Element.FIRE, Rune.WARD)), book.spells());
    }

    @Test
    void spellsAreEveryElementWithEveryRune() {
        Spellbook book = new Spellbook(Element.FIRE);
        book.learn(Element.WATER);
        book.learn(Rune.LANCE);
        // 2 elementi x 3 rune = 6 incantesimi
        assertEquals(6, book.spells().size());
        assertTrue(book.spells().contains(new Spell(Element.WATER, Rune.LANCE)));
    }

    @Test
    void learningTwiceChangesNothing() {
        Spellbook book = new Spellbook(Element.EARTH);
        assertTrue(book.learn(Rune.SPRING));
        assertFalse(book.learn(Rune.SPRING));
        assertFalse(book.learn(Element.EARTH));
        assertEquals(3, book.runes().size());
    }

    @Test
    void canCastOnlyWhatItKnows() {
        Spellbook book = new Spellbook(Element.AIR);
        assertTrue(book.canCast(new Spell(Element.AIR, Rune.BOLT)));
        assertFalse(book.canCast(new Spell(Element.FIRE, Rune.BOLT)));
        assertFalse(book.canCast(new Spell(Element.AIR, Rune.LANCE)));
    }

    @Test
    void returnedListsCannotBeChangedFromOutside() {
        Spellbook book = new Spellbook(Element.WATER);
        assertThrows(UnsupportedOperationException.class, () -> book.runes().add(Rune.LANCE));
        assertFalse(book.knows(Rune.LANCE));
    }

    @Test
    void rejectsAnEmptyBook() {
        assertThrows(IllegalArgumentException.class, () -> new Spellbook(List.of(), List.of(Rune.BOLT)));
        assertThrows(IllegalArgumentException.class, () -> new Spellbook(List.of(Element.FIRE), List.of()));
    }
}
