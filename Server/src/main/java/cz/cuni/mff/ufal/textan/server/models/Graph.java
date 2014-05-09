package cz.cuni.mff.ufal.textan.server.models;

import java.util.List;

/**
 * A service layer representation of the Graph
 * @author Petr Fanta
 */
public class Graph {

    //TODO: change to sets?
    private final List<Object> nodes;
    private final List<Relation> edges;

    /**
     * Instantiates a new Graph.
     *
     * @param nodes the nodes
     * @param edges the edges
     */
    public Graph(List<Object> nodes, List<Relation> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    /**
     * Gets nodes.
     *
     * @return the nodes
     */
    public List<Object> getNodes() {
        return nodes;
    }

    /**
     * Gets edges.
     *
     * @return the edges
     */
    public List<Relation> getEdges() {
        return edges;
    }

    /**
     * Converts an instance to a {@link cz.cuni.mff.ufal.textan.commons.models.Graph}
     *
     * @return the {@link cz.cuni.mff.ufal.textan.commons.models.Graph}
     */
    public cz.cuni.mff.ufal.textan.commons.models.Graph toCommonsGraph() {
        cz.cuni.mff.ufal.textan.commons.models.Graph commonsGraph = new cz.cuni.mff.ufal.textan.commons.models.Graph();

        cz.cuni.mff.ufal.textan.commons.models.Graph.Nodes commonsNodes = new cz.cuni.mff.ufal.textan.commons.models.Graph.Nodes();
        for (Object node : nodes) {
            commonsNodes.getObjects().add(node.toCommonsObject());
        }
        commonsGraph.setNodes(commonsNodes);

        cz.cuni.mff.ufal.textan.commons.models.Graph.Edges commonsEdges = new cz.cuni.mff.ufal.textan.commons.models.Graph.Edges();
        for (Relation edge : edges) {
            commonsEdges.getRelations().add(edge.toCommonsRelation());
        }
        commonsGraph.setEdges(commonsEdges);

        return commonsGraph;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Graph graph = (Graph) o;

        if (edges != null ? !edges.equals(graph.edges) : graph.edges != null) return false;
        if (nodes != null ? !nodes.equals(graph.nodes) : graph.nodes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodes != null ? nodes.hashCode() : 0;
        result = 31 * result + (edges != null ? edges.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Graph{");
        sb.append("nodes=").append(nodes);
        sb.append(", edges=").append(edges);
        sb.append('}');
        return sb.toString();
    }
}
