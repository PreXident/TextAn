package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.Object.Aliases;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Client side representation of {@link cz.cuni.mff.ufal.textan.commons.models.Object}.
 */
public class Object {

    /** Object ID. */
    private final long id;

    /** List of aliases. */
    private final Set<String> aliases;

    /** Object type. */
    private final ObjectType type;

    /** Flag indicating whether the object was fetched from db or created by client. */
    private final boolean isNew;

    /**
     * Creates Object from object blue print.
     * @param obj object's blue print
     */
    public Object(final cz.cuni.mff.ufal.textan.commons.models.Object obj) {
        id = obj.getId();
        type = new ObjectType(obj.getObjectType());
        aliases = new HashSet<>(obj.getAliases().getAlias());
        isNew = obj.isIsNew();
    }

    /**
     * Creates Object from separate values.
     * @param id object id
     * @param type object type
     * @param aliases object aliases
     */
    public Object(final long id, final ObjectType type, final Collection<String> aliases) {
        this.id = id;
        this.type = type;
        this.aliases = new HashSet<>(aliases);
        isNew = true;
    }

    /**
     * Returns object id.
     * @return object id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns set of aliases.
     * @return set of aliases
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Returns object type.
     * @return object type
     */
    public ObjectType getType() {
        return type;
    }

    @Override
    public boolean equals(java.lang.Object other) {
        if (other instanceof Object) {
            return id == ((Object) other).id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (int)(67 * hash + this.id);
        return hash;
    }

    @Override
    public String toString() {
        return id + ": " + String.join(", ", aliases);
    }

    /**
     * Creates new commons Object.
     * @return new commons Object
     */
    public cz.cuni.mff.ufal.textan.commons.models.Object toObject() {
        final cz.cuni.mff.ufal.textan.commons.models.Object result =
                new cz.cuni.mff.ufal.textan.commons.models.Object();
        result.setId(id);
        result.setObjectType(type.toObjectType());
        result.setIsNew(isNew);
        final Aliases alias = new Aliases();
        alias.getAlias().addAll(aliases);
        result.setAliases(alias);
        return result;
    }
}
