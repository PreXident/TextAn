/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

/**
 *
 * @author Václav Pernička
 */
public class Edge {
    enum EdgeType {
        LeftToRight, RightToLeft, Neutral
    }
    Node leftNode, rightNode;
    int order;
    
    public Edge(Node leftNode, Node rightNode, int order) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.order = order;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    public EdgeType getOrientation() {
        if (order == 0) return EdgeType.Neutral;
        if (order > 0) return EdgeType.LeftToRight;
        return EdgeType.RightToLeft;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
}
