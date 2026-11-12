package it.unicam.cs.mpgc.rpg195945.model.opponent;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpponentTest {

    private static final Stats SOME_STATS = new Stats(50, 20, 5, 5, 5);
    private static final List<Spell> SOME_SPELLS = List.of(new Spell(Element.FIRE, Rune.BOLT));

    @Test
    void thereIsAnOpponentForEachElement() {
        Set<Element> elements = Opponent.all().stream()
                .map(Opponent::element)
                .collect(Collectors.toSet());
        assertEquals(Set.of(Element.values()), elements);
    }

    @Test
    void opponentsCastSpellsOfTheirOwnElement() {
        for (Opponent opponent : Opponent.all()) {
            for (Spell spell : opponent.spells()) {
                assertEquals(opponent.element(), spell.element(), opponent.name());
            }
        }
    }

    @Test
    void spellListCannotBeChangedFromOutside() {
        List<Spell> spells = new ArrayList<>(SOME_SPELLS);
        Opponent opponent = new Opponent("Prova", "", Element.FIRE, SOME_STATS, spells, 10);
        spells.add(new Spell(Element.FIRE, Rune.LANCE));
        assertEquals(1, opponent.spells().size());
        assertThrows(UnsupportedOperationException.class,
                () -> opponent.spells().add(new Spell(Element.FIRE, Rune.LANCE)));
    }

    @Test
    void rejectsInvalidOpponents() {
        assertThrows(IllegalArgumentException.class,
                () -> new Opponent(" ", "", Element.FIRE, SOME_STATS, SOME_SPELLS, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Opponent("Muto", "", Element.FIRE, SOME_STATS, List.of(), 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Opponent("Avaro", "", Element.FIRE, SOME_STATS, SOME_SPELLS, -1));
        assertThrows(NullPointerException.class,
                () -> new Opponent("Senza elemento", "", null, SOME_STATS, SOME_SPELLS, 10));
    }
}
