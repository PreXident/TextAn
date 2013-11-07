package cz.cuni.mff.ufal.textan;

import cz.cuni.mff.ufal.textan.reportwizard.ReportWizard;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import jfxtras.labs.scene.control.window.Window;

/**
 * Controller for the TextAn application.
 */
public class TextAnController implements Initializable {

    /** Original title. */
    static protected final String TITLE = "TextAn";

    @FXML
    private BorderPane root;

    @FXML
    private AnchorPane content;

    /** Properties with application settings. */
    protected Properties settings = null;

    /** Property binded to stage titleProperty. */
    StringProperty titleProperty = new SimpleStringProperty(TITLE);

    @FXML
    private void close() {
        Platform.exit();
    }

    @FXML
    private void reportWizard() {
        final ReportWizard wizard = new ReportWizard(settings);
        content.getChildren().add(wizard);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        System.out.printf("Initializing...\n");
        content.addEventFilter(MouseEvent.ANY, (MouseEvent t) -> {
            if (t.getSceneX()< 0 || t.getSceneY() < 0
                    || t.getSceneX() > getWindow().getWidth()
                    || t.getSceneY() > getWindow().getHeight()) {
                t.consume();
            }
        });
    }

    /**
     * Sets TextAn settings.
     * @param settings new settings
     */
    public void setSettings(final Properties settings) {
        this.settings = settings;
    }

    /**
     * Returns title property.
     * @return title property
     */
    public StringProperty titleProperty() {
        return titleProperty;
    }

    /**
     * Returns window of the root.
     * @return window of the root
     */
    private javafx.stage.Window getWindow() {
        return root.getScene().getWindow();
    }

    /**
     * Called when application is stopped to store settings etc.
     */
    public void stop() {
        content.getChildren().stream()
                .filter(n -> n instanceof Window)
                .map(n -> (Window) n)
                .forEach(w -> w.close());
    }
}
