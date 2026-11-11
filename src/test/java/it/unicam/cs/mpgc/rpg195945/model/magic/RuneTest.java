package it.unicam.cs.mpgc.rpg195945.model.magic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuneTest {

    @Test
    void everyEffectHasAtLeastOneRune() {
        for (Effect effect : Effect.values()) {
            assertTrue(Rune.all().stream().anyMatch(rune -> rune.effect() == effect),
                    "Nessuna runa per l'effetto " + effect);
        }
    }

    @Test
    void strongerRuneCostsMoreMana() {
        assertTrue(Rune.LANCE.power() > Rune.BOLT.power());
        assertTrue(Rune.LANCE.manaCost() > Rune.BOLT.manaCost());
    }

    @Test
    void canCreateANewRuneWithoutChangingTheClass() {
        Rune storm = new Rune("Tempesta", Effect.DAMAGE, 30, 18);
        assertEquals("Tempesta (Danno)", storm.toString());
    }

    @Test
    void rejectsInvalidRunes() {
        assertThrows(IllegalArgumentException.class, () -> new Rune(" ", Effect.DAMAGE, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Rune("Vuota", Effect.DAMAGE, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Rune("Gratis", Effect.HEAL, 10, -1));
        assertThrows(NullPointerException.class, () -> new Rune("Senza effetto", null, 10, 5));
    }
}
