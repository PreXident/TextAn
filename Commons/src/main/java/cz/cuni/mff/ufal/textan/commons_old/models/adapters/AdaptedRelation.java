package cz.cuni.mff.ufal.textan.commons_old.models.adapters;

import cz.cuni.mff.ufal.textan.commons_old.models.RelationType;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Adapts {@link Relation}.
 */
public class AdaptedRelation {

    protected int id;
    protected List<Integer> objectInRelationIds;
    protected List<Integer> orderInRelation;
    protected RelationType type;
    protected boolean isNew;

    public int getId() {
        return id;
    }

    @XmlAttribute
    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public List<Integer> getObjectInRelationIds() {
        return objectInRelationIds;
    }

    public void setObjectInRelationIds(List<Integer> objectInRelationIds) {
        this.objectInRelationIds = objectInRelationIds;
    }

    @XmlElement
    public List<Integer> getOrderInRelation() {
        return orderInRelation;
    }

    public void setOrderInRelation(List<Integer> orderInRelation) {
        this.orderInRelation = orderInRelation;
    }

    @XmlElement
    public RelationType getType() {
        return type;
    }

    public void setType(RelationType type) {
        this.type = type;
    }

    public boolean isIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
