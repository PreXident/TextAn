package cz.cuni.mff.ufal.textan.commons.models.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by Petr Fanta on 11.1.14.
 */

@XmlAccessorType(XmlAccessType.PROPERTY)
class AdaptedObjectType {

    private String name;
    private int id;

    @XmlAttribute
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @XmlAttribute
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
