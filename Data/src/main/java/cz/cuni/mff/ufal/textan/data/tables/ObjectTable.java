package cz.cuni.mff.ufal.textan.data.tables;

/**
 * @author Václav Pernička
 */


public class ObjectTable extends AbstractTable {
    
    private long id;
    private String data;
    private ObjectTypeTable objectType;

    // TODO list of relations this object is in
    
    
    public ObjectTable() {
    }

    public ObjectTable(String data, ObjectTypeTable objectType) {
        this.data = data;
        this.objectType = objectType;
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
    
    

    
}
