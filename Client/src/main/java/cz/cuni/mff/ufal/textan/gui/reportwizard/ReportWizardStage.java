package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.OuterStage;
import static cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardController.PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardController.TITLE;
import java.util.Properties;

/**
 * Class for displaying Report Wizard in independent window.
 */
public class ReportWizardStage extends OuterStage {

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
