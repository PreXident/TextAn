package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.RelationType;
import java.util.List;

/**
 * Simple class representing marked Relation.
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list.
 */
public abstract class RelationBuilder extends AbstractBuilder {

    /** Relation type. */
    protected final RelationType type;

    /**
     * Objects in relation.
     */
    protected final List<? extends IRelationInfo> data = createRelationInfos();

    /**
     * Only constructor.
     * @param type relation type
     */
    public RelationBuilder(final RelationType type) {
        this.type = type;
    }

    /**
     * Returns relation type.
     * @return relation type
     */
    public RelationType getType() {
        return type;
    }

    @Override
    protected RelationBuilder extract(final Word word) {
        return word.getRelation();
    }

    @Override
    protected void register(Word word) {
        word.setRelation(this);
    }

    @Override
    protected void unregister(Word word) {
        word.setRelation(null);
    }

    protected abstract List<? extends IRelationInfo> createRelationInfos();

    public interface IRelationInfo {
        int getOrder();
        Object getObject();
    }
}
