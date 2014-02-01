package cz.cuni.mff.ufal.textan.commons.models;

import java.util.ArrayList;

/**
 * Created by Petr Fanta on 24.1.14.
 */
public class Relation {

    private int id;
    private ArrayList<Integer> objectInRelationIds;
    private RelationType type;
    private boolean isNew; //TODO: better name and add getter/setter etc.

    public Relation(int id, RelationType type) {
        this.id = id;
        this.type = type;

        objectInRelationIds = new ArrayList<Integer>();
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getObjectInRelationIds() {
        return objectInRelationIds;
    }

    public RelationType getType() {
        return type;
    }
}
