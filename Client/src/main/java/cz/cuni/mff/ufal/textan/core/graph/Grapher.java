package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import java.util.Set;

/**
 * Provides information about graphs.
 */
public class Grapher {

    protected int distance = -1;

    protected long rootId = -1;

    final protected Client client;

    public Grapher(Client client) {
        this.client = client;
    }

    public Set<Object> getObjects() {
        return client.getObjectsSet();
    }

    public Graph getGraph() throws IdNotFoundException {
        return client.getGraph(rootId, distance);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setRootId(long rootId) {
        this.rootId = rootId;
    }
}
