package it.unicam.cs.mpgc.rpg195945.persistence;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;

import java.util.List;
import java.util.Objects;

/**
 * Tutto quello che serve per riprendere una partita: lo studente (nome,
 * affinità, livello, esperienza, elementi e rune conosciuti) e il punto del
 * percorso (anno, settimana, Prove fallite).
 * <p>
 * È una "fotografia" fatta solo di dati semplici: chi salva non deve
 * conoscere le classi del modello, e il modello non deve sapere come viene
 * salvato. Un duello in corso non viene salvato: si salva tra una settimana e
 * l'altra.
 *
 * @param studentName     nome dello studente
 * @param affinityElement elemento dell'affinità scelta (ogni affinità ha un elemento diverso)
 * @param level           livello
 * @param experience      esperienza verso il livello successivo
 * @param elements        elementi conosciuti
 * @param runes           rune conosciute
 * @param year            anno del percorso
 * @param week            settimana dell'anno
 * @param failedTrials    Prove dell'Equinozio fallite finora
 */
public record SavedGame(String studentName, Element affinityElement, int level, int experience,
                        List<Element> elements, List<Rune> runes, int year, int week, int failedTrials) {

    public SavedGame {
        Objects.requireNonNull(studentName, "studentName");
        Objects.requireNonNull(affinityElement, "affinityElement");
        elements = List.copyOf(elements);
        runes = List.copyOf(runes);
    }
}
