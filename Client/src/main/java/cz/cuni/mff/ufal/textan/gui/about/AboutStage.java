package cz.cuni.mff.ufal.textan.gui.about;

import cz.cuni.mff.ufal.textan.gui.OuterStage;
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
import org.controlsfx.dialog.Dialogs;

/**
 * Class for displaying Settings in independent window.
 */
public class AboutStage extends OuterStage {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     */
    public AboutStage(final TextAnController textAnController,
            final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
        getInnerWindow().setResizableWindow(false);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.about.About");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("About.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getInnerWindow().getContentPane().getChildren().add(loadedRoot);
            getInnerWindow().setMinHeight(0);
            getInnerWindow().setMinWidth(0);
            getInnerWindow().setPrefWidth(PREF_WIDTH);
            getInnerWindow().setPrefHeight(PREF_HEIGHT);
            final AboutController controller = loader.getController();
            controller.setStage(this);
            controller.setSettings(settings);
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
