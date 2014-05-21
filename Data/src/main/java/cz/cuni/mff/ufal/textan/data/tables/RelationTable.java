package cz.cuni.mff.ufal.textan.data.tables;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;



/**
 * @author Václav Pernička
 */


public class RelationTable extends AbstractTable {
    public static final String PROPERTY_NAME_RELATION_TYPE_ID = "relationType";
    public static final String PROPERTY_NAME_OCCURRENCES_ID = "occurrences";
    public static final String PROPERTY_NAME_ID = "id";
    
    private long id;

    private RelationTypeTable relationType;
    private Set<InRelationTable> objectsInRelation = new HashSet<>();
    private Set<RelationOccurrenceTable> occurrences = new HashSet<>();
    
    public RelationTable() {}

    public RelationTable(RelationTypeTable objectType) {
        this.relationType = objectType;
    }

    public Set<RelationOccurrenceTable> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<RelationOccurrenceTable> occurrences) {
        this.occurrences = occurrences;
    }
    
    public Set<InRelationTable> getObjectsInRelation() {
        return objectsInRelation;
    }

    public void setObjectsInRelation(Set<InRelationTable> objectsInRelation) {
        this.objectsInRelation = objectsInRelation;
    }
    
    public RelationTypeTable getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationTypeTable relationType) {
        this.relationType = relationType;
    }

    
    
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return String.format("RelationTable(%d, %s)", getId(), getRelationType());
                
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelationTable)) return false;
        RelationTable rt = (RelationTable) o;
        
        if (rt.getId() != this.getId()) return false;
        
        return rt.getRelationType().equals(this.getRelationType());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 97 * hash + Objects.hashCode(this.relationType);
        return hash;
    }
    
    

    
}
