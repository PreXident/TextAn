package cz.cuni.mff.ufal.textan.gui.document;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.document.DocumentListController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.document.DocumentListController.TITLE;
import static cz.cuni.mff.ufal.textan.gui.document.DocumentsWindow.MIN_HEIGHT;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Wizard for handling reports.
 */
public class DocumentsWindow extends InnerWindow {

    /** Minimal height of the join window. */
    static protected final int MIN_HEIGHT = 400;

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     * @param client client to communicate with server
     * @param object object whose documents should be displayed
     * @param relation relation whose documents should be displayed
     */
    public DocumentsWindow(final TextAnController textAnController,
            final Properties settings, final Client client, final Object object,
            final Relation relation) {
        super(TITLE, PROPERTY_ID, settings);
        setMinHeight(MIN_HEIGHT);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.document.DocumentList");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("DocumentList.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getContentPane().getChildren().add(loadedRoot);
            final DocumentListController controller = loader.getController();
            controller.setWindow(this);
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
