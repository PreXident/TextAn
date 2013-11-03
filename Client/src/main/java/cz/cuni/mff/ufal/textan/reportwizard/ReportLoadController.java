package cz.cuni.mff.ufal.autopolan.reportwizard;

import cz.cuni.mff.ufal.autopolan.WindowController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import org.controlsfx.dialog.Dialogs;

/**
 * Controls initial loading of reports.
 */
public class ReportLoadController extends WindowController {

    @FXML
    private BorderPane root;

    @FXML
    private RadioButton emptyMessageRadioButton;

    @FXML
    private void cancel() {
        window.close();
    }

    @FXML
    private void next() {
        if (!emptyMessageRadioButton.isSelected()) {
            callWithContentBackup(() -> {
                Dialogs.create()
                        .owner(root)
                        .title("Zatím neimplementováno!")
                        .message("Zvolili jste možnost, která nebyla doposud implementována")
                        .lightweight()
                        .showError();
            });
        } else {
            nextFrame("02_ReportEdit.fxml");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
