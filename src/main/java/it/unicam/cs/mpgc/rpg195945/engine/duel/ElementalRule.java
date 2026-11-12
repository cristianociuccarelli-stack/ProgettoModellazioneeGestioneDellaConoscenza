package it.unicam.cs.mpgc.rpg195945.engine.duel;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;

/**
 * Regola che stabilisce quanto un elemento è efficace contro un altro.
 * <p>
 * È un'interfaccia: il motore del duello conosce solo questa, non sa quale
 * regola concreta sta usando. Per cambiare le regole (per esempio una modalità
 * di gioco in cui gli elementi non contano) basta scrivere un'altra
 * implementazione, senza toccare il duello.
 */
public interface ElementalRule {

    /**
     * @param attack l'elemento dell'incantesimo lanciato
     * @param target l'elemento di chi lo riceve
     * @return il moltiplicatore dei danni: più di 1 se l'attacco è efficace,
     *         meno di 1 se è poco efficace, 1 se è neutro
     */
    double multiplier(Element attack, Element target);
}
