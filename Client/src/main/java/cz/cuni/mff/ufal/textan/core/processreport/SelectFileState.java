package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.processreport.load.IImporter;

/**
 * {@link ProcessReportPipeline}'s {@link State} for selecting file as source.
 */
final public class SelectFileState extends State {

    /** Holds the singleton's intance. */
    static private volatile SelectFileState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static SelectFileState getInstance() {
        if (instance == null) { //double checking
            synchronized (SelectFileState.class) {
                if (instance == null) {
                    instance = new SelectFileState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private SelectFileState() { }

    @Override
    public String extractText(final ProcessReportPipeline pipeline,
            final byte[] data, final IImporter importer) {
        return importer.extractText(data);
    }

    @Override
    public void setReportText(final ProcessReportPipeline pipeline, final String report) {
        pipeline.reportText = report;
        pipeline.setState(ReportEditState.getInstance());
    }

    @Override
    public State.StateType getType() {
        return State.StateType.SELECT_FILE;
    }

    @Override
    protected java.lang.Object readResolve() {
        return getInstance();
    }
}
