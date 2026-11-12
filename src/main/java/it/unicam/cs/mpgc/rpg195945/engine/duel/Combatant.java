package it.unicam.cs.mpgc.rpg195945.engine.duel;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import it.unicam.cs.mpgc.rpg195945.model.magic.Spell;
import it.unicam.cs.mpgc.rpg195945.model.opponent.Opponent;
import it.unicam.cs.mpgc.rpg195945.model.student.Stats;
import it.unicam.cs.mpgc.rpg195945.model.student.Student;

import java.util.List;
import java.util.Objects;

/**
 * Uno dei due partecipanti a un duello, con il suo stato durante la sfida:
 * vita e mana attuali e scudo attivo.
 * <p>
 * Lo studente e gli avversari sono classi diverse, ma in duello si comportano
 * allo stesso modo: per questo il duello lavora solo con {@code Combatant}.
 * Lo stato del duello è separato dal personaggio: perdere vita in una sfida non
 * modifica lo studente, che alla sfida successiva riparte in piena forma.
 */
public final class Combatant {

    private final String name;
    private final Element element;
    private final Stats stats;
    private final List<Spell> spells;
    private int health;
    private int mana;
    private int shield;

    public Combatant(String name, Element element, Stats stats, List<Spell> spells) {
        this.name = Objects.requireNonNull(name, "name");
        this.element = Objects.requireNonNull(element, "element");
        this.stats = Objects.requireNonNull(stats, "stats");
        this.spells = List.copyOf(spells);
        this.health = stats.health();
        this.mana = stats.mana();
        this.shield = 0;
    }

    /** Lo studente entra in duello con l'elemento della sua affinità. */
    public static Combatant of(Student student) {
        return new Combatant(student.name(), student.affinity().element(), student.stats(), student.spells());
    }

    public static Combatant of(Opponent opponent) {
        return new Combatant(opponent.name(), opponent.element(), opponent.stats(), opponent.spells());
    }

    public String name() {
        return name;
    }

    public Element element() {
        return element;
    }

    /** @return le statistiche massime (vita e mana di partenza, potenza, difesa, velocità) */
    public Stats stats() {
        return stats;
    }

    public List<Spell> spells() {
        return spells;
    }

    public int health() {
        return health;
    }

    public int mana() {
        return mana;
    }

    public int shield() {
        return shield;
    }

    public boolean isDefeated() {
        return health == 0;
    }

    /**
     * @return {@code true} se conosce l'incantesimo e ha abbastanza mana
     */
    public boolean canCast(Spell spell) {
        return spells.contains(spell) && mana >= spell.manaCost();
    }

    /**
     * @throws IllegalStateException se il mana non basta
     */
    public void spendMana(int amount) {
        if (amount > mana) {
            throw new IllegalStateException(name + " non ha abbastanza mana");
        }
        mana -= amount;
    }

    /**
     * Recupera mana, senza superare il massimo.
     *
     * @return il mana effettivamente recuperato
     */
    public int restoreMana(int amount) {
        int restored = Math.min(amount, stats.mana() - mana);
        mana += restored;
        return restored;
    }

    /**
     * Subisce un colpo. Se c'è uno scudo attivo assorbe parte dei danni e poi
     * si dissolve: protegge solo dal colpo successivo.
     *
     * @return i danni effettivamente subiti dalla vita
     */
    public int takeDamage(int damage) {
        int absorbed = Math.min(shield, damage);
        shield = 0;
        int taken = Math.min(damage - absorbed, health);
        health -= taken;
        return taken;
    }

    /**
     * Recupera vita, senza superare il massimo.
     *
     * @return la vita effettivamente recuperata
     */
    public int heal(int amount) {
        int healed = Math.min(amount, stats.health() - health);
        health += healed;
        return healed;
    }

    /** Alza uno scudo; se ce n'è già uno, resta il più forte. */
    public void raiseShield(int amount) {
        shield = Math.max(shield, amount);
    }
}
