package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import java.util.Properties;

/**
 * Wizard for handling reports.
 */
public class ReportWizardWindow extends InnerWindow {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Report Wizard";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "report.wizard";

    /**
     * Only constructor.
     * @param settings properties with settings
     */
    public ReportWizardWindow(final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
    }

    @Override
    public void close() {
        super.close();
    }
}
