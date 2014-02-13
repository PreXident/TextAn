package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.WindowController;

/**
 * Common ancestor of controllers in this package.
 */
public abstract class ReportWizardController extends WindowController {

    /** Pipeline controlling the report processing. */
    protected ProcessReportPipeline pipeline;

    public void setPipeline(final ProcessReportPipeline pipeline) {
        this.pipeline = pipeline;
    }
}
