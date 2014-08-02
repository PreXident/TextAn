package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import java.util.function.Predicate;

/**
 * Provides information about graphs centered around objects.
 */
public class ObjectGrapher extends AbstractGrapher {

    /**
     * Only constructor.
     * @param client client for connecting to webservices
     */
    public ObjectGrapher(Client client) {
        super(client);
    }

    @Override
    public Predicate<java.lang.Object> getCenterer() {
        return object -> {
            if (object instanceof Object) {
                return ((Object) object).getId() == rootId;
            } else {
                return false;
            }
        };
    }

    @Override
    public String getTitle() {
        final Object center = graph.getNodes().get(rootId);
        return center.toString();
    }

    @Override
    protected Graph fetchGraph() throws IdNotFoundException {
        return client.getObjectGraph(rootId, distance);
    }
}
