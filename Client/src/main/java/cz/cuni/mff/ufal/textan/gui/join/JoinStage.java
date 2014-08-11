package cz.cuni.mff.ufal.textan.gui.join;

import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.join.JoinController.MIN_HEIGHT;
import static cz.cuni.mff.ufal.textan.gui.join.JoinController.MIN_WIDTH;
import static cz.cuni.mff.ufal.textan.gui.join.JoinController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.join.JoinController.TITLE;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Stage for joining objects.
 */
public class JoinStage extends OuterStage {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     */
    public JoinStage(final TextAnController textAnController,
            final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.join.Join");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("Join.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getInnerWindow().getContentPane().getChildren().add(loadedRoot);
            getInnerWindow().setMinHeight(MIN_HEIGHT);
            getInnerWindow().setMinWidth(MIN_WIDTH);
            final JoinController controller = loader.getController();
            controller.setStage(this);
            controller.setSettings(settings);
            controller.setTextAnController(textAnController);
            getInnerWindow().setTitle(Utils.localize(resourceBundle, PROPERTY_ID));
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
