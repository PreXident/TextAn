package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
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
    private void next() {
        if (pipeline.lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            new Thread(() -> {
                final String t = textArea.getText().replace("\r", "");
                handleDocumentChangedException(root, () -> {
                    pipeline.setReportTextAndParse(t);
                    return null;
                });
            }, "FromEditState").start();
        }
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        super.initialize(url, rb);
        slider.setLabelFormatter(new SliderLabelFormatter());
    }


    @Override
    public Runnable getContainerCloser() {
        return () -> {
            pipeline.setReportText(textArea.getText());
            promptSave(root);
        };
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        textArea.setText(pipeline.getReportText().replace("\r", ""));
        textArea.textProperty().addListener(e -> resetStepsBack());
    }

    @Override
    public void nowInControl() {
        textArea.requestFocus();
    }
}
