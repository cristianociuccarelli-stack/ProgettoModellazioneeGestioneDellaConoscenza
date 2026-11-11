package it.unicam.cs.mpgc.rpg195945.model.magic;

/**
 * Che cosa fa un incantesimo quando viene lanciato.
 * <p>
 * Come per gli elementi, qui c'è solo l'elenco: come si applica ogni effetto
 * (quanti danni, quanta cura...) lo decide il motore di gioco.
 */
public enum Effect {

    /** Colpisce l'avversario. */
    DAMAGE("Danno"),

    /** Protegge chi lo lancia dal prossimo colpo. */
    SHIELD("Scudo"),

    /** Fa recuperare punti vita a chi lo lancia. */
    HEAL("Cura");

    private final String displayName;

    Effect(String displayName) {
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
