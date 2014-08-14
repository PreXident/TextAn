package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Provides information about graphs centered around relations.
 */
public class RelationGrapher extends AbstractGrapher {

    /**
     * Only constructor.
     * @param client client for connecting to webservices
     */
    public RelationGrapher(Client client) {
        super(client);
    }

    @Override
    public Predicate<java.lang.Object> getCenterer() {
        return object -> {
            if (object instanceof Relation) {
                return ((Relation) object).getId() == rootId;
            } else {
                return false;
            }
        };
    }

    @Override
    public String getTitle() {
        final Optional<Relation> center = graph.getEdges().stream()
                .filter(rel -> rel.getId() == rootId)
                .findFirst();
        if (center.isPresent()) {
            final Relation rel = center.get();
            return rel.toString() + ": " + rel.getAnchorString();
        } else {
            return String.valueOf(rootId);
        }
    }

    @Override
    protected Graph fetchGraph() throws IdNotFoundException {
        return client.getRelationGraph(rootId, distance);
    }

    @Override
    protected Graph filterGraph() {
        //is filtering needed?
        if (ignoredObjectTypes.isEmpty() && ignoredRelationTypes.isEmpty()) {
            return graph;
        }
        final Map<Long, Object> nodes = graph.getNodes();
        final Set<Relation> edges = graph.getEdges();
        final Relation root = edges.stream()
                .filter(e -> e.getId() == rootId).findFirst().orElse(null);
        //if the root is filtered, it's easy
        if (root == null || ignoredRelationTypes.contains(root.getType())) {
            return new Graph();
        }
        Object rootObject = null; //any object will do
        for (Triple<Integer, String, Object> triple : root.getObjects()) {
            final Object obj = triple.getThird();
            if (!ignoredObjectTypes.contains(obj.getType())) {
                rootObject = obj;
                break;
            }
        }
        if (rootObject != null) {
            return filterGraph(rootObject);
        } else {
            return new Graph();
        }
    }
}
