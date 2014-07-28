package cz.cuni.mff.ufal.textan.server.models;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;

/**
 * A service layer representation of the ObjectType
 *
 * @author Petr Fanta
 * @see cz.cuni.mff.ufal.textan.commons.models.ObjectType
 * @see cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable
 */
public class ObjectType {

    private final long id;
    private final String name;

    /**
     * Instantiates a new Object type.
     *
     * @param id   the id of new type
     * @param name the name of new type
     */
    public ObjectType(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Creates an {@link cz.cuni.mff.ufal.textan.server.models.ObjectType} from an {@link cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable}
     *
     * @param objectTypeTable the object type table
     * @return the object type
     */
    //todo: maybe provide wrappers instead conversion methods
    public static ObjectType fromObjectTypeTable(ObjectTypeTable objectTypeTable) {
        return new ObjectType(objectTypeTable.getId(), objectTypeTable.getName());
    }

    /**
     * Creates an {@link cz.cuni.mff.ufal.textan.server.models.ObjectType} from an {@link cz.cuni.mff.ufal.textan.commons.models.ObjectType}
     *
     * @param commonsObjectType the commons object type
     * @return the object type
     */
    public static ObjectType fromCommonsObjectType(cz.cuni.mff.ufal.textan.commons.models.ObjectType commonsObjectType) {
        return new ObjectType(commonsObjectType.getId(), commonsObjectType.getName());
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Converts an instance to an {@link cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable}
     *
     * @return the object type table
     */
//    public ObjectTypeTable toObjectTypeTable() {
//        ObjectTypeTable objectTypeTable = new ObjectTypeTable();
//        objectTypeTable.setId(id);
//        objectTypeTable.setName(name);
//        return objectTypeTable;
//    }

    /**
     * Converts an instance to an {@link cz.cuni.mff.ufal.textan.commons.models.ObjectType}
     *
     * @return the {@link cz.cuni.mff.ufal.textan.commons.models.ObjectType}
     */
    public cz.cuni.mff.ufal.textan.commons.models.ObjectType toCommonsObjectType() {
        cz.cuni.mff.ufal.textan.commons.models.ObjectType commonsObjectType = new cz.cuni.mff.ufal.textan.commons.models.ObjectType();
        commonsObjectType.setId(id);
        commonsObjectType.setName(name);

        return commonsObjectType;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectType that = (ObjectType) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectType{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
