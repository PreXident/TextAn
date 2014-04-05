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
public class InRelationTable extends AbstractTable {
    
    private long id;
    private int order;
    private RelationTable relation;
    private ObjectTable object;

    public InRelationTable() {}
    public InRelationTable(RelationTable relation, ObjectTable object) {
        this(0, relation, object);
    }
    public InRelationTable(int order, RelationTable relation, ObjectTable object) {
        this.order = order;
        this.relation = relation;
        this.object = object;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public RelationTable getRelation() {
        return relation;
    }

    public void setRelation(RelationTable relation) {
        this.relation = relation;
    }

    public ObjectTable getObject() {
        return object;
    }

    public void setObject(ObjectTable object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InRelationTable)) return false;
        
        InRelationTable irt = (InRelationTable)o;
        if (irt.getId() != irt.getId()) return false;
        return irt.getOrder() == irt.getOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 59 * hash + this.order;
        return hash;
    }
    
       
    
}
