package cz.cuni.mff.ufal.textan.server.models;

import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;

/**
 * A service layer representation of the RelationType
 *
 * @author Petr Fanta
 */
public class RelationType {

    //TODO: add comments
    private final long id;
    private final String name;

    public RelationType(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RelationType fromRelationTypeTable(RelationTypeTable relationTypeTable) {
        return new RelationType(relationTypeTable.getId(), relationTypeTable.getName());
    }

    public static RelationType fromCommonsRelationType(cz.cuni.mff.ufal.textan.commons.models.RelationType commonsRelationType) {
        return new RelationType(commonsRelationType.getId(), commonsRelationType.getName());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RelationTypeTable toRelationTypeTable() {
        RelationTypeTable relationTypeTable = new RelationTypeTable();
        relationTypeTable.setId(id);
        relationTypeTable.setName(name);

        return relationTypeTable;
    }

    public cz.cuni.mff.ufal.textan.commons.models.RelationType toCommonsRelationType() {
        cz.cuni.mff.ufal.textan.commons.models.RelationType commonsRelationType = new cz.cuni.mff.ufal.textan.commons.models.RelationType();
        commonsRelationType.setId(id);
        commonsRelationType.setName(name);

        return commonsRelationType;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationType that = (RelationType) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    //fixme: auto generated
    @Override
    public String toString() {
        return "RelationType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
