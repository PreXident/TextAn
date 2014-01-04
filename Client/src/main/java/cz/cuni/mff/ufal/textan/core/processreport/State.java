package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * Abstract ancestor for states of {@link ProcessReportPipeline}.
 * The pipeline delegates its methods to its state passing this reference.
 * States are intented to be stateless singletons and should override methods
 * they are responsible for because default implementations just throw
 * {@link IllegalStateException}.
 */
public abstract class State {

    /**
     * Only constructor.
     */
    protected State() { }

    /**
     * Returns state's type.
     * @return state's type
     */
    public abstract StateType getType();

    /**
     * Selects database as a source of the new report.
     * Available in {@link State.StateType#LOAD} state.
     * @param pipeline pipeline delegating the request
     */
    public void selectDatabaseDatasource(final ProcessReportPipeline pipeline) {
        throw new IllegalStateException("Cannot select report data source when in state " + getType());
    }

    /**
     * Selects empty report as a source of the new report.
     * Available in {@link State.StateType#LOAD} state.
     * @param pipeline pipeline delegating the request
     */
    public void selectEmptyDatasource(final ProcessReportPipeline pipeline) {
        throw new IllegalStateException("Cannot select report data source when in state " + getType());
    }

    /**
     * Selects file as a source of the new report.
     * Available in {@link State.StateType#LOAD} state.
     * @param pipeline pipeline delegating the request
     */
    public void selectFileDatasource(final ProcessReportPipeline pipeline) {
        throw new IllegalStateException("Cannot select report data source when in state " + getType());
    }

    /**
     * Selects unfinished report as a source of the new report.
     * Available in {@link State.StateType#LOAD} state.
     * @param pipeline pipeline delegating the request
     */
    public void selectLoadDatasource(final ProcessReportPipeline pipeline) {
        throw new IllegalStateException("Cannot select report data source when in state " + getType());
    }

    /**
     * Sets the report's text.
     * @param pipeline pipeline delegating the request
     * @param report new report's text
     */
    public void setReport(final ProcessReportPipeline pipeline, final String report) {
        throw new IllegalStateException("Cannot set report's text when in state " + getType());
    }

    /** Possible states. */
    public enum StateType {
        /** Selecting report source. Implemented by {@link LoadReportState}. */
        LOAD,
        /** Editing the report. Implemented by {@link ReportEditState}. */
        EDIT_REPORT,
        /** Editing the entities. Implemented by {@link ReportEntitiesState}. */
        EDIT_ENTITIES
    }
}
