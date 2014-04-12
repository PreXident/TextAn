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

    public cz.cuni.mff.ufal.textan.commons.models.Entity toCommonsEntity() {

        cz.cuni.mff.ufal.textan.commons.models.Entity commonsEntity = new cz.cuni.mff.ufal.textan.commons.models.Entity();
        commonsEntity.setValue(value);
        commonsEntity.setPosition(position);
        commonsEntity.setLength(length);
        commonsEntity.setType(type);

        return commonsEntity;
    }
}
