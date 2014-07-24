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
import javafx.scene.layout.BorderPane;

/**
 * Controls editing the report.
 */
public class ReportEditController extends ReportWizardController {

    @FXML
    BorderPane root;

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
                final String t = textArea.getText().replace("\r", "");
                handleDocumentChangedException(root, () -> {
                    pipeline.setReportText(t);
                    return null;
                });
            }, "FromEditState").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        slider.addEventFilter(EventType.ROOT, e -> e.consume());
        slider.setLabelFormatter(new SliderLabelFormatter());
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        textArea.setText(pipeline.getReportText().replace("\r", ""));
        textArea.textProperty().addListener(e -> pipeline.resetStepsBack());
    }
}
