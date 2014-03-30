/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

import java.util.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Václav Pernička
 */
public class DocumentTable extends AbstractTable {
    public static final String PROPERTY_NAME_OCCURRENCES = "id";

    private long id;
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

    public Set<AliasOccurrenceTable> getAliasOccurrences() {
        return aliasOccurrences;
    }

    public void setAliasOccurrences(Set<AliasOccurrenceTable> aliasOccurrences) {
        this.aliasOccurrences = aliasOccurrences;
    }

    public Set<RelationOccurrenceTable> getRelationOccurrences() {
        return relationOccurrences;
    }

    public void setRelationOccurrences(Set<RelationOccurrenceTable> relationOccurrences) {
        this.relationOccurrences = relationOccurrences;
    }
    
    public void setProcessedDateToNow() {
        setProcessedDate(Calendar.getInstance().getTime());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    
    
    
}
