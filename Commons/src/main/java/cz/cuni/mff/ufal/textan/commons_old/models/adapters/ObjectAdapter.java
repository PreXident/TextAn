package cz.cuni.mff.ufal.textan.commons_old.models.adapters;

import cz.cuni.mff.ufal.textan.commons_old.models.Object;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapts AdaptedObject and Object.
 */
public class ObjectAdapter extends XmlAdapter<AdaptedObject, Object> {

    @Override
    public Object unmarshal(AdaptedObject adaptedObject) throws Exception {
        return new Object(adaptedObject.getId(), adaptedObject.getType(), adaptedObject.getAliases());
    }

    @Override
    public AdaptedObject marshal(Object object) throws Exception {
        AdaptedObject adaptedObject = new AdaptedObject();
        adaptedObject.setId(object.getId());
        adaptedObject.setType(object.getType());
        adaptedObject.setAliases(object.getAliases());
        return adaptedObject;
    }
}
