/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

/**
 *
 * @author Václav Pernička
 */
public class AliasOccurrenceTable extends AbstractTable {
    public static final String PROPERTY_NAME_DOCUMENT = "document";

    private long id;
    private int position;
    
    private AliasTable alias;
    private DocumentTable document;

    public AliasOccurrenceTable() {
    }

    public AliasOccurrenceTable(int position, AliasTable alias, DocumentTable document) {
        this.position = position;
        this.alias = alias;
        this.document = document;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public AliasTable getAlias() {
        return alias;
    }

    public void setAlias(AliasTable alias) {
        this.alias = alias;
    }

    public DocumentTable getDocument() {
        return document;
    }

    public void setDocument(DocumentTable document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return String.format("AliasOccurrence(%d, %d, %s, %s)", this.getId(), getPosition(), getAlias(), getDocument());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AliasOccurrenceTable)) return false;
        AliasOccurrenceTable aot = (AliasOccurrenceTable)o;
        if (aot.getId() != this.getId()) return false;
        if (aot.getPosition() != this.getPosition()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 23 * hash + this.position;
        return hash;
    }
    
    
    
    
}
