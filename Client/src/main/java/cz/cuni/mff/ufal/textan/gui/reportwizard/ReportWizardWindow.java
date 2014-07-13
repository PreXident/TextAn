package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import static cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardController.TITLE;
import java.util.Properties;

/**
 * Wizard for handling reports.
 */
public class ReportWizardWindow extends InnerWindow {

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
