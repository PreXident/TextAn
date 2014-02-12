package cz.cuni.mff.ufal.textan.data.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * @author Václav Pernička
 */


public class RelationTable extends AbstractTable {
    
    private long id;
    private RelationTypeTable relationType;
    private List<ObjectTable> objectsInRelation = new ArrayList<>();
    
    public RelationTable() {}

    public RelationTable(RelationTypeTable objectType) {
        this.relationType = objectType;
    }

    public List<ObjectTable> getObjectsInRelation() {
        return objectsInRelation;
    }

    public void setObjectsInRelation(List<ObjectTable> objectsInRelation) {
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
    
    

    
}
