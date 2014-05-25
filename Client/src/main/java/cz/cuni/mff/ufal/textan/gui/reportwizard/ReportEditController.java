package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

/**
 * Controls editing the report.
 */
public class ReportEditController extends ReportWizardController {

    @FXML
    TextArea textArea;

    @FXML
    ScrollPane scrollPane;

    @FXML
    private void next() {
        pipeline.setReportText(textArea.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //nothing
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        textArea.setText(pipeline.getReportText());
        textArea.textProperty().addListener(e -> pipeline.resetStepsBack());
    }
}
