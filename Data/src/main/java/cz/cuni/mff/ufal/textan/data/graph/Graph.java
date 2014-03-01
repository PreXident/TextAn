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

    /**
     * Creates a graph with an object in the "center"
     * You can specify how deep it will search more objects
     * 
     * @param objectId Id of an object in the center of the graph
     * @param depth How deep should it search. 
     *              0 = returns only one node (with the given ID)
     *              1 = returns neighbors of this node (nodes in a relationship)
     * @return 
     */
    public static Graph getGraphFromObject(long objectId, int depth) {
        Graph result = new Graph();
        
        return result;
    }
    
    private Graph() {
    }

    private Graph(Collection<? extends Node> nodes, Collection<? extends Edge> edges) {
        this.nodes.addAll(nodes);
        this.edges.addAll(edges);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }
    
}
