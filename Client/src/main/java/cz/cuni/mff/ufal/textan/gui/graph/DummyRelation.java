package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;

/**
 * Simple descendant of {@link Relation} for displaying in pseudo hypergraphs.
 */
public class DummyRelation extends Relation {

    /** Database Object of the relation. */
    private final Triple<Integer, String, Object> triple;

    /**
     * Only constructor
     * @param type relation type
     * @param triple represented InRelation
     */
    public DummyRelation(RelationType type, Triple<Integer, String, Object> triple) {
        super(-1, type);
        getObjects().add(triple);
        this.triple = triple;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return triple.getSecond();
    }
}
