package cz.cuni.mff.ufal.textan.data.tables;

import javax.persistence.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Object (entity) itself
 * @author Vaclav Pernicka
 */
@Entity
@Table(name = "Object")
public class ObjectTable extends AbstractTable {

    public static final String PROPERTY_NAME_ID = "id";
    public static final String PROPERTY_NAME_OBJECT_TYPE_ID = "objectType";
    public static final String PROPERTY_NAME_ALIASES_ID = "aliases";

    
    private long id;
    private String data;

    private ObjectTypeTable objectType;
    private Set<AliasTable> aliases = new HashSet<>();
    private Set<InRelationTable> relations = new HashSet<>();
    
    private JoinedObjectsTable newObject;
    private Set<JoinedObjectsTable> oldObjects1;
    private Set<JoinedObjectsTable> oldObjects2;
    
    public ObjectTable() {
    }

    public ObjectTable(String data, ObjectTypeTable objectType) {
        this.data = data;
        this.objectType = objectType;
    }

    /**
     * <b>Changes in returned set do not propagate into database!</b>
     * @return 
     */
    public Set<JoinedObjectsTable> getJoinObjectsThisWasJoinedTo() {
        Set<JoinedObjectsTable> result = new HashSet<>(getOldObjects1());
        result.addAll(getOldObjects2());
        return result;
    }
    
    /**
     * <b>Changes in returned set do not propagate into database!</b>
     * @return All objects this object WAS composed from
     */
    public Set<ObjectTable> getObjectsThisWasJoinedFrom() {
        Set<ObjectTable> result = new HashSet<>();
        if (getNewObject() == null) return result;
        
        result.add(getNewObject().getOldObject1());
        result.add(getNewObject().getOldObject2());
        
        result.addAll(getNewObject().getOldObject1().getObjectsThisWasJoinedFrom());
        result.addAll(getNewObject().getOldObject2().getObjectsThisWasJoinedFrom());
        return result;
    }

     /**
     * <b>Changes in returned set do not propagate into database!</b>
     * @return All objects this object IS composed from
     */
    public Set<ObjectTable> getObjectsThisIsJoinedFrom() {
        Set<ObjectTable> result = new HashSet<>();
        if (getNewObject() == null) return result;
        
        if (getNewObject().getTo() == null || getNewObject().getTo().after(Calendar.getInstance().getTime()))
        {
            result.add(getNewObject().getOldObject1());
            result.add(getNewObject().getOldObject2());
            result.addAll(getNewObject().getOldObject1().getObjectsThisWasJoinedFrom());
            result.addAll(getNewObject().getOldObject2().getObjectsThisWasJoinedFrom());
        }
        return result;
    }

    @Id
    @GeneratedValue
    @Column(name = "id_object", nullable = false, unique = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="data", nullable = true)
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @ManyToOne //TODO
    public ObjectTypeTable getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectTypeTable objectType) {
        this.objectType = objectType;
    }

    @OneToMany //TODO
    public Set<AliasTable> getAliases() {
        return aliases;
    }

    public void setAliases(Set<AliasTable> aliases) {
        this.aliases = aliases;
    }

    @OneToMany //TODO
    public Set<InRelationTable> getRelations() {
        return relations;
    }

    public void setRelations(Set<InRelationTable> relations) {
        this.relations = relations;
    }

    @OneToOne //TODO
    public JoinedObjectsTable getNewObject() {
        return newObject;
    }

    public void setNewObject(JoinedObjectsTable newObject) {
        this.newObject = newObject;
    }

    @OneToMany //TODO
    public Set<JoinedObjectsTable> getOldObjects1() {
        return oldObjects1;
    }

    public void setOldObjects1(Set<JoinedObjectsTable> oldObjects1) {
        this.oldObjects1 = oldObjects1;
    }

    @OneToMany //TODO
    public Set<JoinedObjectsTable> getOldObjects2() {
        return oldObjects2;
    }

    public void setOldObjects2(Set<JoinedObjectsTable> oldObjects2) {
        this.oldObjects2 = oldObjects2;
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
