/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Václav Pernička
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

    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Graph{" + "nodes=" + nodes + ", edges=" + edges + '}';
    }
    
    static Graph merge(final Graph graph1, final Graph graph2) {
        Graph result = new Graph(graph1.nodes, graph1.edges);
        
        result.nodes.addAll(graph2.nodes);
        result.edges.addAll(graph2.edges);

        return result;
    }

    boolean mergeIntoThis(final Graph graph) {
        graph.nodes.removeAll(nodes);
        
        boolean result = !graph.nodes.isEmpty();
        
        nodes.addAll(graph.nodes);
        edges.addAll(graph.edges);

        return result;
    }

    
}
