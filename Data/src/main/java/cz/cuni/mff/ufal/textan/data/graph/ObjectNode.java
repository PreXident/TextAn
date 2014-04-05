/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

/**
 *
 * @author Václav Pernička
 */
public class ObjectNode extends Node{

    public ObjectNode(ObjectTable obj) {
        this(obj.getId(), obj.getData());
    }

    /**
     *  constructor
     * 
     * @param id id of object
     * @param name name of the object
     */
    public ObjectNode(long id, String name) {
        super(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectNode)) return false;
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
