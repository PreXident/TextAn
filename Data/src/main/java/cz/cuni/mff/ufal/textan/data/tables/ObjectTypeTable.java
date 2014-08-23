package cz.cuni.mff.ufal.textan.data.tables;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Type of the object (person, adress, ...)
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
@Entity
@Table(name = "ObjectType")
public class ObjectTypeTable extends AbstractTable {
    public static final String PROPERTY_NAME_ID = "id";
    public static final String PROPERTY_NAME_TYPE_NAME = "name";

    private long id;
    private String name;

    private Set<ObjectTable> objectsOfThisType = new HashSet<>();

    public ObjectTypeTable() {
        this("");
    }

    public ObjectTypeTable(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    @Column(name = "id_object_type", nullable = false, unique = true)
    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "objectType")
    //@ContainedIn // -> immutable object
    public Set<ObjectTable> getObjectsOfThisType() {
        return objectsOfThisType;
    }

    public void setObjectsOfThisType(Set<ObjectTable> objectsOfThisType) {
        this.objectsOfThisType = objectsOfThisType;
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
