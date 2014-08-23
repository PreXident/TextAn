package cz.cuni.mff.ufal.textan.gui.document;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
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
 * Class for displaying Report Wizard in independent window.
 */
public class DocumentsStage extends OuterStage {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     * @param client client to communicate with server
     * @param object object whose documents should be displayed
     */
    public DocumentsStage(final TextAnController textAnController,
            final Properties settings, final Client client, final Object object,
            final Relation relation) {
        super(TITLE, PROPERTY_ID, settings);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.document.DocumentList");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("DocumentList.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getInnerWindow().getContentPane().getChildren().add(loadedRoot);
            final DocumentListController controller = loader.getController();
            controller.setStage(this);
            controller.setSettings(settings);
            if (object != null) {
                controller.setObject(object);
            }
            if (relation != null) {
                controller.setRelation(relation);
            }
            controller.setTextAnController(textAnController);
            controller.setClient(client);
            controller.filter();
            getInnerWindow().setTitleFixed(Utils.localize(resourceBundle, PROPERTY_ID));
            getInnerWindow().setMinHeight(DocumentsWindow.MIN_HEIGHT);
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
