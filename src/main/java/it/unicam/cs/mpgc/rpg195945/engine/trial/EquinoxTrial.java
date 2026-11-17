package it.unicam.cs.mpgc.rpg195945.engine.trial;

import it.unicam.cs.mpgc.rpg195945.engine.duel.ElementalRule;
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
 * La Prova dell'Equinozio: alla fine dell'anno lo studente affronta il
 * Guardiano dell'Equinozio, custode dell'equilibrio tra gli elementi.
 * <p>
 * Il Guardiano usa l'elemento "opposto" a quello dello studente, cioè uno su
 * cui nessuno dei due ha vantaggio: per batterlo conviene aver imparato a
 * lezione l'elemento che lo mette in difficoltà, oltre a essere saliti di
 * livello. Conosce tutte le rune, quindi attacca, si protegge e si cura.
 */
public final class EquinoxTrial {

    /** Statistiche del Guardiano: molto più forte degli avversari di allenamento. */
    public static final Stats GUARDIAN_STATS = new Stats(180, 70, 10, 10, 9);

    private EquinoxTrial() {
    }

    /**
     * @param student lo studente che affronta la Prova
     * @param rule    la regola del vantaggio elementale usata nei duelli
     * @return il Guardiano preparato per questo studente
     */
    public static Opponent guardianFor(Student student, ElementalRule rule) {
        Objects.requireNonNull(student, "student");
        Objects.requireNonNull(rule, "rule");
        Element element = guardianElement(student.affinity().element(), rule);
        List<Spell> spells = new ArrayList<>();
        for (Rune rune : Rune.all()) {
            spells.add(new Spell(element, rune));
        }
        return new Opponent("Guardiano dell'Equinozio",
                "Il custode dell'equilibrio tra gli elementi: solo chi lo batte supera l'anno.",
                element, GUARDIAN_STATS, spells, 0);
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
}
