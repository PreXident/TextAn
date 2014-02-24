package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.models.Object;

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
    public void setReportObjects(final ProcessReportPipeline pipeline, final Object[] objects) {
        pipeline.reportObjects = objects;
        pipeline.setState(ReportRelationsState.getInstance());
    }
}
