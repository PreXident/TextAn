package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.Window;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

/**
 * Common ancestor of controllers in this package.
 */
public abstract class ReportWizardController extends WindowController {

    /** Pipeline controlling the report processing. */
    protected ProcessReportPipeline pipeline;

    @FXML
    TextFlow textFlow;


    @Override
    public void setWindow(final Window window) {
        super.setWindow(window);
        if (textFlow != null) {
            window.boundsInLocalProperty().addListener(e -> {
                textFlow.layoutChildren();
            });
        }
    }

    @Override
    public void setStage(final OuterStage stage) {
        super.setStage(stage);
        if (textFlow != null) {
            stage.getInnerWindow().boundsInLocalProperty().addListener(e -> {
                textFlow.layoutChildren();
            });
        }
    }

    /**
     * Sets pipeline for this controller.
     * @param pipeline new pipeline
     */
    public void setPipeline(final ProcessReportPipeline pipeline) {
        this.pipeline = pipeline;
    }
}
