package cz.cuni.mff.ufal.textan.commons.models;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by Petr Fanta on 9.12.13.
 */

@XmlJavaTypeAdapter(EntityAdapter.class)
public class Entity {

    private final String value;
    private final int position;
    private final int length;
    private final int type;

    public Entity(String value, int position, int length, int type) {
        this.value = value;
        this.position = position;
        this.length = length;
        this.type = type;
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

    public int getType() {
        return type;
    }
}
