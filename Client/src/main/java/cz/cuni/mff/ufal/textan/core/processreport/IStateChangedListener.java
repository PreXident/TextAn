package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * Listener for {@link ProcessReportPipeline}'s {@link State} change.
 */
public interface IStateChangedListener {

    /** This method gets called when the listener is registered
     * and when pipeline changes its state.
     * @param oldState old state of the pipeline
     * @param newState new state of the pipeline
     */
    void stateChanged(State oldState, State newState);
}
