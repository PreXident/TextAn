package cz.cuni.mff.ufal.textan.commons_old.models;

import cz.cuni.mff.ufal.textan.commons_old.models.adapters.ObjectTypeAdapter;
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

    @Override
    public boolean equals(java.lang.Object other) {
        if (other instanceof ObjectType) {
            return id == ((ObjectType) other).id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }
}
