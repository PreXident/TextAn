package cz.cuni.mff.ufal.textan.core;

import java.io.Serializable;

/**
 * Client representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.ObjectType}.
 */
public class ObjectType implements Serializable {

    /** Object Type ID. */
    private final long id;

    /** Object Type name. */
    private final String name;

    /**
     * Creates Object Type from commons blue print.
     * @param objectType object type blueprint
     */
    public ObjectType(final cz.cuni.mff.ufal.textan.commons.models.ObjectType objectType) {
        id = objectType.getId();
        name = objectType.getName();
    }

    /**
     * Creates Obejct Type from separate values.
     * @param id  object type id
     * @param name object type name
     */
    public ObjectType(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns object type id.
     * @return object type id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns object type name.
     * @return object type name
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(java.lang.Object other) {
        if (other instanceof ObjectType) {
            return id == ((ObjectType) other).id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    /**
     * Creates new commons object type.
     * @return new commons object type
     */
    public cz.cuni.mff.ufal.textan.commons.models.ObjectType toObjectType() {
        final cz.cuni.mff.ufal.textan.commons.models.ObjectType result =
                new cz.cuni.mff.ufal.textan.commons.models.ObjectType();
        result.setId(id);
        result.setName(name);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
