package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's relations.
 */
final class ReportErrorState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportErrorState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportErrorState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportErrorState.class) {
                if (instance == null) {
                    instance = new ReportErrorState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportErrorState() { }

    @Override
    public State.StateType getType() {
        return State.StateType.ERROR;
    }

    @Override
    public void back(final ProcessReportPipeline pipeline) {
        pipeline.incStepsBack();
        pipeline.setState(ReportRelationsState.getInstance());
    }
}
