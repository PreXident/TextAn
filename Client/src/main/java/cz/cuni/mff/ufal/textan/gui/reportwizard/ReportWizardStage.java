package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.OuterStage;
import java.util.Properties;

/**
 * Class for displaying Report Wizard in independent window.
 */
public class ReportWizardStage extends OuterStage {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Report Wizard";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "report.wizard";

    /**
     * Only constructor.
     * @param settings properties with settings
     */
    public ReportWizardStage(final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
    }

    @Override
    public void close() {
        super.close();
    }
}
