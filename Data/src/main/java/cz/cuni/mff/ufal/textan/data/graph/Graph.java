package cz.cuni.mff.ufal.textan.data.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a graph created from database. Nodes represent objects
 * and relations. Edges represents belonging of an object to a relation.
 *
 * @author Vaclav Pernicka
 */
public class Graph {
    Set<Node> nodes = new HashSet<>();
    Set<Edge> edges = new HashSet<>();

    Graph() {
    }

    Graph(Collection<? extends Node> nodes, Collection<? extends Edge> edges) {
        this.nodes.addAll(nodes);
        this.edges.addAll(edges);
    }

    /**
     *
     * @return nodes of the graph
     */
    public Set<Node> getNodes() {
        return nodes;
    }

    /**
     *
     * @return edges of the graph
     */
    public Set<Edge> getEdges() {
        return edges;
    }

    Graph add(Node node) {
        nodes.add(node);
        return this;
    }

    Graph add(Edge edge) {
        edges.add(edge);
        return this;
    }


    @Override
    public String toString() {
        return "Graph{" + "nodes=" + nodes + ", edges=" + edges + '}';
    }

    /**
     * This method performs union of two graphs
     *
     * @param graph1
     * @param graph2
     * @return
     */
    static Graph merge(final Graph graph1, final Graph graph2) {
        Graph result = new Graph(graph1.nodes, graph1.edges);

        result.nodes.addAll(graph2.nodes);
        result.edges.addAll(graph2.edges);

        return result;
    }

    /**
     * This method performs intersection of two graphs.
     *
     * @param graph1
     * @param graph2
     * @return
     */
    static Graph intersection(final Graph graph1, final Graph graph2) {
        Graph result = new Graph(graph1.nodes, graph1.edges);

        result.nodes.retainAll(graph2.nodes);
        result.edges.retainAll(graph2.edges);

        return result;
    }

    /**
     * Performs union to this graph.
     *
     * @param graph
     */
    void unionIntoThis(final Graph graph) {
        nodes.addAll(graph.nodes);
        edges.addAll(graph.edges);
    }
    
    /**
     * performs intersection to this graph.
     *
     * @param graph
     */
    void intersectIntoThis(final Graph graph) {
        nodes.retainAll(graph.nodes);
        edges.retainAll(graph.edges);
    }
    /**
     * performs subtraction to this graph.
     * 
     * @param graph 
     */
    void subractIntoThis(final Graph graph) {
        nodes.removeAll(graph.nodes);
        edges.removeAll(graph.edges);
    }    
    
}
