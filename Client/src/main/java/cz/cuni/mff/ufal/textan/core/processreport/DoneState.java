package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * {@link ProcessReportPipeline}'s done.
 */
public class DoneState extends State {

    /** Holds the singleton's intance. */
    static private volatile DoneState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static DoneState getInstance() {
        if (instance == null) { //double checking
            synchronized (DoneState.class) {
                if (instance == null) {
                    instance = new DoneState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private DoneState() { }

    @Override
    public State.StateType getType() {
        return State.StateType.DONE;
    }
}
