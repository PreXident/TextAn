/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.graph.pathfinding;

import cz.cuni.mff.ufal.textan.data.graph.Edge;
import cz.cuni.mff.ufal.textan.data.graph.Node;

/**
 *
 * @author Vaclav Pernicka
 */
public class PathNode {
    Node node;
    Edge previousEdge;

    public PathNode(Node node, Edge previousEdge) {
        this.node = node;
        this.previousEdge = previousEdge;
    }

    public Node getNode() {
        return node;
    }

    public Edge getPreviousNode() {
        return previousEdge;
    }
    
}
