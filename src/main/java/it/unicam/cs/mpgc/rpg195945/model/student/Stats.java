package it.unicam.cs.mpgc.rpg195945.model.student;

/**
 * Le statistiche di un personaggio.
 * <p>
 * È un record, quindi immutabile: per far crescere un personaggio si crea un
 * nuovo oggetto con {@link #plus(Stats)}, invece di modificare quello esistente.
 *
 * @param health  punti vita massimi
 * @param mana    punti magia massimi, consumati dagli incantesimi
 * @param power   potenza degli incantesimi offensivi
 * @param defense riduzione dei danni subiti
 * @param speed   rapidità: chi è più rapido agisce per primo
 */
public record Stats(int health, int mana, int power, int defense, int speed) {

    /** Statistiche tutte a zero: utile come punto di partenza per le somme. */
    public static final Stats ZERO = new Stats(0, 0, 0, 0, 0);

    public Stats {
        if (health < 0 || mana < 0 || power < 0 || defense < 0 || speed < 0) {
            throw new IllegalArgumentException("Le statistiche non possono essere negative");
        }
    }

    /**
     * @return nuove statistiche, somma di queste e di {@code other}
     */
    public Stats plus(Stats other) {
        return new Stats(
                health + other.health,
                mana + other.mana,
                power + other.power,
                defense + other.defense,
                speed + other.speed);
    }

    /**
     * @param factor quante volte ripetere queste statistiche (zero o più)
     * @return nuove statistiche, ognuna moltiplicata per {@code factor}
     * @throws IllegalArgumentException se {@code factor} è negativo
     */
    public Stats times(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Il fattore non può essere negativo");
        }
        return new Stats(
                health * factor,
                mana * factor,
                power * factor,
                defense * factor,
                speed * factor);
    }
}
