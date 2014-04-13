package cz.cuni.mff.ufal.textan.core;

/**
 * Client representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.RelationType}.
 */
public class RelationType {

    /** Type id. */
    private final long id;

    /** Type name. */
    private final String name;

    /**
     * Only constructor.
     * @param type relation type blue print
     */
    public RelationType(final cz.cuni.mff.ufal.textan.commons.models.RelationType type) {
        id = type.getId();
        name = type.getName();
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
}
