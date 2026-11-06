package it.unicam.cs.mpgc.rpg195945.model.student;

import java.util.List;
import java.util.Objects;

/**
 * L'affinità elementale scelta dallo studente all'inizio del gioco: è
 * l'equivalente della "classe" in un gioco di ruolo.
 * <p>
 * Ogni affinità dà un bonus iniziale alle statistiche e stabilisce quanto
 * crescono a ogni livello. È un record e non un enum: per aggiungere una nuova
 * affinità basta creare un nuovo valore, senza modificare questa classe.
 *
 * @param element     l'elemento a cui è legata
 * @param title       il titolo dello studente (es. "Piromante")
 * @param description breve descrizione per il giocatore
 * @param bonus       statistiche aggiunte a quelle di base
 * @param growth      statistiche guadagnate a ogni livello
 */
public record Affinity(Element element, String title, String description, Stats bonus, Stats growth) {

    public static final Affinity FIRE = new Affinity(Element.FIRE, "Piromante",
            "Incantesimi devastanti: fa più danni di tutti, ma resiste poco.",
            new Stats(0, 0, 5, 0, 1), new Stats(8, 4, 3, 1, 1));

    public static final Affinity WATER = new Affinity(Element.WATER, "Idromante",
            "Grande riserva di mana: lancia più incantesimi e resiste più a lungo.",
            new Stats(0, 20, 1, 2, 0), new Stats(8, 8, 1, 2, 1));

    public static final Affinity EARTH = new Affinity(Element.EARTH, "Geomante",
            "Solido come la roccia: tantissima vita e difesa, ma è lento.",
            new Stats(30, 0, 0, 4, 0), new Stats(14, 3, 1, 3, 0));

    public static final Affinity AIR = new Affinity(Element.AIR, "Aeromante",
            "Rapido come il vento: agisce quasi sempre per primo.",
            new Stats(0, 5, 2, 0, 5), new Stats(7, 5, 2, 1, 3));

    public Affinity {
        Objects.requireNonNull(element, "element");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(bonus, "bonus");
        Objects.requireNonNull(growth, "growth");
    }

    /**
     * @return le affinità tra cui si può scegliere all'inizio del gioco
     */
    public static List<Affinity> all() {
        return List.of(FIRE, WATER, EARTH, AIR);
    }

    @Override
    public String toString() {
        return title + " (" + element.displayName() + ")";
    }
}
