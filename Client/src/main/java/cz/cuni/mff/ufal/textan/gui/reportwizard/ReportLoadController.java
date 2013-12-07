package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
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
        closeContainer();
    }

    @FXML
    private void next() {
        if (!emptyMessageRadioButton.isSelected()) {
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title("Zatím neimplementováno!")
                        .message("Zvolili jste možnost, která nebyla doposud implementována")
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
