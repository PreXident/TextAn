/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.graph.pathfinding;

import cz.cuni.mff.ufal.textan.data.graph.Edge;
import cz.cuni.mff.ufal.textan.data.graph.Node;
import cz.cuni.mff.ufal.textan.data.graph.ObjectNode;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

/**
 * @author Vaclav Pernicka
 */
public class ObjectPathNode extends ObjectNode implements IPathNode {

    Edge previousEdge;
    Node previousNode;

    public ObjectPathNode(Edge previousEdge, Node previousNode, ObjectTable obj) {
        super(obj);
        this.previousEdge = previousEdge;
        this.previousNode = previousNode;
    }

    public ObjectPathNode(Edge previousEdge, Node previousNode, long id, String name) {
        super(id, name);
        this.previousEdge = previousEdge;
        this.previousNode = previousNode;
    }

    public ObjectPathNode(ObjectTable obj) {
        super(obj);
    }

    public ObjectPathNode(long id, String name) {
        super(id, name);
    }


    @Override
    public Edge getPreviousEdge() {
        return previousEdge;
    }

    @Override
    public void setPreviousEdge(Edge previousEdge) {
        this.previousEdge = previousEdge;
    }

    @Override
    public Node getPreviousNode() {
        return previousNode;
    }

    @Override
    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }


}
