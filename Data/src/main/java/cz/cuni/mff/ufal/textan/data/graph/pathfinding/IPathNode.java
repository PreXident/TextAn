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
public interface IPathNode {
    public Edge getPreviousEdge();

    public void setPreviousEdge(Edge previousEdge);

    public Node getPreviousNode();

    public void setPreviousNode(Node previousNode);
}
