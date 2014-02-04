package cz.cuni.mff.ufal.textan.data.tables;

/**
 * @author Václav Pernička
 */


public class RelationTable extends AbstractTable {
    
    private long id;
    private RelationTypeTable relationType;

    // TODO List of entities in this relation
    
    public RelationTable() {
    }

    public RelationTable(RelationTypeTable objectType) {
        this.relationType = objectType;
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
