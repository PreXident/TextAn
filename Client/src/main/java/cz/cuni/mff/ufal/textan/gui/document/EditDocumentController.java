package cz.cuni.mff.ufal.textan.gui.document;

import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

/**
 * Controls editing the report relations.
 */
public class EditDocumentController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Document Edit";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "document.edit";

    @FXML
    BorderPane root;

    @FXML
    TextArea textArea;

    /** Application controller. */
    TextAnController textAnController;

    /** Document to edit. Can be null if document is being added. */
    Document document;

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Synchronization lock. */
    protected Semaphore lock = new Semaphore(1);

    @FXML
    private void save() {
        if (lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            final String text = textArea.getText();
            new Thread(() -> {
                if (document == null) {
                    textAnController.getClient().addDocument(text);
                } else {
                    try {
                        textAnController.getClient().updateDocument(document.getId(), text);
                    } catch (IdNotFoundException e) { //should never happen
                        e.printStackTrace();
                    }
                }
                lock.release();
                Platform.runLater(() -> {
                    closeContainer();
                });
            }, "FromEntitiesState").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
    }

    /**
     * Sets document.
     * @param document new document;
     */
    public void setDocument(final Document document) {
        this.document = document;
        if (document != null) {
            textArea.setText(document.getText());
        }
    }

    /**
     * Sets application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
    }

    public String getTitle() {
        if (document == null) {
            return Utils.localize(resourceBundle, "document.add");
        } else {
            return Utils.localize(resourceBundle, PROPERTY_ID) + " - " + document.getId();
        }
    }
}
