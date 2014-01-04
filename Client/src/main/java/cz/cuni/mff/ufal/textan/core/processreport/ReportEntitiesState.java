package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's entities.
 */
final class ReportEntitiesState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportEntitiesState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportEntitiesState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportEditState.class) {
                if (instance == null) {
                    instance = new ReportEntitiesState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportEntitiesState() { }

    @Override
    public StateType getType() {
        return StateType.EDIT_ENTITIES;
    }
}
