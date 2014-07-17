package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;

/**
 * Common ancestor for graphers.
 */
public abstract class AbstractGrapher implements IGrapher {

    /** Distance from root to fetch. */
    protected int distance = -1;

    /** Root central object id. */
    protected long rootId = -1;

    /** Client connecting to webservices. */
    final protected Client client;

    /** Wrapped graph. */
    protected Graph graph;

    /**
     * Only constructor.
     * @param client client for connecting to webservices
     */
    public AbstractGrapher(Client client) {
        this.client = client;
    }

    @Override
    public Graph getGraph() throws IdNotFoundException {
        if (graph == null) {
            graph = fetchGraph();
        }
        return graph;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean isReady() {
        return rootId > 0 && distance >= 0;
    }

    @Override
    public long getRootId() {
        return rootId;
    }

    @Override
    public void setRootId(long rootId) {
        this.rootId = rootId;
    }

    /**
     * Calls relevant methods of client to get the graph.
     * @return graph to be wrapped by this grapher
     * @throws IdNotFoundException if id error occurs
     */
    protected abstract Graph fetchGraph() throws IdNotFoundException;
}
