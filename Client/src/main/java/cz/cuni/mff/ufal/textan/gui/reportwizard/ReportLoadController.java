package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

/**
 * Controls initial loading of reports.
 */
public class ReportLoadController extends ReportWizardController {

    @FXML
    private BorderPane root;

    @FXML
    private RadioButton databaseRadioButton;

    @FXML
    private RadioButton fileRadioButton;

    @FXML
    private RadioButton emptyMessageRadioButton;

    @FXML
    private RadioButton loadRadioButton;

    @FXML
    private ToggleGroup loadToggleGroup;

    /** Localization container. */
    ResourceBundle resourceBundle;

    @FXML
    private void cancel() {
        closeContainer();
    }

    @FXML
    private void next() {
        final Toggle toggled = loadToggleGroup.getSelectedToggle();
        try {
            if (toggled == databaseRadioButton) {
                pipeline.selectDatabaseDatasource();
            } else if (toggled == fileRadioButton) {
                pipeline.selectFileDatasource();
            } else if (toggled == emptyMessageRadioButton) {
                pipeline.selectEmptyDatasource();
            } else if (toggled == loadRadioButton) {
                pipeline.selectLoadDatasource();
            }
        } catch (Exception e) {
            e.printStackTrace();
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "error"))
                        .showException(e);
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
    }
}
