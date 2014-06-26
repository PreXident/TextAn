package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.Window;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.util.StringConverter;

/**
 * Common ancestor of controllers in this package.
 */
public abstract class ReportWizardController extends WindowController {

    /** Pipeline controlling the report processing. */
    protected ProcessReportPipeline pipeline;

    @FXML
    TextFlow textFlow;

    /** Parent controller. */
    protected TextAnController textAnController;

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

    /**
     * Sets parent controller
     * @param textAnController new parent controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
    }

    /**
     * Simple convertor to provide labels to progress sliders.
     */
    protected static class SliderLabelFormatter extends StringConverter<Double> {

        /** Localization container. */
        final ResourceBundle rb = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizard");

        @Override
        public String toString(Double val) {
            if (val < 1.5) {
                return Utils.localize(rb, "report.wizard.edit.label");
            }
            if (val < 2.5) {
                return Utils.localize(rb, "report.wizard.entities.label");
            }
            if (val < 3.5) {
                return Utils.localize(rb, "report.wizard.objects.label");
            }
            if (val < 4.5) {
                return Utils.localize(rb, "report.wizard.relations.label");
            }
            return "";
        }

        @Override
        public Double fromString(String string) {
            if (string == null || string.isEmpty()) {
                return 0D;
            }
            if (string.equals(Utils.localize(rb, "report.wizard.edit.label"))) {
                return 1D;
            }
            if (string.equals(Utils.localize(rb, "report.wizard.entities.label"))) {
                return 2D;
            }
            if (string.equals(Utils.localize(rb, "report.wizard.objects.label"))) {
                return 3D;
            }
            if (string.equals(Utils.localize(rb, "report.wizard.relations.label"))) {
                return 4D;
            }
            return 0D;
        }

    }
}
