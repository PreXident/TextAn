package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.NonRootObjectException;
import cz.cuni.mff.ufal.textan.core.Object;

/**
 * Provides information about graphs centered around objects.
 */
public class PathGrapher extends ObjectGrapher {

    /** Id of the target object. */
    protected long targetId = -1;

    /**
     * Only constructor.
     * @param client client for connecting to webservices
     */
    public PathGrapher(final Client client) {
        super(client);
    }

    @Override
    public boolean isReady() {
        return rootId > 0 && distance >= 0 && targetId > 0;
    }

    /**
     * Returns target object id.
     * @return target object id
     */
    public long getTargetId() {
        return targetId;
    }

    /**
     * Sets target object id.
     * @param targetId new target object id
     */
    public void setTargetId(final long targetId) {
        this.targetId = targetId;
    }

    @Override
    public String getTitle() {
        final Object center = graph.getNodes().get(rootId);
        final Object target = graph.getNodes().get(targetId);
        return center.toString() + "->" + target.toString();
    }

    @Override
    protected Graph fetchGraph() throws IdNotFoundException {
        while (true) {
            try {
                return client.getPathGraph(rootId, targetId);
            } catch (NonRootObjectException e) {
                if (e.getObjectId() == rootId) {
                    rootId = e.getNewRootId();
                } else if (e.getObjectId() == targetId) {
                    targetId = e.getNewRootId();
                } else {
                    throw new RuntimeException("This should have never happened!", e);
                }
            }
        }
    }
}
