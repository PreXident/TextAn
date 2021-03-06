package cz.cuni.mff.ufal.textan.gui.about;

import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.MaximizeIcon;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.about.AboutController.PREF_HEIGHT;
import static cz.cuni.mff.ufal.textan.gui.about.AboutController.PREF_WIDTH;
import static cz.cuni.mff.ufal.textan.gui.about.AboutController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.about.AboutController.TITLE;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import jfxtras.labs.scene.control.window.WindowIcon;
import org.controlsfx.dialog.Dialogs;

/**
 * Class for displaying Settings in inner window.
 */
public class AboutWindow extends InnerWindow {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     */
    public AboutWindow(final TextAnController textAnController,
            final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
        this.getStyleClass().remove(CLICKABLE_CLASS);
        this.setMinWidth(0);
        this.setMinHeight(0);
        this.setPrefWidth(PREF_WIDTH);
        this.setPrefHeight(PREF_HEIGHT);
        for (WindowIcon icon : this.getRightIcons()) {
            if (icon instanceof MaximizeIcon) {
                this.getRightIcons().remove(icon);
                break;
            }
        }
        setResizableWindow(false);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.about.About");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("About.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getContentPane().getChildren().add(loadedRoot);
            final AboutController controller = loader.getController();
            controller.setWindow(this);
            controller.setSettings(settings);
            setTitleFixed(Utils.localize(resourceBundle, PROPERTY_ID));
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.create()
                    .title(Utils.localize(resourceBundle, "page.load.error"))
                    .showException(e);
        }
    }

    @Override
    public void close() {
        super.close();
    }
}