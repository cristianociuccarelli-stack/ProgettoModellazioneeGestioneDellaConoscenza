package it.unicam.cs.mpgc.rpg195945.model.magic;

import it.unicam.cs.mpgc.rpg195945.model.student.Element;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpellTest {

    @Test
    void nameCombinesRuneAndElement() {
        assertEquals("Dardo di Fuoco", new Spell(Element.FIRE, Rune.BOLT).name());
        assertEquals("Barriera di Terra", new Spell(Element.EARTH, Rune.WARD).name());
    }

    @Test
    void nameUsesApostropheBeforeAVowel() {
        assertEquals("Dardo d'Acqua", new Spell(Element.WATER, Rune.BOLT).name());
        assertEquals("Lancia d'Aria", new Spell(Element.AIR, Rune.LANCE).name());
    }

    @Test
    void effectPowerAndCostComeFromTheRune() {
        Spell spell = new Spell(Element.WATER, Rune.SPRING);
        assertEquals(Effect.HEAL, spell.effect());
        assertEquals(Rune.SPRING.power(), spell.power());
        assertEquals(Rune.SPRING.manaCost(), spell.manaCost());
    }

    @Test
    void sameRuneWithDifferentElementIsADifferentSpell() {
        assertNotEquals(new Spell(Element.FIRE, Rune.BOLT), new Spell(Element.WATER, Rune.BOLT));
        assertEquals(new Spell(Element.FIRE, Rune.BOLT), new Spell(Element.FIRE, Rune.BOLT));
    }

    @Test
    void rejectsMissingParts() {
        assertThrows(NullPointerException.class, () -> new Spell(null, Rune.BOLT));
        assertThrows(NullPointerException.class, () -> new Spell(Element.FIRE, null));
    }
}
