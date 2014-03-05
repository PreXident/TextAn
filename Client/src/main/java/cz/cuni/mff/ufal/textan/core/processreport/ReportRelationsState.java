package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.models.Relation;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's relations.
 */
final class ReportRelationsState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportRelationsState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportRelationsState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportRelationsState.class) {
                if (instance == null) {
                    instance = new ReportRelationsState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportRelationsState() { }

    @Override
    public State.StateType getType() {
        return State.StateType.EDIT_RELATIONS;
    }

    @Override
    public void setReportRelations(final ProcessReportPipeline pipeline, final Relation[] relations) {
        pipeline.reportRelations = relations;
        //TODO save document, what ID?
        pipeline.client.getDocumentProcessor().saveProcessedDocument(
                -1, pipeline.reportObjects, pipeline.reportRelations,
                pipeline.ticket, false);
        pipeline.setState(DoneState.getInstance());
    }
}
