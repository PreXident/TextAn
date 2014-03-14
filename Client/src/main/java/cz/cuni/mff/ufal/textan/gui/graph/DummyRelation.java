package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons_old.models.Relation;
import cz.cuni.mff.ufal.textan.commons_old.models.RelationType;

/**
 * Simple descendant of {@link Relation} for displaying in pseudo hypergraphs.
 */
public class DummyRelation extends Relation {

    public DummyRelation(RelationType type) {
        super(-1, type);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return getType().getType();
    }
}
