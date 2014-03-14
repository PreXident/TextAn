package cz.cuni.mff.ufal.textan.commons_old.models.adapters;

import cz.cuni.mff.ufal.textan.commons_old.models.RelationType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapts {@link RelationType}
 */
public class RelationTypeAdapter extends XmlAdapter<AdaptedRelationType, RelationType> {

    @Override
    public RelationType unmarshal(AdaptedRelationType adaptedRelationType) throws Exception {
        return new RelationType(adaptedRelationType.getId(), adaptedRelationType.getType());
    }

    @Override
    public AdaptedRelationType marshal(RelationType relationType) throws Exception {
        final AdaptedRelationType art = new AdaptedRelationType();
        art.setId(relationType.getId());
        art.setType(relationType.getType());
        return art;
    }
}
