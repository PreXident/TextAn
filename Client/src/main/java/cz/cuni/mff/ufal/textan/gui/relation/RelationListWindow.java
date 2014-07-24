package cz.cuni.mff.ufal.textan.gui.relation;

import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.relation.RelationListController.MIN_HEIGHT;
import static cz.cuni.mff.ufal.textan.gui.relation.RelationListController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.relation.RelationListController.TITLE;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Window for joining objects.
 */
public class RelationListWindow extends InnerWindow {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     */
    public RelationListWindow(final TextAnController textAnController,
            final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
        setMinHeight(MIN_HEIGHT);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.relation.RelationList");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("RelationList.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getContentPane().getChildren().add(loadedRoot);
            final RelationListController controller = loader.getController();
            controller.setWindow(this);
            controller.setSettings(settings);
            controller.setTextAnController(textAnController);
            setTitle(Utils.localize(resourceBundle, PROPERTY_ID));
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