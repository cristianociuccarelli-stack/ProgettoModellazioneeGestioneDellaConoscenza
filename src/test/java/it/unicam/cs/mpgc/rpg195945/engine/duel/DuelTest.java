package it.unicam.cs.mpgc.rpg195945.engine.duel;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DuelTest {

    private static final Spell FIRE_BOLT = new Spell(Element.FIRE, Rune.BOLT);
    private static final Spell FIRE_WARD = new Spell(Element.FIRE, Rune.WARD);
    private static final Spell WATER_BOLT = new Spell(Element.WATER, Rune.BOLT);
    private static final Spell WATER_LANCE = new Spell(Element.WATER, Rune.LANCE);
    private static final Spell WATER_SPRING = new Spell(Element.WATER, Rune.SPRING);

    private static final ElementalRule WHEEL = ElementWheel.standard();

    /** Un avversario che non può fare nulla: ha 0 mana e quindi si concentra sempre. */
    private static Combatant dummy(Element element, int health, int defense) {
        return new Combatant("Manichino", element, new Stats(health, 0, 0, defense, 1), List.of(WATER_BOLT));
    }

    private static Combatant fireStudent() {
        return new Combatant("Lira", Element.FIRE, new Stats(100, 50, 10, 10, 10), List.of(FIRE_BOLT, FIRE_WARD));
    }

    @Test
    void damageUsesElementAffinityAndDefense() {
        Combatant target = dummy(Element.AIR, 200, 4);
        new Duel(fireStudent(), target, WHEEL).cast(FIRE_BOLT);
        // (12 + 10) x 1,5 (Fuoco batte Aria) x 1,2 (affinità) = 39,6 -> 40; 40 - 4/2 = 38
        assertEquals(200 - 38, target.health());
    }

    @Test
    void spellsOfAnotherElementGetNoAffinityBonus() {
        Combatant waterStudent = new Combatant("Lira", Element.WATER,
                new Stats(100, 50, 10, 10, 10), List.of(FIRE_BOLT));
        Combatant target = dummy(Element.AIR, 200, 4);
        new Duel(waterStudent, target, WHEEL).cast(FIRE_BOLT);
        // (12 + 10) x 1,5 = 33; 33 - 2 = 31
        assertEquals(200 - 31, target.health());
    }

    @Test
    void castingSpendsMana() {
        Combatant student = fireStudent();
        new Duel(student, dummy(Element.EARTH, 200, 0), WHEEL).cast(FIRE_BOLT);
        assertEquals(50 - Rune.BOLT.manaCost(), student.mana());
    }

    @Test
    void wardRaisesAShield() {
        Combatant student = fireStudent();
        new Duel(student, dummy(Element.EARTH, 200, 0), WHEEL).cast(FIRE_WARD);
        // 15 + difesa 10 / 2
        assertEquals(20, student.shield());
    }

    @Test
    void concentratingRestoresMana() {
        Combatant student = fireStudent();
        student.spendMana(40);
        new Duel(student, dummy(Element.EARTH, 200, 0), WHEEL).concentrate();
        assertEquals(10 + Duel.CONCENTRATION_MANA, student.mana());
    }

    @Test
    void fasterCombatantActsFirst() {
        Combatant fast = new Combatant("Lira", Element.FIRE, new Stats(100, 50, 30, 10, 20), List.of(FIRE_BOLT));
        Combatant slow = new Combatant("Lento", Element.WATER, new Stats(10, 50, 50, 0, 5), List.of(WATER_BOLT));
        Duel duel = new Duel(fast, slow, WHEEL);
        duel.cast(FIRE_BOLT);
        // lo studente colpisce per primo e vince: l'avversario non fa in tempo ad attaccare
        assertTrue(duel.studentWon());
        assertEquals(100, fast.health());
    }

    @Test
    void opponentUsesItsStrongestAffordableAttack() {
        Combatant opponent = new Combatant("Spirito", Element.WATER,
                new Stats(100, 40, 10, 10, 1), List.of(WATER_BOLT, WATER_LANCE));
        new Duel(fireStudent(), opponent, WHEEL).concentrate();
        assertEquals(40 - Rune.LANCE.manaCost(), opponent.mana());
    }

    @Test
    void woundedOpponentHealsItself() {
        Combatant opponent = new Combatant("Spirito", Element.WATER,
                new Stats(100, 60, 10, 10, 1), List.of(WATER_BOLT, WATER_SPRING));
        opponent.takeDamage(60);
        new Duel(fireStudent(), opponent, WHEEL).concentrate();
        // 40 + (20 + 10 / 2)
        assertEquals(65, opponent.health());
    }

    @Test
    void studentCanLose() {
        Combatant weak = new Combatant("Lira", Element.FIRE, new Stats(5, 50, 10, 0, 1), List.of(FIRE_BOLT));
        Combatant strong = new Combatant("Golem", Element.WATER, new Stats(500, 50, 30, 10, 20), List.of(WATER_BOLT));
        Duel duel = new Duel(weak, strong, WHEEL);
        List<String> log = duel.cast(FIRE_BOLT);
        assertTrue(duel.isOver());
        assertFalse(duel.studentWon());
        assertEquals("Sei stato sconfitto da Golem.", log.get(log.size() - 1));
    }

    @Test
    void rejectsUnknownSpellsAndTurnsAfterTheEnd() {
        Duel duel = new Duel(fireStudent(), dummy(Element.AIR, 1, 0), WHEEL);
        assertThrows(IllegalArgumentException.class, () -> duel.cast(WATER_BOLT));
        duel.cast(FIRE_BOLT);
        assertTrue(duel.isOver());
        assertThrows(IllegalStateException.class, duel::concentrate);
    }
}
