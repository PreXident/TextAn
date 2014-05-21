package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import java.util.Properties;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.Region;

/**
 * Wizard for handling reports.
 */
public class ReportWizardWindow extends InnerWindow {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Report Wizard";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "report.wizard";

    /** If prefWidth is bound because of maximalization, it is to this property. */
    protected ReadOnlyDoubleProperty boundWidth;

    /**
     * Only constructor.
     * @param settings properties with settings
     */
    public ReportWizardWindow(final Properties settings) {
        super(TITLE, PROPERTY_ID, settings);
    }

    @Override
    protected void bindPrefSize(final Region region) {
        super.bindPrefSize(region);
        boundWidth = region.widthProperty();
    }

    @Override
    public void close() {
        super.close();
    }
}
