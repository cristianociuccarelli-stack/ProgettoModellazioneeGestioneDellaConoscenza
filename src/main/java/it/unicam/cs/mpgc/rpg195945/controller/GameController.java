package it.unicam.cs.mpgc.rpg195945.controller;

import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Punto di ingresso della grafica verso il gioco.
 * <p>
 * La grafica non crea né modifica direttamente gli oggetti del modello: chiede
 * tutto a questo controller. Così, se domani cambiassimo grafica (per esempio
 * una versione da terminale), basterebbe riusare questa classe.
 */
public class GameController {

    private Student student;

    /**
     * @return le affinità tra cui il giocatore può scegliere
     */
    public List<Affinity> affinities() {
        return Affinity.all();
    }

    /**
     * Crea lo studente con cui si gioca.
     *
     * @throws IllegalArgumentException se il nome è vuoto
     */
    public Student createStudent(String name, Affinity affinity) {
        Objects.requireNonNull(affinity, "Scegli un'affinità");
        student = new Student(name, affinity);
        return student;
    }

    /**
     * @return lo studente creato, oppure vuoto se la partita non è ancora iniziata
     */
    public Optional<Student> student() {
        return Optional.ofNullable(student);
    }
}
