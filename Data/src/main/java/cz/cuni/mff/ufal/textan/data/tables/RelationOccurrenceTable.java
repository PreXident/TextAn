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
public class RelationOccurrenceTable extends AbstractTable {
    private long id;
    private RelationTable relation;
    private DocumentTable document;
    private int position;
    private String anchor;

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
    
    
}
