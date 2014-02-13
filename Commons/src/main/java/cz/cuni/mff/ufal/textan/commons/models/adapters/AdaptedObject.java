package cz.cuni.mff.ufal.textan.commons.models.adapters;

import cz.cuni.mff.ufal.textan.commons.models.ObjectType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Adaptor for Object.
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AdaptedObject {

    private int id;
    private List<String> aliases;
    private ObjectType type;
    private boolean isNew; //TODO: better name and add getter/setter etc.

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    @XmlElement
    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    @XmlAttribute
    public boolean isIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

}
