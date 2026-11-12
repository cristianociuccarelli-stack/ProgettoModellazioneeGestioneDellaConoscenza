package it.unicam.cs.mpgc.rpg195945.engine.duel;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;

import java.util.Map;
import java.util.Objects;

/**
 * La ruota degli elementi: ogni elemento prevale su un altro, come nella
 * morra cinese.
 * <p>
 * Nella ruota standard il Fuoco brucia l'Aria, l'Aria erode la Terra, la Terra
 * assorbe l'Acqua e l'Acqua spegne il Fuoco. Le coppie opposte (Fuoco-Terra,
 * Acqua-Aria) sono neutre.
 */
public final class ElementWheel implements ElementalRule {

    /** Moltiplicatore quando l'attacco è efficace. */
    public static final double STRONG = 1.5;

    /** Moltiplicatore quando l'attacco è poco efficace. */
    public static final double WEAK = 0.75;

    /** Moltiplicatore quando nessuno dei due elementi prevale. */
    public static final double NEUTRAL = 1.0;

    private final Map<Element, Element> beats;

    /**
     * @param beats per ogni elemento, l'elemento su cui prevale
     */
    public ElementWheel(Map<Element, Element> beats) {
        this.beats = Map.copyOf(Objects.requireNonNull(beats, "beats"));
    }

    /**
     * @return la ruota usata nel gioco
     */
    public static ElementWheel standard() {
        return new ElementWheel(Map.of(
                Element.FIRE, Element.AIR,
                Element.AIR, Element.EARTH,
                Element.EARTH, Element.WATER,
                Element.WATER, Element.FIRE));
    }

    /**
     * @return {@code true} se {@code attack} prevale su {@code target}
     */
    public boolean beats(Element attack, Element target) {
        return beats.get(attack) == target;
    }

    @Override
    public double multiplier(Element attack, Element target) {
        Objects.requireNonNull(attack, "attack");
        Objects.requireNonNull(target, "target");
        if (beats(attack, target)) {
            return STRONG;
        }
        if (beats(target, attack)) {
            return WEAK;
        }
        return NEUTRAL;
    }
}
