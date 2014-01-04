package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import java.util.Properties;

/**
 * Main class controlling core manipulations with reports.
 * Handles all communicatioin with the server.
 */
public class Client {

    /** Settings of the application. Handle with care, they're shared. */
    final protected Properties settings;

    /**
     * Only constructor.
     * @param settings settings of the application
     */
    public Client(final Properties settings) {
        this.settings = settings;
    }

    /**
     * Creates new pipeline for processing new report.
     * @return new pipeline for processing new report
     */
    public ProcessReportPipeline createNewReportPipeline() {
        return new ProcessReportPipeline(this);
    }
}
