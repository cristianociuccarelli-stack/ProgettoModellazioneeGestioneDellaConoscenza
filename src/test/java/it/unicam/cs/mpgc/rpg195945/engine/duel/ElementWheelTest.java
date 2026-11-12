package it.unicam.cs.mpgc.rpg195945.engine.duel;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementWheelTest {

    private final ElementWheel wheel = ElementWheel.standard();

    @Test
    void fireBeatsAirAndWaterBeatsFire() {
        assertEquals(ElementWheel.STRONG, wheel.multiplier(Element.FIRE, Element.AIR));
        assertEquals(ElementWheel.STRONG, wheel.multiplier(Element.WATER, Element.FIRE));
    }

    @Test
    void theOppositeDirectionIsWeak() {
        assertEquals(ElementWheel.WEAK, wheel.multiplier(Element.AIR, Element.FIRE));
        assertEquals(ElementWheel.WEAK, wheel.multiplier(Element.FIRE, Element.WATER));
    }

    @Test
    void oppositeElementsAndSameElementAreNeutral() {
        assertEquals(ElementWheel.NEUTRAL, wheel.multiplier(Element.FIRE, Element.EARTH));
        assertEquals(ElementWheel.NEUTRAL, wheel.multiplier(Element.WATER, Element.AIR));
        assertEquals(ElementWheel.NEUTRAL, wheel.multiplier(Element.FIRE, Element.FIRE));
    }

    @Test
    void everyElementBeatsExactlyOneAndLosesToExactlyOne() {
        for (Element element : Element.values()) {
            int wins = 0;
            int losses = 0;
            for (Element other : Element.values()) {
                if (wheel.beats(element, other)) {
                    wins++;
                }
                if (wheel.beats(other, element)) {
                    losses++;
                }
            }
            assertEquals(1, wins, element + " deve battere un solo elemento");
            assertEquals(1, losses, element + " deve perdere contro un solo elemento");
        }
    }

    @Test
    void aDifferentRuleCanBeUsedWithoutChangingTheDuel() {
        ElementalRule noElements = (attack, target) -> 1.0;
        assertEquals(1.0, noElements.multiplier(Element.WATER, Element.FIRE));
    }
}
