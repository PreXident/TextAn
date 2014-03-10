package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.commons_old.models.Graph;
import cz.cuni.mff.ufal.textan.commons_old.models.Object;
import cz.cuni.mff.ufal.textan.core.Client;

/**
 * Provides information about graphs.
 */
public class Grapher {

    protected int distance = -1;

    protected int rootId = -1;

    final protected Client client;

    public Grapher(Client client) {
        this.client = client;
    }

    public Object[] getObjects() {
        return client.getDataProvider().getObjects();
    }

    public Graph getGraph() {
        return client.getDataProvider().getGraphByID(rootId, distance);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }
}
