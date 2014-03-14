package cz.cuni.mff.ufal.textan.commons_old.models.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/*
 * Created by Petr Fanta on 9.12.13.
 */

@XmlAccessorType(XmlAccessType.PROPERTY)
public class AdaptedEntity {

    private String value;
    private int position;
    private int length;
    private int type;

    @XmlAttribute
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    @XmlAttribute
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    @XmlAttribute
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }

    @XmlAttribute
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
