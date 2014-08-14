package cz.cuni.mff.ufal.textan.gui;

import java.lang.reflect.Field;
import javafx.scene.Node;
import jfxtras.labs.internal.scene.control.skin.window.DefaultWindowSkin;

/**
 * Provides titlebar by reflection to allow doubleclicking it.
 */
public class ClickableWindowSkin extends DefaultWindowSkin {

    /**
     * Only constructor.
     * @param w skinned window
     */
    public ClickableWindowSkin(final InnerWindow w) {
        super(w);
        try {
            final Field titlebarField = DefaultWindowSkin.class.getDeclaredField("titleBar");
            titlebarField.setAccessible(true);
            final Node titlebar = (Node) titlebarField.get(this);
            titlebar.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    w.toggleMaximize();
                }
            });
        } catch (Exception e) {
            //we have failed, let's be silent so noone see this hack :-/
        }
    }
}
