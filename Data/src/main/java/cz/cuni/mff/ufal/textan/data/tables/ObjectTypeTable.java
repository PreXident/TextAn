/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Vaclav Pernicka
 */

public class ObjectTypeTable extends AbstractTable {
    public static final String PROPERTY_NAME_ID = "id";

    private long id;
    private String name;

    private Set<ObjectTable> objectsOfThisType = new HashSet<>();

    public ObjectTypeTable() {
        this("");
    }
    
    public ObjectTypeTable(String name) {
        this.name = name;
    }

    public Set<ObjectTable> getObjectsOfThisType() {
        return objectsOfThisType;
    }

    public void setObjectsOfThisType(Set<ObjectTable> objectsOfThisType) {
        this.objectsOfThisType = objectsOfThisType;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

 

    
    
    
 
}
