package it.unicam.cs.mpgc.rpg195945.engine.activity;

import it.unicam.cs.mpgc.rpg195945.model.student.Student;

/**
 * Un'attività che lo studente può svolgere durante una settimana.
 * <p>
 * È un'interfaccia: il calendario e la grafica conoscono solo questa. Per
 * aggiungere un'attività nuova basta scrivere un'altra classe che la
 * implementa, senza modificare il resto del gioco.
 */
public interface Activity {

    /** @return il nome da mostrare sul pulsante */
    String name();

    /** @return una breve descrizione di cosa fa */
    String description();

    /**
     * @return {@code true} se lo studente può svolgerla adesso
     */
    boolean isAvailableFor(Student student);

    /**
     * Svolge l'attività, modificando lo studente.
     *
     * @return una frase che racconta cosa è successo
     */
    String perform(Student student);
}
