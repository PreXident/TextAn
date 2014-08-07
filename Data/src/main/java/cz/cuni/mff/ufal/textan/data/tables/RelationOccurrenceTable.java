package cz.cuni.mff.ufal.textan.data.tables;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Occurrence of a relation in a document. Stores the position and anchor of the relation.
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
@Entity
@Table(name = "RelationOccurrence")
public class RelationOccurrenceTable extends AbstractTable {
    public static final String PROPERTY_NAME_ANCHOR = "anchor";
    public static final String PROPERTY_NAME_DOCUMENT = "document";
    public static final String PROPERTY_NAME_RELATION = "relation";

    private long id;
    private Integer position;
    private String anchor;

    private RelationTable relation;
    private DocumentTable document;

    public RelationOccurrenceTable() {
    }

    /**
     *
     * @param relation
     * @param document
     * @param position
     * @param anchor
     */
    public RelationOccurrenceTable(RelationTable relation, DocumentTable document, Integer position, String anchor) {
        this.relation = relation;
        this.document = document;
        this.position = position;
        this.anchor = anchor;
    }

    public RelationOccurrenceTable(RelationTable relation, DocumentTable document, int position, int length) {
        this(relation, document, position, document.getText().substring(position, position+length));
    }

    @Id
    @GeneratedValue
    @Column(name = "id_relation_occurrence")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "anchor", nullable = true)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    @Column(name = "position", nullable = true)
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "id_relation", nullable = false)
    @IndexedEmbedded
    @ContainedIn
    public RelationTable getRelation() {
        return relation;
    }

    public void setRelation(RelationTable relation) {
        this.relation = relation;
    }

    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "id_document", nullable = false)
    @ContainedIn
    public DocumentTable getDocument() {
        return document;
    }

    public void setDocument(DocumentTable document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return String.format("RelationOccurrence(%d, \"%s\", %d, %s, %s)", this.getId(), this.getAnchor(), this.getPosition(), this.getRelation(), this.getDocument());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelationOccurrenceTable)) return false;
        
        RelationOccurrenceTable rot = (RelationOccurrenceTable)o;
        if (rot.getId() != this.getId()) return false;
        if (rot.getPosition() != this.getPosition()) return false;
        return rot.getAnchor().equals(this.getAnchor());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 53 * hash + this.position;
        hash = 53 * hash + Objects.hashCode(this.anchor);
        return hash;
    }
}
