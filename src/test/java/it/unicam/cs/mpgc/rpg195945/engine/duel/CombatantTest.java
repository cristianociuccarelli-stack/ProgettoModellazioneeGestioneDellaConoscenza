package it.unicam.cs.mpgc.rpg195945.engine.duel;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatantTest {

    private static final Spell BOLT = new Spell(Element.FIRE, Rune.BOLT);

    private static Combatant fighter() {
        return new Combatant("Prova", Element.FIRE, new Stats(100, 20, 10, 10, 10), List.of(BOLT));
    }

    @Test
    void startsWithFullHealthAndMana() {
        Combatant combatant = fighter();
        assertEquals(100, combatant.health());
        assertEquals(20, combatant.mana());
        assertEquals(0, combatant.shield());
    }

    @Test
    void studentFightsWithTheElementOfItsAffinity() {
        Student student = new Student("Lira", Affinity.EARTH);
        Combatant combatant = Combatant.of(student);
        assertEquals(Element.EARTH, combatant.element());
        assertEquals(student.stats().health(), combatant.health());
        assertEquals(student.spells(), combatant.spells());
    }

    @Test
    void losingHealthInADuelDoesNotChangeTheStudent() {
        Student student = new Student("Lira", Affinity.EARTH);
        Combatant.of(student).takeDamage(50);
        assertEquals(130, Combatant.of(student).health());
    }

    @Test
    void opponentFightsWithItsOwnData() {
        Combatant combatant = Combatant.of(Opponent.STORM_HAWK);
        assertEquals(Element.AIR, combatant.element());
        assertEquals(70, combatant.health());
    }

    @Test
    void healthNeverGoesBelowZero() {
        Combatant combatant = fighter();
        assertEquals(100, combatant.takeDamage(500));
        assertEquals(0, combatant.health());
        assertTrue(combatant.isDefeated());
    }

    @Test
    void shieldAbsorbsOnlyTheNextHit() {
        Combatant combatant = fighter();
        combatant.raiseShield(15);
        assertEquals(5, combatant.takeDamage(20));
        assertEquals(0, combatant.shield());
        assertEquals(20, combatant.takeDamage(20));
        assertEquals(75, combatant.health());
    }

    @Test
    void healingAndManaNeverGoAboveTheMaximum() {
        Combatant combatant = fighter();
        combatant.takeDamage(10);
        assertEquals(10, combatant.heal(50));
        assertEquals(100, combatant.health());
        combatant.spendMana(5);
        assertEquals(5, combatant.restoreMana(50));
        assertEquals(20, combatant.mana());
    }

    @Test
    void canCastOnlyKnownSpellsWithEnoughMana() {
        Combatant combatant = fighter();
        assertTrue(combatant.canCast(BOLT));
        assertFalse(combatant.canCast(new Spell(Element.FIRE, Rune.LANCE)));
        combatant.spendMana(18);
        assertFalse(combatant.canCast(BOLT));
        assertThrows(IllegalStateException.class, () -> combatant.spendMana(5));
    }
}
