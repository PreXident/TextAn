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
 * @author Václav Pernička
 */
public class AliasTable extends AbstractTable {
    private long id;
    private String alias;
    
    private ObjectTable object;
    private Set<AliasOccurrenceTable> occurrences = new HashSet<>();
    
    
    public AliasTable() {
    }

    public AliasTable(ObjectTable object, String alias) {
        this.object = object;
        this.alias = alias;
    }

    public Set<AliasOccurrenceTable> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<AliasOccurrenceTable> occurrences) {
        this.occurrences = occurrences;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ObjectTable getObject() {
        return object;
    }

    public void setObject(ObjectTable object) {
        this.object = object;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return String.format("AliasTable(%d, \"%s\", %s)", this.getId(), this.getAlias(), this.getObject());
    }   
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AliasTable)) return false;
        AliasTable at = (AliasTable)o;
        if (at.getId() != this.getId()) return false;
        if (!at.getAlias().equals(this.getAlias())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 97 * hash + Objects.hashCode(this.alias);
        return hash;
    }
    
    
    
}
