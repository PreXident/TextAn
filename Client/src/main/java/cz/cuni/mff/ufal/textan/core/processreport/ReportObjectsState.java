package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.Entity;
import java.util.List;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's objects.
 */
final class ReportObjectsState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportObjectsState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportObjectsState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportObjectsState.class) {
                if (instance == null) {
                    instance = new ReportObjectsState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportObjectsState() { }

    @Override
    public State.StateType getType() {
        return State.StateType.EDIT_OBJECTS;
    }

    @Override
    public void back(final ProcessReportPipeline pipeline) {
        pipeline.setState(ReportEntitiesState.getInstance());
    }

    @Override
    public void setReportObjects(final ProcessReportPipeline pipeline, final List<Entity> entities) {
        pipeline.reportEntities = entities;
        pipeline.setState(ReportRelationsState.getInstance());
    }
}
