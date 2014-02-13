package cz.cuni.mff.ufal.textan.commons.models;

import cz.cuni.mff.ufal.textan.commons.models.adapters.ObjectAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by Petr Fanta on 11.1.14.
 */
@XmlJavaTypeAdapter(ObjectAdapter.class)
public class Object {

    private final int id;
    private final List<String> aliases;
    private final ObjectType type;
    private boolean isNew; //TODO: better name and add getter/setter etc.

//    public Object() {
//        this(0, null, null);
//    }

    public Object(int id, ObjectType type, List<String> aliases) {
        this.id = id;
        this.type = type;
        this.aliases = aliases;
    }

    public int getId() {
        return id;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public ObjectType getType() {
        return type;
    }

    @Override
    public boolean equals(java.lang.Object other) {
        if (other instanceof Object) {
            return id == ((Object) other).id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return id + ": " + String.join(",", aliases);
    }
}
