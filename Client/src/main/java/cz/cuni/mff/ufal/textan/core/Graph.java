package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.Graph.Edges;
import cz.cuni.mff.ufal.textan.commons.models.Graph.Nodes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Client side representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.Graph}.
 */
public class Graph {

    /** Graph nodes. */
    private final Map<Integer, Object> nodes = new HashMap<>();

    /** Graph edges. */
    private final Set<Relation> edges = new HashSet<>();

    /**
     * Only constructor.
     * @param graph graph blue print
     */
    public Graph(final cz.cuni.mff.ufal.textan.commons.models.Graph graph) {
        graph.getNodes().getObjects().stream().forEach(obj -> {
            nodes.put(obj.getId(), new Object(obj));
        });
        graph.getEdges().getRelations().stream().forEach(relation -> {
            edges.add(new Relation(relation, nodes));
        });
    }

    /**
     * Returns graph edges.
     * @return graph edges
     */
    public Set<Relation> getEdges() {
        return edges;
    }

    /**
     * Returns graph nodes.
     * @return graph nodes
     */
    public Map<Integer, Object> getNodes() {
        return nodes;
    }

    /**
     * Creates new commons Graph.
     * @return new commons Graph
     */
    public cz.cuni.mff.ufal.textan.commons.models.Graph toGraph() {
        final cz.cuni.mff.ufal.textan.commons.models.Graph result =
                new cz.cuni.mff.ufal.textan.commons.models.Graph();
        final Nodes n = new Nodes();
        n.getObjects().addAll(
                nodes.values().stream()
                        .map(Object::toObject)
                        .collect(Collectors.toList())
        );
        result.setNodes(n);
        final Edges e = new Edges();
        e.getRelations().addAll(
                edges.stream()
                        .map(Relation::toRelation)
                        .collect(Collectors.toList())
        );
        result.setEdges(e);
        return result;
    }
}
