package cz.cuni.mff.ufal.textan.gui.settings;

import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.settings.SettingsController.PREF_HEIGHT;
import static cz.cuni.mff.ufal.textan.gui.settings.SettingsController.PREF_WIDTH;
import static cz.cuni.mff.ufal.textan.gui.settings.SettingsController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.settings.SettingsController.TITLE;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Class for displaying Settings in independent window.
 */
public class SettingsStage extends OuterStage {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     */
    public SettingsStage(final TextAnController textAnController,
            final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
        getInnerWindow().setResizableWindow(false);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.settings.Settings");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getInnerWindow().getContentPane().getChildren().add(loadedRoot);
            getInnerWindow().setMinHeight(0);
            getInnerWindow().setMinWidth(0);
            getInnerWindow().setPrefWidth(PREF_WIDTH);
            getInnerWindow().setPrefHeight(PREF_HEIGHT);
            final SettingsController controller = loader.getController();
            controller.setStage(this);
            controller.setSettings(settings);
            controller.setTextAnController(textAnController);
            getInnerWindow().setTitleFixed(Utils.localize(resourceBundle, PROPERTY_ID));
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
