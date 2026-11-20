package it.unicam.cs.mpgc.rpg195945.engine.trial;

import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementalRule;
import it.unicam.cs.mpgc.rpg195945.model.calendar.AcademicYear;
import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Rune;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Le Prove dell'Equinozio: alla fine di ogni anno lo studente affronta un
 * guardiano, ognuno con il suo elemento e le sue abilità, sempre più forte.
 * Per passare all'anno successivo bisogna batterlo.
 * <p>
 * I primi quattro guardiani sono fissi. L'ultimo, il Guardiano dell'Equinozio,
 * custode dell'equilibrio, si prepara apposta per lo studente: usa l'elemento
 * "opposto" al suo e conosce tutte le rune.
 */
public final class EquinoxTrial {

    public static final Opponent THRESHOLD_GUARDIAN = guardian("Guardiano della Soglia",
            "Una statua di pietra che custodisce il primo cancello: resistentissima, ma lenta.",
            Element.EARTH, new Stats(100, 30, 6, 10, 4), Rune.BOLT, Rune.WARD);

    public static final Opponent TIDE_GUARDIAN = guardian("Guardiana delle Maree",
            "Evoca onde che la curano: se non la colpisci forte, si rigenera.",
            Element.WATER, new Stats(120, 60, 8, 10, 8), Rune.BOLT, Rune.SPRING);

    public static final Opponent FORGE_GUARDIAN = guardian("Guardiano della Fornace",
            "Scaglia lance di fuoco: colpisce fortissimo, ma si difende poco.",
            Element.FIRE, new Stats(130, 60, 9, 8, 9), Rune.BOLT, Rune.LANCE);

    public static final Opponent WIND_GUARDIAN = guardian("Guardiana dei Venti",
            "Velocissima: agisce sempre prima di te e si ripara dietro barriere d'aria.",
            Element.AIR, new Stats(140, 70, 9, 8, 16), Rune.BOLT, Rune.LANCE, Rune.WARD);

    /** Statistiche dell'ultimo guardiano, il più forte di tutti. */
    public static final Stats EQUINOX_GUARDIAN_STATS = new Stats(200, 90, 14, 12, 12);

    private EquinoxTrial() {
    }

    /**
     * @param year    l'anno di cui si affronta la Prova, da 1 a {@link AcademicYear#LAST_YEAR}
     * @param student lo studente che affronta la Prova
     * @param rule    la regola del vantaggio elementale usata nei duelli
     * @return il guardiano di quell'anno
     * @throws IllegalArgumentException se l'anno non esiste
     */
    public static Opponent guardianOfYear(int year, Student student, ElementalRule rule) {
        return switch (year) {
            case 1 -> THRESHOLD_GUARDIAN;
            case 2 -> TIDE_GUARDIAN;
            case 3 -> FORGE_GUARDIAN;
            case 4 -> WIND_GUARDIAN;
            case 5 -> equinoxGuardianFor(student, rule);
            default -> throw new IllegalArgumentException("Non esiste un guardiano per l'anno " + year);
        };
    }

    /**
     * @return il Guardiano dell'Equinozio preparato per questo studente
     */
    public static Opponent equinoxGuardianFor(Student student, ElementalRule rule) {
        Objects.requireNonNull(student, "student");
        Objects.requireNonNull(rule, "rule");
        Element element = guardianElement(student.affinity().element(), rule);
        return guardian("Guardiano dell'Equinozio",
                "Il custode dell'equilibrio tra gli elementi: solo chi lo batte si diploma.",
                element, EQUINOX_GUARDIAN_STATS, Rune.all().toArray(new Rune[0]));
    }

    /**
     * Sceglie un elemento diverso da quello dello studente su cui, secondo la
     * regola, nessuno dei due ha vantaggio. Se non ce n'è nessuno, il
     * Guardiano usa lo stesso elemento dello studente.
     */
    static Element guardianElement(Element studentElement, ElementalRule rule) {
        for (Element element : Element.values()) {
            boolean neutral = rule.multiplier(element, studentElement) == 1.0
                    && rule.multiplier(studentElement, element) == 1.0;
            if (element != studentElement && neutral) {
                return element;
            }
        }
        return studentElement;
    }

    /** Crea un guardiano che conosce le rune indicate con il suo elemento. */
    private static Opponent guardian(String name, String description, Element element, Stats stats,
                                     Rune... runes) {
        List<Spell> spells = new ArrayList<>();
        for (Rune rune : runes) {
            spells.add(new Spell(element, rune));
        }
        return new Opponent(name, description, element, stats, spells, 0);
    }
}
