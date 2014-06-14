package cz.cuni.mff.ufal.textan.gui;

import com.sun.javafx.scene.control.skin.MenuButtonSkin;
import javafx.scene.control.MenuButton;

/**
 * MenuButtonSkin that handles context menus stylesheets problems with
 * scenes better.
 */
public class MyMenuButtonSkin extends MenuButtonSkin {

    /**
     * Only constructor.
     * @param menuButton skinned menuButton
     */
    public MyMenuButtonSkin(final MenuButton menuButton) {
        super(menuButton);
        popup.getStyleClass().add("settings-menu");
    }
}
