package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.Relation;
import java.util.Set;

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
    public void setReportRelations(final ProcessReportPipeline pipeline, final Set<Relation> relations) {
        pipeline.reportRelations = relations;
        pipeline.client.saveProcessedDocument(
                pipeline.ticket,
                pipeline.getReportText(),
                pipeline.reportEntities,
                pipeline.reportRelations);
        pipeline.setState(DoneState.getInstance());
    }
}
