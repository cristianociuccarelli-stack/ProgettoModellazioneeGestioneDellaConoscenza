package it.unicam.cs.mpgc.rpg195945.view.javafx;

import it.unicam.cs.mpgc.rpg195945.model.magic.Element;
import javafx.scene.Node;

/**
 * Come si mostrano gli elementi nella grafica: un simbolo e un colore.
 * <p>
 * I simboli sono quelli dell'alchimia (triangoli): il modello non ne sa
 * nulla, perché l'aspetto grafico è compito della vista. I colori stanno nel
 * foglio di stile {@code academy.css}, nelle classi {@code element-...}.
 */
final class ElementStyle {

    private ElementStyle() {
    }

    /** @return il simbolo alchemico dell'elemento */
    static String symbol(Element element) {
        return switch (element) {
            case FIRE -> "▲";
            case WATER -> "▼";
            case AIR -> "△";
            case EARTH -> "▽";
        };
    }

    /** @return il nome dell'elemento preceduto dal suo simbolo, per esempio "▲ Fuoco" */
    static String name(Element element) {
        return symbol(element) + " " + element.displayName();
    }

    /** @return la classe CSS che dà il colore dell'elemento */
    static String styleClass(Element element) {
        return "element-" + element.name().toLowerCase();
    }

    /** Colora un'etichetta o un pulsante con il colore dell'elemento. */
    static <T extends Node> T paint(T node, Element element) {
        node.getStyleClass().add(styleClass(element));
        return node;
    }
}
