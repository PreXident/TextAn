package cz.cuni.mff.ufal.textan.core.processreport;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

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
        pipeline.setState(SelectDocumentState.getInstance());
    }

    @Override
    public void selectFileDatasource(final ProcessReportPipeline pipeline) {
        pipeline.setState(SelectFileState.getInstance());
    }

    @Override
    public void selectEmptyDatasource(final ProcessReportPipeline pipeline) {
        pipeline.setState(ReportEditState.getInstance());
    }

    @Override
    public void selectLoadDatasource(final ProcessReportPipeline pipeline, final String path) {
        try (ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(path))) {
            final ProcessReportPipeline loaded =
                    (ProcessReportPipeline) in.readObject();
            pipeline.load(loaded);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StateType getType() {
        return StateType.LOAD;
    }

    @Override
    protected java.lang.Object readResolve() {
        return getInstance();
    }
}
