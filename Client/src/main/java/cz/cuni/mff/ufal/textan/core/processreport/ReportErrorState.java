package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.IdNotFoundException;

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
    protected java.lang.Object readResolve() {
        return getInstance();
    }

    @Override
    public void back(final ProcessReportPipeline pipeline) {
        pipeline.incStepsBack();
        pipeline.setState(ReportRelationsState.getInstance());
    }

    @Override
    public void forceSave(final ProcessReportPipeline pipeline)
            throws DocumentChangedException, DocumentAlreadyProcessedException {
        ReportRelationsState.getInstance().saveReport(pipeline, true);
        if (pipeline.result) {
            pipeline.setState(DoneState.getInstance());
        }
    }
}
