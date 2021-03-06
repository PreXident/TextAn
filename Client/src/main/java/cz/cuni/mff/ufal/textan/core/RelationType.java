package cz.cuni.mff.ufal.textan.core;

import java.io.Serializable;

/**
 * Client representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.RelationType}.
 */
public class RelationType implements Serializable {

    /** Type id. */
    private final long id;

    /** Type name. */
    private final String name;

    /**
     * Constructs RelationType from commons blue print.
     * @param type relation type blue print
     */
    public RelationType(final cz.cuni.mff.ufal.textan.commons.models.RelationType type) {
        this(type.getId(), type.getName());
    }

    /**
     * Constructs from separate values.
     * @param id type id
     * @param name type name
     */
    public RelationType(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns relation type id.
     * @return relation type id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns relation type name
     * @return relation type name
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final java.lang.Object other) {
        if (other == null || !(other instanceof RelationType)) {
            return false;
        }
        return id == ((RelationType) other).id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * Creates new commons RelationType.
     * @return new commons RelationType
     */
    public cz.cuni.mff.ufal.textan.commons.models.RelationType toRelationType() {
        final cz.cuni.mff.ufal.textan.commons.models.RelationType result =
                new cz.cuni.mff.ufal.textan.commons.models.RelationType();
        result.setId(id);
        result.setName(name);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
