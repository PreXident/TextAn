package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.Graph.Edges;
import cz.cuni.mff.ufal.textan.commons.models.Graph.Nodes;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Client side representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.Graph}.
 */
public class Graph {

    /** Graph nodes. */
    private final Map<Long, Object> nodes = new HashMap<>();

    /** Graph edges. */
    private final Set<Relation> edges = new HashSet<>();

    /**
     * Constructs emtpy graph.
     */
    public Graph() {
        //nothing
    }

    /**
     * Constructs graph from commons blue print
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
     * Constructs graph from map of nodes and set of edges.
     * @param nodes graph nodes
     * @param edges graph edges
     */
    public Graph(final Map<Long, Object> nodes, final Collection<Relation> edges) {
        this.nodes.putAll(nodes);
        this.edges.addAll(edges);
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
    public Map<Long, Object> getNodes() {
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
        final List<cz.cuni.mff.ufal.textan.commons.models.Object> objects =
                n.getObjects();
        nodes.values().stream()
                .map(Object::toObject)
                .forEach(objects::add);
        result.setNodes(n);
        final Edges e = new Edges();
        final List<cz.cuni.mff.ufal.textan.commons.models.Relation> relations =
                e.getRelations();
        edges.stream()
                .map(Relation::toRelation)
                .forEach(relations::add);
        result.setEdges(e);
        return result;
    }
}
