package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.Object.Aliases;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Client side representation of {@link cz.cuni.mff.ufal.textan.commons.models.Object}.
 */
public class Object implements Serializable {

    /** Alias delimiter for toString() method. */
    static private final String ALIAS_DELIMITER = ", ";

    /** Object ID. */
    private final long id;

    /** List of aliases. */
    private final Set<String> aliases;

    /** Object type. */
    private final ObjectType type;

    /** Flag indicating whether the object was fetched from db or created by client. */
    private final boolean isNew;

    /** Set of new aliases obtained during report processing. */
    private final Set<String> newAliases = new HashSet<>();

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
     * Returns concatenated aliases and new aliases.
     * @return concatenated aliases and new aliases
     */
    public String getAliasString() {
        final StringBuilder s = new StringBuilder();
        getAliasString(s);
        return s.toString();
    }

    /**
     * Appends concatenated aliases and new aliases to builder.
     * @param builder builder to append to
     */
    protected void getAliasString(final StringBuilder builder) {
        builder.append(String.join(ALIAS_DELIMITER, aliases));
        if (!aliases.isEmpty() && !newAliases.isEmpty()) {
            builder.append(ALIAS_DELIMITER);
        }
        builder.append(String.join(ALIAS_DELIMITER, newAliases));
    }

    /**
     * Returns whether the object was fetched from DB or created by client.
     * @return true if the object was fetched from DB, false otherwise
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Returns object type.
     * @return object type
     */
    public ObjectType getType() {
        return type;
    }

    /**
     * Adds new alias if not already in aliases.
     * @param newAlias new alias to add
     */
    public void addNewAlias(final String newAlias) {
        if (!aliases.contains(newAlias)) {
            newAliases.add(newAlias);
        }
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

    /**
     * Removes newAlias from list of new aliases.
     * @param newAlias new alias to remove
     */
    public void removeNewAlias(final String newAlias) {
        newAliases.remove(newAlias);
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append(id);
        s.append(": ");
        getAliasString(s);
        return s.toString();
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
