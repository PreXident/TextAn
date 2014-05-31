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

public class RelationTypeTable extends AbstractTable {
    public static final String PROPERTY_NAME_ID = "id";
 
    private long id;
    private String name;
    
    private Set<RelationTable> relationsOfThisType = new HashSet<>();

    
    public RelationTypeTable() {
        this("");
    }
    
    public RelationTypeTable(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }

    public Set<RelationTable> getRelationsOfThisType() {
        return relationsOfThisType;
    }

    public void setRelationsOfThisType(Set<RelationTable> relationsOfThisType) {
        this.relationsOfThisType = relationsOfThisType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("RelationTypeTable(%d, \"%s\")", getId(), this.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RelationTypeTable) {
            RelationTypeTable ott = (RelationTypeTable) o;
            return ott.getId() == this.getId() && ott.getName().equals(this.getName());
        } else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    
    
    
 
}
