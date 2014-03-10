package cz.cuni.mff.ufal.textan.commons_old.models.adapters;

import cz.cuni.mff.ufal.textan.commons_old.models.Entity;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/*
 * Created by Petr Fanta on 9.12.13.
 */
public class EntityAdapter extends XmlAdapter<AdaptedEntity, Entity> {

    @Override
    public Entity unmarshal(AdaptedEntity adaptedEntity) throws Exception {
        return new Entity(adaptedEntity.getValue(),adaptedEntity.getPosition(), adaptedEntity.getLength(), adaptedEntity.getType());
    }

    @Override
    public AdaptedEntity marshal(Entity entity) throws Exception {
        AdaptedEntity adaptedEntity = new AdaptedEntity();
        adaptedEntity.setValue(entity.getValue());
        adaptedEntity.setPosition(entity.getPosition());
        adaptedEntity.setLength(entity.getLength());
        adaptedEntity.setType(entity.getType());
        return adaptedEntity;
    }
}
