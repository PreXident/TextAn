/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

/**
 *
 * @author Václav Pernička
 */

public class ObjectTypeTable extends AbstractTable {
 
    private long id;

    private String name;

    public ObjectTypeTable() {
        this("");
    }
    
    public ObjectTypeTable(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("ObjectTypeTable(%d, \"%s\")", getId(), this.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ObjectTypeTable) {
            ObjectTypeTable ott = (ObjectTypeTable) o;
            return ott.getId() == this.getId() && ott.getName().equals(this.getName());
        } else
            return false;
    }

    
    
    
 
}
