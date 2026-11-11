package it.unicam.cs.mpgc.rpg195945.model.magic;

import java.util.List;
import java.util.Objects;

/**
 * Una runa: il segno che dà la forma a un incantesimo.
 * <p>
 * Da sola una runa non fa nulla: diventa un incantesimo quando viene
 * combinata con un elemento (per esempio la runa "Dardo" con il Fuoco dà il
 * "Dardo di Fuoco"). Come le affinità, le rune sono contenuti: se ne possono
 * aggiungere di nuove senza modificare questa classe.
 *
 * @param name     nome della runa
 * @param effect   che cosa fa l'incantesimo
 * @param power    intensità dell'effetto (danni, scudo o cura)
 * @param manaCost mana consumato a ogni lancio
 */
public record Rune(String name, Effect effect, int power, int manaCost) {

    public static final Rune BOLT = new Rune("Dardo", Effect.DAMAGE, 12, 5);
    public static final Rune LANCE = new Rune("Lancia", Effect.DAMAGE, 22, 12);
    public static final Rune WARD = new Rune("Barriera", Effect.SHIELD, 15, 6);
    public static final Rune SPRING = new Rune("Sorgente", Effect.HEAL, 20, 10);

    public Rune {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(effect, "effect");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Una runa deve avere un nome");
        }
        if (power <= 0 || manaCost < 0) {
            throw new IllegalArgumentException("Potenza o costo della runa non validi");
        }
    }

    /**
     * @return tutte le rune presenti nel gioco
     */
    public static List<Rune> all() {
        return List.of(BOLT, LANCE, WARD, SPRING);
    }

    @Override
    public String toString() {
        return name + " (" + effect.displayName() + ")";
    }
}
