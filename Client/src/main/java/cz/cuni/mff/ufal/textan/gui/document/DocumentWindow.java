package cz.cuni.mff.ufal.textan.gui.document;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.document.DocumentListController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.document.DocumentListController.TITLE;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Wizard for handling reports.
 */
public class DocumentWindow extends InnerWindow {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     * @param client client to communicate with server
     * @param objectId object id
     */
    public DocumentWindow(final TextAnController textAnController,
            final Properties settings, final Client client, final long objectId) {
        super(TITLE, PROPERTY_ID, settings);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.document.DocumentList");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("DocumentList.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getContentPane().getChildren().add(loadedRoot);
            final DocumentListController controller = loader.getController();
            controller.setWindow(this);
            controller.setSettings(settings);
            if (objectId != -1) {
                controller.setObjectId(objectId);
            }
            controller.setTextAnController(textAnController);
            controller.setClient(client);
            controller.filter();
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
