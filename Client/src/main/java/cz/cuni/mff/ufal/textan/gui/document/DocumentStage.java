package cz.cuni.mff.ufal.textan.gui.document;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.document.DocumentViewController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.document.DocumentViewController.TITLE;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Class for displaying Report Wizard in independent window.
 */
public class DocumentStage extends OuterStage {

    /**
     * Only constructor.
     * @param textAnController application controller
     * @param settings properties with settings
     * @param client client to communicate with server
     * @param document document
     */
    public DocumentStage(final TextAnController textAnController,
            final Properties settings, final Client client, final Document document) {
        super(TITLE, PROPERTY_ID, settings);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.document.DocumentView");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("DocumentView.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            getInnerWindow().getContentPane().getChildren().add(loadedRoot);
            final DocumentViewController controller = loader.getController();
            controller.setStage(this);
            controller.setSettings(settings);
            controller.setTextAnController(textAnController);
            controller.setClient(client);
            controller.setDocument(document);
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
