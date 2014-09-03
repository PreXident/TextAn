/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

import cz.cuni.mff.ufal.textan.data.tables.RelationTable;

/**
 * Represents a node that was created from a relation.
 * 
 * @see Node
 * @see Graph
 * 
 * @author Vaclav Pernicka
 */
public class RelationNode extends Node {
    long relationTypeId;
    
    RelationNode(RelationTable rel) {
        this(rel.getId(), rel.getRelationType().getId(), rel.getRelationType().getName());
    }
    
    RelationNode(long id, long relationTypeId, String name) {
        super(id, name);
        this.relationTypeId = relationTypeId;
    }

    /**
     *
     * @return id of the relation type
     */
    public long getRelationTypeId() {
        return relationTypeId;
    }

    void setRelationTypeId(long relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RelationNode)) return false;
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }    

    @Override
    public String toString() {
        return "ObjectNode{" + "id=" + id + ", name=\"" + name + "\"}";
    }

    
}
