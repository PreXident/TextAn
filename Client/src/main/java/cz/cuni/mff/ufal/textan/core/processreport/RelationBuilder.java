package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Occurrence;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.RelationOccurrence;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;
import java.util.List;

/**
 * Simple class representing marked Relation.
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list.
 */
public abstract class RelationBuilder extends AbstractBuilder {

    /** Cleaner for cleaning relation builders from words. */
    private static final RelationBuilder CLEANER = new RelationBuilder(null) {
        @Override
        protected List<? extends IRelationInfo> createRelationInfos() {
            return null;
        }

        @Override
        protected void unregister(Word word) {
            final RelationBuilder old = word.getRelation();
            if (old != null) {
                old.unregister(word);
            }
        }
    };

    /**
     * Cleans relations from words.
     * @param words list of words
     * @param from starting index
     * @param to final index (inclusive)
     * @param clearer functor to clear trimmed words
     * @throws SplitException if an relation should be split
     */
    static public void clear(final List<Word> words, final int from,
            final int to, final IClearer clearer) throws SplitException {
        CLEANER.clean(words, from, to, clearer);
    }


    /** Relation type. */
    protected final RelationType type;

    /**
     * Objects in relation.
     */
    protected final List<? extends IRelationInfo> data = createRelationInfos();

    /** Anchors's position in report. */
    protected int position;

    /** Relations' anchor. */
    protected String alias;

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

    protected abstract List<? extends IRelationInfo> createRelationInfos();

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

    /**
     * Creates new Relation from the builder.
     * @return new Relation from the builder
     */
    public cz.cuni.mff.ufal.textan.commons.models.Relation toRelation() {
        final Relation relation = new Relation(-index, type);
        for (IRelationInfo relInfo : data) {
            relation.getObjects().add(new Pair<>(relInfo.getObject(), relInfo.getOrder()));
        }
        return relation.toRelation();
    }

    /**
     * Creates new RelationOccurrence from the builder.
     * Returns null for if the relation has no anchor
     * @return new RelationOccurrence from the builder
     */
    public RelationOccurrence toRelationOccurrence() {
        final RelationOccurrence result = new RelationOccurrence();
        result.setRelationId(-index);
        if (alias != null) {
            final Occurrence occurrence = new Occurrence();
            occurrence.setPosition(position);
            occurrence.setValue(alias);
            result.setAnchor(occurrence);
        }
        return result;
    }

    /**
     * Simple holder for object to relation assignments.
     */
    public interface IRelationInfo {

        /**
         * Returns assigned object.
         * @return assigned object
         */
        Object getObject();

        /**
         * Returns assigned object's order.
         * @return assigned object's order
         */
        int getOrder();
    }
}
