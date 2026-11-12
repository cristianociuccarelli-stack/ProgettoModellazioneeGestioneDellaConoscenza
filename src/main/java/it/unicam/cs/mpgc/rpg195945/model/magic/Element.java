package it.unicam.cs.mpgc.rpg195945.model.magic;

/**
 * I quattro elementi da cui nasce la magia dell'Accademia.
 * <p>
 * Ogni studente sceglie un elemento come affinità e ogni incantesimo appartiene
 * a un elemento. Quale elemento prevale su quale non è scritto qui: è una regola
 * del motore di gioco, così può cambiare senza toccare questa classe.
 */
public enum Element {

    FIRE("Fuoco"),
    WATER("Acqua"),
    EARTH("Terra"),
    AIR("Aria");

    private final String displayName;

    Element(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
