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
import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spellbook;
import it.unicam.cs.mpgc.rpg195945.model.student.Affinity;
import it.unicam.cs.mpgc.rpg195945.model.student.Progression;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;
import it.unicam.cs.mpgc.rpg195945.persistence.GameRepository;
import it.unicam.cs.mpgc.rpg195945.persistence.InMemoryGameRepository;
import it.unicam.cs.mpgc.rpg195945.persistence.PersistenceException;
import it.unicam.cs.mpgc.rpg195945.persistence.SavedGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Punto di ingresso della grafica verso il gioco.
 * <p>
 * La grafica non crea né modifica direttamente gli oggetti del modello: chiede
 * tutto a questo controller. Così, se domani cambiassimo grafica (per esempio
 * una versione da terminale), basterebbe riusare questa classe.
 * <p>
 * Il controller salva la partita da solo dopo ogni settimana, usando il
 * {@link GameRepository} che riceve nel costruttore: non sa se si salva su
 * file, in un database o in memoria.
 */
public class GameController {

    private final ElementalRule elementalRule;
    private final GameRepository repository;
    private final List<Activity> activities = List.of(new Training(), new RuneLesson(), new ElementLesson());
    private Student student;
    private AcademicYear year;
    private Duel duel;
    private Opponent opponent;
    private boolean inTrial;
    private boolean completed;
    private int failedTrials;

    /** Controller con le regole standard e un salvataggio solo in memoria. */
    public GameController() {
        this(ElementWheel.standard());
    }

    /**
     * Controller con un salvataggio solo in memoria (utile nei test).
     *
     * @param elementalRule la regola del vantaggio elementale da usare nei duelli
     */
    public GameController(ElementalRule elementalRule) {
        this(elementalRule, new InMemoryGameRepository());
    }

    /**
     * @param elementalRule la regola del vantaggio elementale da usare nei duelli
     * @param repository    dove salvare e caricare la partita
     */
    public GameController(ElementalRule elementalRule, GameRepository repository) {
        this.elementalRule = Objects.requireNonNull(elementalRule, "elementalRule");
        this.repository = Objects.requireNonNull(repository, "repository");
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
     * Crea lo studente con cui si gioca e lo iscrive al primo anno.
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
        completed = false;
        failedTrials = 0;
        save();
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
     * alla successiva. Se invece era la Prova dell'Equinozio: vincendo si passa
     * all'anno dopo (o, dopo l'ultimo guardiano, ci si diploma), perdendo si
     * ripete lo stesso anno.
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
            finishTrial(duel.studentWon());
        } else {
            year.advance();
        }
        duel = null;
        opponent = null;
        save();
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
        save();
        return message;
    }

    /** @return l'anno corrente, da 1 a {@link AcademicYear#LAST_YEAR} */
    public int year() {
        return requireYear().year();
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
     * @return il guardiano che lo studente affronterà alla fine di quest'anno
     */
    public Opponent trialGuardian() {
        return EquinoxTrial.guardianOfYear(requireYear().year(), student, elementalRule);
    }

    /**
     * Inizia la Prova dell'Equinozio contro il guardiano dell'anno.
     *
     * @throws IllegalStateException se le settimane di scuola non sono ancora
     *                               finite, il percorso è già concluso o c'è
     *                               un duello in corso
     */
    public Duel startTrial() {
        if (!requireYear().isOver()) {
            throw new IllegalStateException("La Prova si affronta alla fine dell'anno");
        }
        if (completed || duel != null) {
            throw new IllegalStateException("Il percorso è concluso o c'è già un duello in corso");
        }
        opponent = trialGuardian();
        duel = new Duel(Combatant.of(student), Combatant.of(opponent), elementalRule);
        inTrial = true;
        return duel;
    }

    /** @return {@code true} se lo studente ha battuto tutti i guardiani e si è diplomato */
    public boolean isCompleted() {
        return completed;
    }

    /** @return quante Prove dell'Equinozio lo studente ha fallito */
    public int failedTrials() {
        return failedTrials;
    }

    /**
     * Quanto un elemento è efficace contro un altro, secondo la regola usata
     * nei duelli. Serve alla guida per spiegare la ruota degli elementi.
     */
    public double elementalMultiplier(Element attack, Element target) {
        return elementalRule.multiplier(attack, target);
    }

    /**
     * @return i guardiani di tutti gli anni, in ordine
     */
    public List<Opponent> guardians() {
        Student current = student().orElseThrow(() -> new IllegalStateException("Prima devi creare lo studente"));
        List<Opponent> guardians = new ArrayList<>();
        for (int number = 1; number <= AcademicYear.LAST_YEAR; number++) {
            guardians.add(EquinoxTrial.guardianOfYear(number, current, elementalRule));
        }
        return guardians;
    }

    /**
     * @return {@code true} se c'è una partita salvata da continuare
     */
    public boolean hasSavedGame() {
        try {
            return repository.load().isPresent();
        } catch (PersistenceException e) {
            return false;
        }
    }

    /**
     * Riprende la partita salvata dal punto in cui era stata lasciata.
     *
     * @return lo studente della partita ripresa
     * @throws IllegalStateException se non c'è nessuna partita salvata
     * @throws PersistenceException  se il salvataggio non si riesce a leggere
     */
    public Student continueGame() {
        SavedGame saved = repository.load()
                .orElseThrow(() -> new IllegalStateException("Non c'è nessuna partita salvata"));
        Affinity affinity = affinityOf(saved.affinityElement());
        student = new Student(saved.studentName(), affinity,
                new Progression(saved.level(), saved.experience()),
                new Spellbook(saved.elements(), saved.runes()));
        year = new AcademicYear(saved.year(), saved.week());
        duel = null;
        opponent = null;
        inTrial = false;
        completed = false;
        failedTrials = saved.failedTrials();
        return student;
    }

    /**
     * Salva la "fotografia" della partita. Quando il percorso è concluso il
     * salvataggio non serve più e viene cancellato.
     */
    private void save() {
        if (completed) {
            repository.delete();
            return;
        }
        repository.save(new SavedGame(student.name(), student.affinity().element(),
                student.level(), student.experience(), student.knownElements(), student.knownRunes(),
                year.year(), year.week(), failedTrials));
    }

    /** Ritrova l'affinità a partire dal suo elemento: ogni affinità ha un elemento diverso. */
    private static Affinity affinityOf(Element element) {
        for (Affinity affinity : Affinity.all()) {
            if (affinity.element() == element) {
                return affinity;
            }
        }
        throw new PersistenceException("Nessuna affinità per l'elemento " + element, null);
    }

    /** Applica l'esito della Prova: anno successivo, diploma o anno da ripetere. */
    private void finishTrial(boolean passed) {
        inTrial = false;
        if (!passed) {
            failedTrials++;
            year.repeatYear();
        } else if (year.isLastYear()) {
            completed = true;
        } else {
            year.nextYear();
        }
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
