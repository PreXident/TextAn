/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

import java.util.Objects;

/**
 *
 * @author Václav Pernička
 */
public class RelationOccurrenceTable extends AbstractTable {
    public static final String PROPERTY_NAME_ANCHOR = "anchor";
    public static final String PROPERTY_NAME_DOCUMENT = "document";
    public static final String PROPERTY_NAME_RELATION = "relation";

    private long id;
    private int position;
    private String anchor;

    private RelationTable relation;
    private DocumentTable document;

    public RelationOccurrenceTable() {
    }

    public RelationOccurrenceTable(RelationTable relation, DocumentTable document, int position, String anchor) {
        this.relation = relation;
        this.document = document;
        this.position = position;
        this.anchor = anchor;
    }

    public RelationOccurrenceTable(RelationTable relation, DocumentTable document, int position, int length) {
        this(relation, document, position, document.getText().substring(position, position+length));
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RelationTable getRelation() {
        return relation;
    }

    public void setRelation(RelationTable relation) {
        this.relation = relation;
    }

    public DocumentTable getDocument() {
        return document;
    }

    public void setDocument(DocumentTable document) {
        this.document = document;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
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
