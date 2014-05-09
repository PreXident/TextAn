package cz.cuni.mff.ufal.textan.server.models;

/**
 * A service layer representation of the Entity
 * @author Petr Fanta
 */
public class Entity {

    private final String value;
    private final int position;
    private final int length;
    private final ObjectType type;

    /**
     * Instantiates a new Entity.
     *
     * @param value the value
     * @param position the position
     * @param length the length
     * @param type the type
     */
    public Entity(String value, int position, int length, ObjectType type) {
        this.value = value;
        this.position = position;
        this.length = length;
        this.type = type;
    }

    /**
     * Converts an {@link cz.cuni.mff.ufal.textan.commons.models.Entity} to {@link cz.cuni.mff.ufal.textan.server.models.Entity}
     *
     * @param commonsEntity the commons entity
     * @return the entity
     */
    public static Entity fromCommonsEntity(cz.cuni.mff.ufal.textan.commons.models.Entity commonsEntity) {
        return new Entity(commonsEntity.getValue(), commonsEntity.getPosition(), commonsEntity.getLength(), ObjectType.fromCommonsObjectType(commonsEntity.getType()));
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Gets length.
     *
     * @return the length
     */
    public int getLength() {
        return length;
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
     * Converts an instance to an {@link cz.cuni.mff.ufal.textan.commons.models.Entity}
     *
     * @return {@link cz.cuni.mff.ufal.textan.commons.models.Entity}
     */
    public cz.cuni.mff.ufal.textan.commons.models.Entity toCommonsEntity() {

        cz.cuni.mff.ufal.textan.commons.models.Entity commonsEntity = new cz.cuni.mff.ufal.textan.commons.models.Entity();
        commonsEntity.setValue(value);
        commonsEntity.setPosition(position);
        commonsEntity.setLength(length);
        commonsEntity.setType(type.toCommonsObjectType());

        return commonsEntity;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (length != entity.length) return false;
        if (position != entity.position) return false;
        if (type != null ? !type.equals(entity.type) : entity.type != null) return false;
        if (value != null ? !value.equals(entity.value) : entity.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + position;
        result = 31 * result + length;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Entity{");
        sb.append("value='").append(value).append('\'');
        sb.append(", position=").append(position);
        sb.append(", length=").append(length);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
