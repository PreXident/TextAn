/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.graph.pathfinding;

import cz.cuni.mff.ufal.textan.data.graph.Edge;
import cz.cuni.mff.ufal.textan.data.graph.Node;
import cz.cuni.mff.ufal.textan.data.graph.RelationNode;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;

/**
 * @author Vaclav Pernicka
 */
public class RelationPathNode extends RelationNode implements IPathNode {
    private Edge previousEdge;
    private Node previousNode;

    public RelationPathNode(Edge previousEdge, Node previousNode, RelationTable rel) {
        super(rel);
        this.previousEdge = previousEdge;
        this.previousNode = previousNode;
    }

    public RelationPathNode(Edge previousEdge, Node previousNode, long id, long relationTypeId, String name) {
        super(id, relationTypeId, name);
        this.previousEdge = previousEdge;
        this.previousNode = previousNode;
    }

    public RelationPathNode(RelationTable rel) {
        super(rel);
    }

    public RelationPathNode(long id, long relationTypeId, String name) {
        super(id, relationTypeId, name);
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
