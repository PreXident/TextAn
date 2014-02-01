package cz.cuni.mff.ufal.textan.commons.models;

/**
 * Created by Petr Fanta on 24.1.14.
 */
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
}
