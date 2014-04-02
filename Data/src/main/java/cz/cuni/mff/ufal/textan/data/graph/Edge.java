/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

import java.util.Objects;

/**
 *
 * @author Václav Pernička
 */
public class Edge {
    enum EdgeType {
        LeftToRight, RightToLeft, Neutral
    }
    ObjectNode leftNode;
    Node rightNode;
    int order;

    public Edge() {
    }
    
    
    public Edge(ObjectNode leftNode, Node rightNode, int order) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.order = order;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.rightNode);
        hash = 47 * hash + this.order;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        if (this.order != other.order) {
            return false;
        }
        
        if (Objects.equals(this.leftNode, other.leftNode) && Objects.equals(this.rightNode, other.rightNode)) {
            return this.order == other.order;
        }
        if (Objects.equals(this.leftNode, other.rightNode) && Objects.equals(this.rightNode, other.leftNode)) {
            return this.order == -other.order;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge{" + "leftNode=" + leftNode + ", rightNode=" + rightNode + ", order=" + order + '}';
    }
    
    

    public ObjectNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(ObjectNode leftNode) {
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
