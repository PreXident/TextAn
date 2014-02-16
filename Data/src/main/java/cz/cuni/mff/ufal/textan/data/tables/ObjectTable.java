package cz.cuni.mff.ufal.textan.data.tables;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Václav Pernička
 */


public class ObjectTable extends AbstractTable {
    
    private long id;
    private String data;

    private ObjectTypeTable objectType;
    private Set<AliasTable> aliases = new HashSet<>();
    private Set<RelationTable> relations = new HashSet<>();
    
    public ObjectTable() {
    }

    public ObjectTable(String data, ObjectTypeTable objectType) {
        this.data = data;
        this.objectType = objectType;
    }

    public Set<RelationTable> getRelations() {
        return relations;
    }

    public void setRelations(Set<RelationTable> relations) {
        this.relations = relations;
    }

    public Set<AliasTable> getAliases() {
        return aliases;
    }

    public void setAliases(Set<AliasTable> aliases) {
        this.aliases = aliases;
    }

    public ObjectTypeTable getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectTypeTable objectType) {
        this.objectType = objectType;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("ObjectTable(%d, \"%s\". %s)", getId(), getData(), getObjectType());
                
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjectTable)) return false;
        ObjectTable ot = (ObjectTable) o;
        return ot.getId() == this.getId() && ot.getData().equals(this.getData()) && ot.getObjectType().equals(this.getObjectType());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 79 * hash + Objects.hashCode(this.data);
        hash = 79 * hash + Objects.hashCode(this.objectType);
        return hash;
    }




    
    

    
}
