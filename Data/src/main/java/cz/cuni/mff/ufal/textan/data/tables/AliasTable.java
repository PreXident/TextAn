package cz.cuni.mff.ufal.textan.data.tables;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

/**
 * Alias of an entity. For a person it could be "Karel", "Kája" or "The butcher"
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
@Entity
@Table(name = "Alias")
public class AliasTable extends AbstractTable {
    public static final String PROPERTY_NAME_ALIAS = "alias";
    public static final String PROPERTY_NAME_OCCURRENCES = "occurrences";
    public static final String PROPERTY_NAME_OBJECT_ID = "object";

    private long id;
    private String alias;

    private ObjectTable object;
    private Set<AliasOccurrenceTable> occurrences = new HashSet<>();


    public AliasTable() {
    }

    public AliasTable(ObjectTable object, String alias) {
        this.object = object;
        this.object.getAliases().add(this);
        
        this.alias = alias;
    }

    @Id
    @GeneratedValue
    @Column(name = "id_alias", nullable = false, unique = true)
    @DocumentId
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "alias", nullable = false)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "id_object", nullable = false)
    @IndexedEmbedded
    @ContainedIn
    public ObjectTable getObject() {
        return object;
    }

    public void setObject(ObjectTable object) {
        this.object = object;
    }

    @OneToMany(mappedBy = "alias")
    @Cascade({CascadeType.ALL})
    @ContainedIn
    public Set<AliasOccurrenceTable> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<AliasOccurrenceTable> occurrences) {
        this.occurrences = occurrences;
    }


    @Override
    public String toString() {
        return String.format("AliasTable(%d, \"%s\", %s)", this.getId(), this.getAlias(), this.getObject());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AliasTable)) return false;
        AliasTable at = (AliasTable) o;
        if (at.getId() != this.getId()) return false;
        return at.getAlias().equals(this.getAlias());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 97 * hash + Objects.hashCode(this.alias);
        return hash;
    }
}
