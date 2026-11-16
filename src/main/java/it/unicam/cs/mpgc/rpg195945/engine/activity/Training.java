package it.unicam.cs.mpgc.rpg195945.engine.activity;

import it.unicam.cs.mpgc.rpg195945.model.student.Student;

/**
 * Allenamento: lo studente si esercita e guadagna esperienza. È sempre
 * disponibile.
 */
public final class Training implements Activity {

    /** Esperienza guadagnata con un allenamento. */
    public static final int EXPERIENCE = 30;

    @Override
    public String name() {
        return "Allenamento";
    }

    @Override
    public String description() {
        return "Ti eserciti nel cortile dell'Accademia: +" + EXPERIENCE + " esperienza.";
    }

    @Override
    public boolean isAvailableFor(Student student) {
        return true;
    }

    @Override
    public String perform(Student student) {
        int levels = student.gainExperience(EXPERIENCE);
        String message = "Ti alleni con impegno e guadagni " + EXPERIENCE + " punti esperienza.";
        if (levels > 0) {
            message += " Sei salito al livello " + student.level() + "!";
        }
        return message;
    }
}
