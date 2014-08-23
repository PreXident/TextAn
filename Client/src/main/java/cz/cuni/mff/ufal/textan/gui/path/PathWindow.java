package cz.cuni.mff.ufal.textan.gui.path;

import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.path.PathController.MIN_HEIGHT;
import static cz.cuni.mff.ufal.textan.gui.path.PathController.MIN_WIDTH;
import static cz.cuni.mff.ufal.textan.gui.path.PathController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.path.PathController.TITLE;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Window for joining objects.
 */
public class PathWindow extends InnerWindow {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     */
    public PathWindow(final TextAnController textAnController,
            final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
        setMinWidth(MIN_WIDTH);
        setMinHeight(MIN_HEIGHT);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.path.Path");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("Path.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getContentPane().getChildren().add(loadedRoot);
            final PathController controller = loader.getController();
            controller.setWindow(this);
            controller.setSettings(settings);
            controller.setTextAnController(textAnController);
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