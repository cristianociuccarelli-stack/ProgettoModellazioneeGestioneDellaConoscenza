package it.unicam.cs.mpgc.rpg195945.controller;

import it.unicam.cs.mpgc.rpg195945.engine.activity.Activity;
import it.unicam.cs.mpgc.rpg195945.engine.activity.ElementLesson;
import it.unicam.cs.mpgc.rpg195945.engine.activity.RuneLesson;
import it.unicam.cs.mpgc.rpg195945.engine.activity.Training;
import it.unicam.cs.mpgc.rpg195945.engine.duel.Combatant;
import it.unicam.cs.mpgc.rpg195945.engine.duel.Duel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementWheel;
import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementalRule;
import it.unicam.cs.mpgc.rpg195945.engine.trial.EquinoxTrial;
import it.unicam.cs.mpgc.rpg195945.model.calendar.AcademicYear;
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
    private final List<Activity> activities = List.of(new Training(), new RuneLesson(), new ElementLesson());
    private Student student;
    private AcademicYear year;
    private Duel duel;
    private Opponent opponent;
    private boolean inTrial;
    private boolean trialTaken;
    private boolean trialPassed;

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
     * Crea lo studente con cui si gioca e fa iniziare l'anno accademico.
     * Se c'era una partita precedente, ricomincia da capo.
     *
     * @throws IllegalArgumentException se il nome è vuoto
     */
    public Student createStudent(String name, Affinity affinity) {
        Objects.requireNonNull(affinity, "Scegli un'affinità");
        student = new Student(name, affinity);
        year = new AcademicYear();
        duel = null;
        opponent = null;
        inTrial = false;
        trialTaken = false;
        trialPassed = false;
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
     * @throws IllegalStateException se non si può iniziare una nuova attività
     *                               (vedi {@link #requireFreeWeek()})
     */
    public Duel startDuel(Opponent chosen) {
        Objects.requireNonNull(chosen, "opponent");
        requireFreeWeek();
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
     * dell'avversario. Un duello normale occupa la settimana, quindi si passa
     * alla successiva; se invece era la Prova dell'Equinozio, se ne registra
     * l'esito.
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
        if (inTrial) {
            trialTaken = true;
            trialPassed = duel.studentWon();
            inTrial = false;
        } else {
            year.advance();
        }
        duel = null;
        opponent = null;
        return reward;
    }

    /**
     * @return le attività che si possono svolgere durante una settimana
     */
    public List<Activity> activities() {
        return activities;
    }

    /**
     * Svolge un'attività: occupa tutta la settimana, quindi poi si passa alla
     * successiva.
     *
     * @return la frase che racconta cosa è successo
     * @throws IllegalArgumentException se lo studente non può svolgerla adesso
     * @throws IllegalStateException    se non si può iniziare una nuova attività
     */
    public String doActivity(Activity activity) {
        Objects.requireNonNull(activity, "activity");
        requireFreeWeek();
        if (!activity.isAvailableFor(student)) {
            throw new IllegalArgumentException(activity.name() + " non è disponibile");
        }
        String message = activity.perform(student);
        year.advance();
        return message;
    }

    /** @return la settimana corrente */
    public int week() {
        return requireYear().week();
    }

    /** @return le settimane di scuola che restano, compresa quella corrente */
    public int weeksLeft() {
        return requireYear().weeksLeft();
    }

    /** @return {@code true} quando è il momento della Prova dell'Equinozio */
    public boolean isYearOver() {
        return requireYear().isOver();
    }

    /**
     * @return il Guardiano che lo studente affronterà nella Prova dell'Equinozio
     */
    public Opponent trialGuardian() {
        requireYear();
        return EquinoxTrial.guardianFor(student, elementalRule);
    }

    /**
     * Inizia la Prova dell'Equinozio, il duello finale dell'anno.
     *
     * @throws IllegalStateException se le settimane di scuola non sono ancora
     *                               finite, la Prova è già stata affrontata o
     *                               c'è un duello in corso
     */
    public Duel startTrial() {
        if (!requireYear().isOver()) {
            throw new IllegalStateException("La Prova si affronta alla fine dell'anno");
        }
        if (trialTaken || duel != null) {
            throw new IllegalStateException("La Prova è già stata affrontata o è in corso");
        }
        opponent = trialGuardian();
        duel = new Duel(Combatant.of(student), Combatant.of(opponent), elementalRule);
        inTrial = true;
        return duel;
    }

    /** @return {@code true} se la Prova dell'Equinozio è stata affrontata */
    public boolean isTrialTaken() {
        return trialTaken;
    }

    /** @return {@code true} se lo studente ha superato la Prova dell'Equinozio */
    public boolean isTrialPassed() {
        return trialPassed;
    }

    private AcademicYear requireYear() {
        if (year == null) {
            throw new IllegalStateException("Prima devi creare lo studente");
        }
        return year;
    }

    /**
     * Controlla che si possa iniziare qualcosa di nuovo questa settimana: lo
     * studente esiste, l'anno non è finito e non c'è un duello in corso.
     */
    private void requireFreeWeek() {
        if (requireYear().isOver()) {
            throw new IllegalStateException("Le settimane di scuola sono finite");
        }
        if (duel != null) {
            throw new IllegalStateException("C'è già un duello in corso");
        }
    }
}
