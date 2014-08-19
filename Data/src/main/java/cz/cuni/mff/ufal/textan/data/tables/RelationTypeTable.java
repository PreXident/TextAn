package cz.cuni.mff.ufal.textan.data.tables;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.search.annotations.DocumentId;

/**
 * Type of a relation (killed, lived, was engaged, ...)
 *
 * @author Vaclav Pernicka
 */
@Entity
@Table(name = "RelationType")
public class RelationTypeTable extends AbstractTable {
    public static final String PROPERTY_NAME_ID = "id";
    public static final String PROPERTY_NAME_TYPE_NAME = "name";

    private long id;
    private String name;

    private Set<RelationTable> relationsOfThisType = new HashSet<>();

    public RelationTypeTable() {
        this("");
    }

    public RelationTypeTable(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    @Column(name = "id_relation_type")
    @DocumentId
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

    @OneToMany(mappedBy = "relationType")
    //@ContainedIn // -> immutable object
    @Cascade(CascadeType.ALL)
    public Set<RelationTable> getRelationsOfThisType() {
        return relationsOfThisType;
    }

    public void setRelationsOfThisType(Set<RelationTable> relationsOfThisType) {
        this.relationsOfThisType = relationsOfThisType;
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
