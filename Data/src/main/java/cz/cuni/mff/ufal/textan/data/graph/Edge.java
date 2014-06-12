/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

import java.util.Objects;

/**
 *
 * @author Vaclav Pernicka
 */
public class Edge {

    /**
     * Type of the edge orientation
     */
    public enum EdgeType {

        /**
         *  Edge is oriented from left node to the right one
         */
        LeftToRight,

        /**
         *  Edge is oriented from right node to the left one
         */
        RightToLeft,

        /**
         *  Edge is not oriented
         */
        Neutral
    }

    ObjectNode leftNode;
    Node rightNode;
    int order;

    Edge() {
    }

    Edge(ObjectNode leftNode, Node rightNode, int order) {
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

    /**
     *
     * @return left node of the edge
     */
    public ObjectNode getLeftNode() {
        return leftNode;
    }

    /**
     *
     * @param leftNode left node of the edge
     */
    void setLeftNode(ObjectNode leftNode) {
        this.leftNode = leftNode;
    }

    /**
     *
     * @return right node of the edge
     */
    public Node getRightNode() {
        return rightNode;
    }

    /**
     *
     * @param rightNode right node of the edge
     */
    void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    /**
     *
     * @return 
     * 
     * @see EdgeType
     */
    public EdgeType getOrientation() {
        if (order == 0) return EdgeType.Neutral;
        if (order > 0) return EdgeType.LeftToRight;
        return EdgeType.RightToLeft;
    }

    void setOrder(int order) {
        this.order = order;
    }

    /**
     * 
     * @return order in isinrelation table which describes the orientation of the edge
     */
    public int getOrder() {
        return order;
    }
    
}
