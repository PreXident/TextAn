package cz.cuni.mff.ufal.textan.server.models;

import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A service layer representation of the Object
 *
 * @author Petr Fanta
 */
public class Object {

    private final long id;
    private final List<String> aliases;
    private final ObjectType type;
    private final boolean isNew;

    /**
     * Instantiates a new Object.
     *
     * @param id the id
     * @param type the type
     * @param aliases the aliases
     * @param isNew the is new
     */
    public Object(long id, ObjectType type, List<String> aliases, boolean isNew) {
        this.id = id;
        this.aliases = aliases;
        this.type = type;
        this.isNew = isNew;
    }

    /**
     * Creates a {@link cz.cuni.mff.ufal.textan.server.models.Document} from a {@link cz.cuni.mff.ufal.textan.commons.models.Document}
     *
     * @param objectTable the object table
     * @return the object
     */
    public static Object fromObjectTable(ObjectTable objectTable) {

        List<String> aliases = objectTable.getAliases().stream().map(AliasTable::getAlias).collect(Collectors.toList());

        return new Object(
                objectTable.getId(),
                ObjectType.fromObjectTypeTable(objectTable.getObjectType()),
                aliases, false
                );
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
     * Gets aliases.
     *
     * @return the aliases
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public ObjectType getType() {
        return type;
    }

    /**
     * Indicates if object is new (not in the database yet)
     *
     * @return the boolean
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Converts the instance into a {@link cz.cuni.mff.ufal.textan.commons.models.Document}.
     *
     * @return the {@link cz.cuni.mff.ufal.textan.commons.models.Document}
     */
    public cz.cuni.mff.ufal.textan.commons.models.Object toCommonsObject() {

        cz.cuni.mff.ufal.textan.commons.models.Object commonsObject = new cz.cuni.mff.ufal.textan.commons.models.Object();
        commonsObject.setId(id);
        commonsObject.setObjectType(type.toCommonsObjectType());

        cz.cuni.mff.ufal.textan.commons.models.Object.Aliases commonsAliases = new cz.cuni.mff.ufal.textan.commons.models.Object.Aliases();
        commonsAliases.getAlias().addAll(aliases);
        commonsObject.setAliases(commonsAliases);

        commonsObject.setIsNew(isNew);

        return commonsObject;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Object object = (Object) o;

        if (id != object.id) return false;
        if (type != null ? !type.equals(object.type) : object.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Object{")
                .append("id=").append(id)
                .append(", aliases=[").append(aliases.stream().collect(Collectors.joining(","))).append("]")
                .append(", type=").append(type)
                .append(", isNew=").append(isNew)
                .append("}");

        return sb.toString();
    }
}
