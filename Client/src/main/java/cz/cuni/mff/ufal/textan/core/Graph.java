package cz.cuni.mff.ufal.textan.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Client side representation of {@link cz.cuni.mff.ufal.textan.commons.models.Graph}.
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
    public Graph(final cz.cuni.mff.ufal.textan.commons_old.models.Graph graph) {
        graph.getNodes().stream().forEach(obj -> {
            nodes.put(obj.getId(), new Object(obj));
        });
        graph.getEdges().stream().forEach(relation -> {
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
    public cz.cuni.mff.ufal.textan.commons_old.models.Graph toGraph() {
        return new cz.cuni.mff.ufal.textan.commons_old.models.Graph(
                nodes.values().stream()
                        .map((Object obj) -> obj.toObject())
                        .collect(Collectors.toCollection(ArrayList::new)),
                edges.stream()
                        .map((Relation rel) -> rel.toRelation())
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }
}
