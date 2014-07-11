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
    public void back(final ProcessReportPipeline pipeline) {
        pipeline.incStepsBack();
        pipeline.setState(ReportRelationsState.getInstance());
    }

    @Override
    public void forceSave(final ProcessReportPipeline pipeline) {
        try {
            pipeline.result = pipeline.client.saveProcessedDocument(
                    pipeline.ticket,
                    pipeline.getReportText(),
                    pipeline.reportEntities,
                    pipeline.reportRelations,
                    true);
        } catch (IdNotFoundException e) {
            e.printStackTrace();
        }
        if (pipeline.result) {
            pipeline.setState(DoneState.getInstance());
        }
    }
}
