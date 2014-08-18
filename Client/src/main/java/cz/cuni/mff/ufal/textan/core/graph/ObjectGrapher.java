package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.NonRootObjectException;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        while (true) {
            try {
                return client.getObjectGraph(rootId, distance);
            } catch (NonRootObjectException e) {
                rootId = e.getNewRootId();
            }
        }
    }

    @Override
    protected Graph filterGraph() {
        //is filtering needed?
        if (ignoredObjectTypes.isEmpty() && ignoredRelationTypes.isEmpty()) {
            return graph;
        }
        final Map<Long, Object> nodes = graph.getNodes();
        final Object root = nodes.get(rootId);
        //if the root is filtered, it's easy
        if (root == null || ignoredObjectTypes.contains(root.getType())) {
            return new Graph();
        } else {
            return filterGraph(root);
        }
    }
}
