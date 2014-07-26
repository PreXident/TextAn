package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import jfxtras.util.PlatformUtil;

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
                final FileChooser chooser = new FileChooser();
                chooser.setTitle(Utils.localize(resourceBundle, "load.report.prompt"));
                final String dir = settings.getProperty("loadreport.dir");
                if (dir != null && !dir.isEmpty()) {
                    chooser.setInitialDirectory(new File(dir));
                } else {
                    chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                }
                final Window w = window != null ? window.getScene().getWindow() : stage;
                final File file = chooser.showOpenDialog(w);
                if (file == null || !file.isFile()) {
                    return;
                }
                settings.setProperty("loadreport.dir", file.getParent());
                RelationBuilder.RelationInfo.deserializator = proxy -> {
                    return PlatformUtil.runAndWait(() -> {
                        return new FXRelationBuilder.FXRelationInfo(proxy.order, proxy.role, proxy.object);
                    });
                };
                RelationBuilder.deserializator = proxy -> {
                    return PlatformUtil.runAndWait(() -> {
                        return new FXRelationBuilder(proxy);
                    });
                };
                pipeline.selectLoadDatasource(file.getAbsolutePath());
                for (Word word : pipeline.getReportWords()) {
                    word.reregister();
                }
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
