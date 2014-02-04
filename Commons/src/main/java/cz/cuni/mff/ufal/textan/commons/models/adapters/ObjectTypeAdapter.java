package cz.cuni.mff.ufal.textan.commons.models.adapters;

import cz.cuni.mff.ufal.textan.commons.models.ObjectType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by Petr Fanta on 11.1.14.
 */
public class ObjectTypeAdapter extends XmlAdapter<AdaptedObjectType, ObjectType> {

    @Override
    public ObjectType unmarshal(AdaptedObjectType adaptedObjectType) throws Exception {
        return new ObjectType(adaptedObjectType.getId(), adaptedObjectType.getName());
    }

    @Override
    public AdaptedObjectType marshal(ObjectType objectType) throws Exception {
        AdaptedObjectType adaptedObjectType = new AdaptedObjectType();
        adaptedObjectType.setId(objectType.getId());
        adaptedObjectType.setName(objectType.getName());
        return adaptedObjectType;
    }
}
