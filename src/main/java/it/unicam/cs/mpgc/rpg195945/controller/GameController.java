package it.unicam.cs.mpgc.rpg195945.controller;

import it.unicam.cs.mpgc.rpg195945.engine.duel.Combatant;
import it.unicam.cs.mpgc.rpg195945.engine.duel.Duel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementWheel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementalRule;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
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

    private final ElementalRule elementalRule;
    private Student student;
    private Duel duel;
    private Opponent opponent;

    /** Controller con le regole standard del gioco. */
    public GameController() {
        this(ElementWheel.standard());
    }

    /**
     * @param elementalRule la regola del vantaggio elementale da usare nei duelli
     */
    public GameController(ElementalRule elementalRule) {
        this.elementalRule = Objects.requireNonNull(elementalRule, "elementalRule");
    }

    /**
     * @return le affinità tra cui il giocatore può scegliere
     */
    public List<Affinity> affinities() {
        return Affinity.all();
    }

    /**
     * Anteprima delle statistiche di partenza, da mostrare mentre il giocatore
     * sceglie l'affinità. Usa lo stesso calcolo di {@link Student#stats()},
     * così l'anteprima non può mai essere diversa dalla realtà.
     */
    public Stats startingStats(Affinity affinity) {
        return new Student("Anteprima", affinity).stats();
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

    /**
     * @return gli avversari che si possono sfidare
     */
    public List<Opponent> opponents() {
        return Opponent.all();
    }

    /**
     * Inizia un duello tra lo studente e l'avversario scelto.
     *
     * @throws IllegalStateException se lo studente non è ancora stato creato
     *                               o c'è già un duello in corso
     */
    public Duel startDuel(Opponent chosen) {
        Objects.requireNonNull(chosen, "opponent");
        if (student == null) {
            throw new IllegalStateException("Prima devi creare lo studente");
        }
        if (duel != null) {
            throw new IllegalStateException("C'è già un duello in corso");
        }
        opponent = chosen;
        duel = new Duel(Combatant.of(student), Combatant.of(chosen), elementalRule);
        return duel;
    }

    /**
     * @return il duello in corso, oppure vuoto se non se ne sta giocando nessuno
     */
    public Optional<Duel> duel() {
        return Optional.ofNullable(duel);
    }

    /**
     * Chiude il duello finito: se lo studente ha vinto riceve l'esperienza
     * dell'avversario.
     *
     * @return l'esperienza guadagnata (0 in caso di sconfitta)
     * @throws IllegalStateException se non c'è un duello o non è ancora finito
     */
    public int finishDuel() {
        if (duel == null || !duel.isOver()) {
            throw new IllegalStateException("Non c'è nessun duello concluso da chiudere");
        }
        int reward = duel.studentWon() ? opponent.experienceReward() : 0;
        student.gainExperience(reward);
        duel = null;
        opponent = null;
        return reward;
    }
}
