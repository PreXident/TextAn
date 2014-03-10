package cz.cuni.mff.ufal.textan.commons_old.models;

import cz.cuni.mff.ufal.textan.commons_old.models.adapters.RelationAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by Petr Fanta on 24.1.14.
 */
@XmlJavaTypeAdapter(RelationAdapter.class)
public class Relation {

    private final int id;
    private final List<Integer> objectInRelationIds;
    private final RelationType type;
    private boolean isNew; //TODO: better name and add getter/setter etc.

    public Relation(int id, RelationType type) {
        this.id = id;
        this.type = type;

        objectInRelationIds = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public List<Integer> getObjectInRelationIds() {
        return objectInRelationIds;
    }

    public RelationType getType() {
        return type;
    }

    @Override
    public boolean equals(java.lang.Object other) {
        if (other instanceof Relation) {
            return id == ((Relation) other).id;
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
        return type.getType();
    }
}
