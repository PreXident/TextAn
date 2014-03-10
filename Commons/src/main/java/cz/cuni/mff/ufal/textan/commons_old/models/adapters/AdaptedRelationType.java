package cz.cuni.mff.ufal.textan.commons_old.models.adapters;

import cz.cuni.mff.ufal.textan.commons_old.models.RelationType;

/**
 * Adapting {@link RelationType}.
 */
public class AdaptedRelationType {

    protected int id;
    protected String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
