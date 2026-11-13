package it.unicam.cs.mpgc.rpg195945.engine.duel;

import it.unicam.cs.mpgc.rpg195945.model.magic.Effect;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Un duello magico a turni tra lo studente e un avversario.
 * <p>
 * A ogni turno lo studente sceglie se lanciare un incantesimo o concentrarsi
 * per recuperare mana; l'avversario sceglie da solo. Agisce per primo chi è
 * più veloce. Il duello finisce quando uno dei due resta senza vita.
 * <p>
 * Il duello non sa nulla di studenti, avversari o grafica: lavora solo con due
 * {@link Combatant} e con una {@link ElementalRule}.
 */
public final class Duel {

    /** Moltiplicatore per gli incantesimi del proprio elemento. */
    public static final double AFFINITY_BONUS = 1.2;

    /** Mana recuperato concentrandosi per un turno. */
    public static final int CONCENTRATION_MANA = 15;

    private final Combatant student;
    private final Combatant opponent;
    private final ElementalRule rule;
    private int round;

    public Duel(Combatant student, Combatant opponent, ElementalRule rule) {
        this.student = Objects.requireNonNull(student, "student");
        this.opponent = Objects.requireNonNull(opponent, "opponent");
        this.rule = Objects.requireNonNull(rule, "rule");
        this.round = 0;
    }

    public Combatant student() {
        return student;
    }

    public Combatant opponent() {
        return opponent;
    }

    /** @return quanti turni sono stati giocati */
    public int round() {
        return round;
    }

    public boolean isOver() {
        return student.isDefeated() || opponent.isDefeated();
    }

    public boolean studentWon() {
        return opponent.isDefeated() && !student.isDefeated();
    }

    /**
     * Turno in cui lo studente lancia un incantesimo.
     *
     * @return la cronaca del turno, una riga per ogni cosa successa
     * @throws IllegalArgumentException se lo studente non può lanciarlo
     * @throws IllegalStateException    se il duello è già finito
     */
    public List<String> cast(Spell spell) {
        Objects.requireNonNull(spell, "spell");
        if (!student.canCast(spell)) {
            throw new IllegalArgumentException("Non puoi lanciare " + spell.name());
        }
        return playRound(Optional.of(spell));
    }

    /**
     * Turno in cui lo studente si concentra per recuperare mana.
     *
     * @return la cronaca del turno
     * @throws IllegalStateException se il duello è già finito
     */
    public List<String> concentrate() {
        return playRound(Optional.empty());
    }

    /** Un turno completo: agiscono tutti e due, prima il più veloce. */
    private List<String> playRound(Optional<Spell> studentAction) {
        if (isOver()) {
            throw new IllegalStateException("Il duello è già finito");
        }
        round++;
        List<String> log = new ArrayList<>();
        Optional<Spell> opponentAction = chooseOpponentAction();
        if (student.stats().speed() >= opponent.stats().speed()) {
            act(student, opponent, studentAction, log);
            act(opponent, student, opponentAction, log);
        } else {
            act(opponent, student, opponentAction, log);
            act(student, opponent, studentAction, log);
        }
        if (studentWon()) {
            log.add(opponent.name() + " è sconfitto. Hai vinto il duello!");
        } else if (student.isDefeated()) {
            log.add("Sei stato sconfitto da " + opponent.name() + ".");
        }
        return log;
    }

    /** L'azione di uno dei due: lanciare un incantesimo o concentrarsi. */
    private void act(Combatant caster, Combatant target, Optional<Spell> action, List<String> log) {
        if (caster.isDefeated() || target.isDefeated()) {
            return;
        }
        if (action.isEmpty() || !caster.canCast(action.get())) {
            int restored = caster.restoreMana(CONCENTRATION_MANA);
            log.add(caster.name() + " si concentra e recupera " + restored + " mana.");
            return;
        }
        Spell spell = action.get();
        caster.spendMana(spell.manaCost());
        switch (spell.effect()) {
            case DAMAGE -> {
                double multiplier = rule.multiplier(spell.element(), target.element());
                int taken = target.takeDamage(damage(caster, target, spell, multiplier));
                log.add(caster.name() + " lancia " + spell.name() + ": " + target.name()
                        + " subisce " + taken + " danni" + effectiveness(multiplier) + ".");
            }
            case SHIELD -> {
                int amount = spell.power() + caster.stats().defense() / 2;
                caster.raiseShield(amount);
                log.add(caster.name() + " lancia " + spell.name() + " e alza uno scudo da " + amount + ".");
            }
            case HEAL -> {
                int healed = caster.heal(spell.power() + caster.stats().power() / 2);
                log.add(caster.name() + " lancia " + spell.name() + " e recupera " + healed + " vita.");
            }
        }
    }

    /**
     * Danni = (potenza dell'incantesimo + potenza di chi lancia)
     * × vantaggio elementale × bonus di affinità − metà della difesa del
     * bersaglio. Un colpo fa sempre almeno 1 danno.
     */
    private static int damage(Combatant caster, Combatant target, Spell spell, double multiplier) {
        double raw = (spell.power() + caster.stats().power()) * multiplier;
        if (spell.element() == caster.element()) {
            raw = raw * AFFINITY_BONUS;
        }
        int damage = (int) Math.round(raw) - target.stats().defense() / 2;
        return Math.max(1, damage);
    }

    private static String effectiveness(double multiplier) {
        if (multiplier > 1) {
            return " (è super efficace!)";
        }
        if (multiplier < 1) {
            return " (è poco efficace)";
        }
        return "";
    }

    /**
     * Le scelte dell'avversario, in ordine di priorità:
     * <ol>
     *     <li>se ha meno di metà vita e sa curarsi, si cura;</li>
     *     <li>altrimenti lancia l'attacco più forte che può permettersi;</li>
     *     <li>altrimenti alza uno scudo;</li>
     *     <li>altrimenti si concentra.</li>
     * </ol>
     */
    private Optional<Spell> chooseOpponentAction() {
        boolean wounded = opponent.health() < opponent.stats().health() / 2;
        if (wounded) {
            Optional<Spell> heal = affordable(Effect.HEAL);
            if (heal.isPresent()) {
                return heal;
            }
        }
        Optional<Spell> attack = affordable(Effect.DAMAGE);
        if (attack.isPresent()) {
            return attack;
        }
        return affordable(Effect.SHIELD);
    }

    /** @return l'incantesimo più potente con quell'effetto che l'avversario può lanciare */
    private Optional<Spell> affordable(Effect effect) {
        Spell best = null;
        for (Spell spell : opponent.spells()) {
            if (spell.effect() == effect && opponent.canCast(spell)
                    && (best == null || spell.power() > best.power())) {
                best = spell;
            }
        }
        return Optional.ofNullable(best);
    }
}
