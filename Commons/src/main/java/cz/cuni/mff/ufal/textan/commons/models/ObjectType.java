package cz.cuni.mff.ufal.textan.commons.models;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by Petr Fanta on 11.1.14.
 */

@XmlJavaTypeAdapter(ObjectTypeAdapter.class)
public class ObjectType {
    private final int id;
    private final String name;

    public ObjectType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
