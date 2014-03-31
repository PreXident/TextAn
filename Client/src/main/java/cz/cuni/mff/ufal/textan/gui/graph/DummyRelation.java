package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;

/**
 * Simple descendant of {@link Relation} for displaying in pseudo hypergraphs.
 */
public class DummyRelation extends Relation {

    public DummyRelation(RelationType type, Pair<Object, Integer> pair) {
        super(-1, type);
        getObjects().add(pair);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return getType().getName();
    }
}
