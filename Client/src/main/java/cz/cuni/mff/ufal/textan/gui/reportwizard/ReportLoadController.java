package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.io.File;
import java.net.URL;
import java.util.Properties;
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

    /**
     * Prompts user to select the report to load.
     * @param resourceBundle localization container
     * @param settings application setttings
     * @param window parent window of the file chooser
     * @param pipeline pipeline to load to
     * @return true if loading was successful, false on user cancel
     */
    static public boolean loadReport(final ResourceBundle resourceBundle,
            final Properties settings, final Window window,
            final ProcessReportPipeline pipeline) {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle(Utils.localize(resourceBundle, "load.report.prompt"));
        final String dir = settings.getProperty("loadreport.dir");
        if (dir != null && !dir.isEmpty()) {
            chooser.setInitialDirectory(new File(dir));
        } else {
            chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }
        final File file = chooser.showOpenDialog(window);
        if (file == null || !file.isFile()) {
            return false;
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
        return true;
    }

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
                loadReport(resourceBundle,
                        settings,
                        window != null ? window.getScene().getWindow() : stage,
                        pipeline);
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
        super.initialize(url, rb);
    }
}
