package cz.cuni.mff.ufal.textan.commons.models;

import java.util.ArrayList;

/**
 * Created by Petr Fanta on 11.1.14.
 */
public class Object {

    private int id;
    private ArrayList<String> aliases;
    private ObjectType type;
    private boolean isNew; //TODO: better name and add getter/setter etc.

    public Object(int id, ObjectType type, ArrayList<String> aliases) {
        this.id = id;
        this.type = type;
        this.aliases = aliases;
    }

    public int getId() {
        return id;
    }

    public ArrayList<String> getAliases() {
        return aliases;
    }

    public ObjectType getType() {
        return type;
    }
}
