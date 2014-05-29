package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
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
    Slider slider;

    @FXML
    private void next() {
        if (pipeline.lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            new Thread(() -> {
                pipeline.setReportText(textArea.getText());
            }, "FromEditState").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        slider.addEventFilter(EventType.ROOT, e -> e.consume());
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        textArea.setText(pipeline.getReportText());
        textArea.textProperty().addListener(e -> pipeline.resetStepsBack());
    }
}
