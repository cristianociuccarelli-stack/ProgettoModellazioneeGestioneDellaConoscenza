package it.unicam.cs.mpgc.rpg195945.model.student;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AffinityTest {

    @Test
    void thereIsOneAffinityForEachElement() {
        Set<Element> elements = Affinity.all().stream()
                .map(Affinity::element)
                .collect(Collectors.toSet());
        assertEquals(Set.of(Element.values()), elements);
    }

    @Test
    void eachAffinityHasItsOwnStrength() {
        assertTrue(Affinity.FIRE.bonus().power() > Affinity.EARTH.bonus().power());
        assertTrue(Affinity.WATER.bonus().mana() > Affinity.FIRE.bonus().mana());
        assertTrue(Affinity.EARTH.bonus().health() > Affinity.AIR.bonus().health());
        assertTrue(Affinity.AIR.bonus().speed() > Affinity.WATER.bonus().speed());
    }

    @Test
    void rejectsMissingData() {
        assertThrows(NullPointerException.class,
                () -> new Affinity(Element.FIRE, "Titolo", "Descrizione", null, Stats.ZERO));
    }
}
