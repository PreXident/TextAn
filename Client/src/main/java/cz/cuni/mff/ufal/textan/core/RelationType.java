package cz.cuni.mff.ufal.textan.core;

/**
 * Client representation of {@link cz.cuni.mff.ufal.textan.commons.models.RelationType}.
 */
public class RelationType {

    /** Type id. */
    private final int id;

    /** Type name. */
    private final String name;

    /**
     * Only constructor.
     * @param type relation type blue print
     */
    public RelationType(final cz.cuni.mff.ufal.textan.commons_old.models.RelationType type) {
        id = type.getId();
        name = type.getType();
    }

    /**
     * Returns relation type id.
     * @return relation type id
     */
    public int getId() {
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
    public cz.cuni.mff.ufal.textan.commons_old.models.RelationType toRelationType() {
        return new cz.cuni.mff.ufal.textan.commons_old.models.RelationType(
                id, name
        );
    }
}
