package cz.cuni.mff.ufal.textan.data.tables;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Relation itself
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
@Entity
@Indexed(index = "RelationIndex")
@Table(name = "Relation")
public class RelationTable extends AbstractTable {
    public static final String PROPERTY_NAME_RELATION_TYPE_ID = "relationType";
    public static final String PROPERTY_NAME_OCCURRENCES_ID = "occurrences";
    public static final String PROPERTY_NAME_ID = "id";
    public static final String PROPERTY_NAME_GLOBAL_VERSION = "globalVersion";

    private long id;
    private long globalVersion;

    private RelationTypeTable relationType;
    private Set<InRelationTable> objectsInRelation = new HashSet<>();
    private Set<RelationOccurrenceTable> occurrences = new HashSet<>();

    public RelationTable() {
    }

    public RelationTable(RelationTypeTable objectType) {
        this.relationType = objectType;
        this.relationType.getRelationsOfThisType().add(this);
    }

    @Id
    @GeneratedValue
    @DocumentId
    @Column(name = "id_relation", nullable = false, unique = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "globalversion", nullable = false)
    public long getGlobalVersion() {
        return globalVersion;
    }

    public void setGlobalVersion(long globalVersion) {
        this.globalVersion = globalVersion;
    }

    @ManyToOne
    @JoinColumn(name = "id_relation_type", nullable = false)
    @IndexedEmbedded
    public RelationTypeTable getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationTypeTable relationType) {
        this.relationType = relationType;
    }

    @OneToMany(mappedBy = "relation")
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @IndexedEmbedded(includePaths = "anchor")
    @ContainedIn
    public Set<RelationOccurrenceTable> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<RelationOccurrenceTable> occurrences) {
        this.occurrences = occurrences;
    }

    @OneToMany(mappedBy = "relation")
    public Set<InRelationTable> getObjectsInRelation() {
        return objectsInRelation;
    }

    public void setObjectsInRelation(Set<InRelationTable> objectsInRelation) {
        this.objectsInRelation = objectsInRelation;
    }

    @Override
    public String toString() {
        return "RelationTable{" + "id=" + id + ", globalVersion=" + globalVersion + ", relationType=" + relationType + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelationTable)) return false;
        RelationTable rt = (RelationTable) o;

        return rt.getId() == this.getId() && rt.getRelationType().equals(this.getRelationType());

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 97 * hash + Objects.hashCode(this.relationType);
        return hash;
    }
}
