package cz.cuni.mff.ufal.textan.commons_old.models;

import cz.cuni.mff.ufal.textan.commons_old.models.adapters.RelationTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by Petr Fanta on 24.1.14.
 */
@XmlJavaTypeAdapter(RelationTypeAdapter.class)
public class RelationType {

    private final int id;
    private final String type;

    public RelationType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(java.lang.Object other) {
        if (other instanceof RelationType) {
            return id == ((RelationType) other).id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }
}
