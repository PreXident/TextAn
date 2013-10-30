package cz.cuni.mff.ufal.autopolan.reportwizard;

import cz.cuni.mff.ufal.autopolan.WindowController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import org.controlsfx.dialog.Dialogs;

/**
 * Controls editing the report.
 */
public class ReportEditController extends WindowController {

    static final String TEST_TEXT = "Ahoj, toto je testovaci zprava urcena pro vyzkouseni vsech moznosti oznacovani textu.";

    @FXML
    TextArea textArea;

    @FXML
    ScrollPane scrollPane;

    @FXML
    private void cancel() {
        window.close();
    }

    @FXML
    private void next() {
        final ReportEntitiesController controller = nextFrame("03_ReportEntities.fxml");
        controller.setReport(textArea.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        textArea.prefWidthProperty().bind(scrollPane.widthProperty());
//        textArea.prefHeightProperty().bind(scrollPane.heightProperty());
        textArea.setText(TEST_TEXT);
    }
}
