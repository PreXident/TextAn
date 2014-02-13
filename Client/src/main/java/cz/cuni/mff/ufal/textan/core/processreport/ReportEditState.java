package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's text.
 */
final class ReportEditState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportEditState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportEditState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportEditState.class) {
                if (instance == null) {
                    instance = new ReportEditState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportEditState() { }

    @Override
    public void setReport(final ProcessReportPipeline pipeline, final String report) {
        pipeline.reportText = report;
        pipeline.setState(ReportEntitiesState.getInstance());
    }

    @Override
    public StateType getType() {
        return StateType.EDIT_REPORT;
    }
}
