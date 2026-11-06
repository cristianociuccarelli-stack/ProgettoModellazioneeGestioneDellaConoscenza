package it.unicam.cs.mpgc.rpg195945;

import it.unicam.cs.mpgc.rpg195945.view.javafx.AcademyApp;
import javafx.application.Application;

/**
 * Punto di ingresso dell'applicazione.
 * <p>
 * È volutamente separato da {@link AcademyApp}: una classe main che non estende
 * {@link Application} permette di avviare JavaFX anche quando le sue librerie
 * sono sul classpath (come avviene con Gradle), evitando l'errore
 * "JavaFX runtime components are missing".
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Application.launch(AcademyApp.class, args);
    }
}
