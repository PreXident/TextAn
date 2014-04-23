package cz.cuni.mff.ufal.textan.server.models;

/**
 * A service layer representation of the Entity
 * @author Petr Fanta
 */
public class Entity {

    private final String value;
    private final int position;
    private final int length;
    private final long type;

    public Entity(String value, int position, int length, long type) {
        this.value = value;
        this.position = position;
        this.length = length;
        this.type = type;
    }

    public static Entity fromCommonsEntity(cz.cuni.mff.ufal.textan.commons.models.Entity commonsEntity) {
        return new Entity(commonsEntity.getValue(), commonsEntity.getPosition(), commonsEntity.getLength(), commonsEntity.getType());
    }

    public String getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public long getType() {
        return type;
    }

    public cz.cuni.mff.ufal.textan.commons.models.Entity toCommonsEntity() {

        cz.cuni.mff.ufal.textan.commons.models.Entity commonsEntity = new cz.cuni.mff.ufal.textan.commons.models.Entity();
        commonsEntity.setValue(value);
        commonsEntity.setPosition(position);
        commonsEntity.setLength(length);
        commonsEntity.setType(type);

        return commonsEntity;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (length != entity.length) return false;
        if (position != entity.position) return false;
        if (type != entity.type) return false;
        if (value != null ? !value.equals(entity.value) : entity.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + position;
        result = 31 * result + length;
        result = 31 * result + (int) (type ^ (type >>> 32));
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
