/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Document - Report - Text written by policemen
 * @author Vaclav Pernicka
 */
@Entity
@Table(name = "Document")
public class DocumentTable extends AbstractTable {
    
    public static final String PROPERTY_NAME_ID = "id";
    public static final String PROPERTY_NAME_ALIAS_OCCURRENCES = "aliasOccurrences";
    public static final String PROPERTY_NAME_RELATION_OCCURRENCES = "relationOccurrences";

    
    private long id;
    private int version;
    
    //@Temporal(TemporalType.TIMESTAMP)
    private Date addedDate = Calendar.getInstance().getTime();
    private Date processedDate;
    private String text;

    private Set<RelationOccurrenceTable> relationOccurrences = new HashSet<>();
    private Set<AliasOccurrenceTable> aliasOccurrences = new HashSet<>();
    
    public DocumentTable() {}

    public DocumentTable(String text) {
        this();
        this.text = text;
    }

    @Id
    @GeneratedValue
    @Column(name = "document_id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "version", nullable = false)
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Column(name = "added", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    @Column(name = "processed", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public boolean isProcessed() {
        return getProcessedDate() != null;
    }

    @Column(name = "text", columnDefinition = "text", nullable = false)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @OneToMany(mappedBy = "id_document") //TODO
    public Set<RelationOccurrenceTable> getRelationOccurrences() {
        return relationOccurrences;
    }

    public void setRelationOccurrences(Set<RelationOccurrenceTable> relationOccurrences) {
        this.relationOccurrences = relationOccurrences;
    }

    @OneToMany //TODO
    public Set<AliasOccurrenceTable> getAliasOccurrences() {
        return aliasOccurrences;
    }

    public void setAliasOccurrences(Set<AliasOccurrenceTable> aliasOccurrences) {
        this.aliasOccurrences = aliasOccurrences;
    }

    public void setProcessedDateToNow() {
        setProcessedDate(new Date());
    }

    @Override
    public String toString() {
        return String.format("Document(%d, %s, %s, \"%s\")", getId(), getAddedDate(), getProcessedDate(), getText());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DocumentTable)) return false;
        DocumentTable dt = (DocumentTable)o;
        return this.getId() == dt.getId();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    
    
    
}
