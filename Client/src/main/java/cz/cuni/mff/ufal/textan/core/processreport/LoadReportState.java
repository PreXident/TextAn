package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * {@link ProcessReportPipeline}'s {@link State} for selecting report source.
 */
final class LoadReportState extends State {

    /** Holds the singleton's intance. */
    static private volatile LoadReportState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static LoadReportState getInstance() {
        if (instance == null) { //double checking
            synchronized (LoadReportState.class) {
                if (instance == null) {
                    instance = new LoadReportState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private LoadReportState() { }

    @Override
    public void selectDatabaseDatasource(final ProcessReportPipeline pipeline) {
        //TODO database datasource
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void selectFileDatasource(final ProcessReportPipeline pipeline) {
        //TODO file datasource
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void selectEmptyDatasource(final ProcessReportPipeline pipeline) {
        pipeline.setState(ReportEditState.getInstance());
    }

    @Override
    public void selectLoadDatasource(final ProcessReportPipeline pipeline) {
        //TODO unfinished report datasource
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public StateType getType() {
        return StateType.LOAD;
    }
}
