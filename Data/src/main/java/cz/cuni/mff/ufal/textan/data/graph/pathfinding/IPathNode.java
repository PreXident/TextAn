/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.graph.pathfinding;

import cz.cuni.mff.ufal.textan.data.graph.Edge;
import cz.cuni.mff.ufal.textan.data.graph.Node;

/**
 * @author Vaclav Pernicka
 */
public interface IPathNode {
    Edge getPreviousEdge();

    void setPreviousEdge(Edge previousEdge);

    Node getPreviousNode();

    void setPreviousNode(Node previousNode);
}
