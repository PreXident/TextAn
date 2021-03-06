package cz.cuni.mff.ufal.textan.data.tables;

import javax.persistence.*;

/**
 * Join table between relation and object.
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
@Entity
@Table(name = "IsInRelation")
public class InRelationTable extends AbstractTable {

    private long id;
    private int order;
    private String role;
    private RelationTable relation;
    private ObjectTable object;

    public InRelationTable() {
    }

    public InRelationTable(RelationTable relation, ObjectTable object) {
        this(null, 0, relation, object);
    }

    public InRelationTable(int order, RelationTable relation, ObjectTable object) {
        this("", order, relation, object);
    }

    public InRelationTable(String role, int order, RelationTable relation, ObjectTable object) {
        this.role = role;
        this.order = order;

        this.relation = relation;
        this.relation.getObjectsInRelation().add(this);

        this.object = object;
        this.object.getRelations().add(this);
    }

    @Id
    @GeneratedValue
    @Column(name = "id_is_in_relation", nullable = false, unique = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "order_in_relation", nullable = false)
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Column(name = "role", nullable = false)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @ManyToOne
    @JoinColumn(name = "id_relation", nullable = false)
    public RelationTable getRelation() {
        return relation;
    }

    public void setRelation(RelationTable relation) {
        this.relation = relation;
    }

    @ManyToOne
    @JoinColumn(name = "id_object", nullable = false)
    public ObjectTable getObject() {
        return object;
    }

    public void setObject(ObjectTable object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InRelationTable)) return false;

        InRelationTable irt = (InRelationTable) o;
        return irt.getId() == irt.getId() && irt.getOrder() == irt.getOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 59 * hash + this.order;
        return hash;
    }
}
