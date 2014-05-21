package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.Relation;
import java.util.Arrays;

/**
 * Object represeting relation between multiple objects without hypergraphs.
 */
public class RelationObject extends cz.cuni.mff.ufal.textan.core.Object {

    protected final Relation relation;

    /**
     * Only constructor.
     * @param relation blueprint
     */
    public RelationObject(final Relation relation) {
        super(-1, new ObjectType(-1, relation.getType().getName()), Arrays.asList(relation.getType().getName()));
        this.relation = relation;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return relation.toString();
    }
}
