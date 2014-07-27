package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.models.Occurrence;
import cz.cuni.mff.ufal.textan.commons.models.RelationOccurrence;
import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Callback;

/**
 * Simple class representing marked Relation.
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list. If extending, consider
 * reimplementing writeReplace
 */
public abstract class RelationBuilder extends AbstractBuilder {

    /**
     * Factory used during deserialization.
     * If none is provided, RelationBuilderProxy will be deserialized
     */
    static public RelationBuilderFactory deserializator = null;

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
    protected int position = -1;

    /** Relations' anchor. */
    protected String alias = null;

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

    /**
     * Creates relation infos in constructor.
     * @return relation infos for constructor
     */
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
            if (relInfo.getObject() != null) {
                relation.getObjects().add(new Triple<>(relInfo.getOrder(), relInfo.getRole(), relInfo.getObject()));
            }
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
        final Occurrence occurrence = new Occurrence();
        occurrence.setPosition(position);
        occurrence.setValue(alias);
        result.setAnchor(occurrence);
        return result;
    }

    /**
     * Instead of this RelationBuilder, serialize the proxy.
     * @return proxy to serialize
     * @throws ObjectStreamException
     */
    protected java.lang.Object writeReplace() throws ObjectStreamException {
        return new RelationBuilderProxy(this);
    }

    @FunctionalInterface
    public interface RelationBuilderFactory
            extends Callback<RelationBuilderProxy, RelationBuilder> {
        //nothing
    }

    /**
     * Proxy for (de)serialization.
     * If no {@link #deserializator} is provided, it serves as default fallback.
     */
    public static class RelationBuilderProxy extends RelationBuilder {

        /**
         * Only constructor.
         * @param relationBuilder blue print
         */
        @SuppressWarnings("unchecked")
        public RelationBuilderProxy(final RelationBuilder relationBuilder) {
            super(relationBuilder.type);
            index = relationBuilder.index;
            ((List)data).addAll(relationBuilder.data);
        }

        /**
         * Returns relation data.
         * @return relation data
         */
        public List<? extends IRelationInfo>  getData() {
            return data;
        }

        @Override
        protected List<? extends IRelationInfo> createRelationInfos() {
            return new ArrayList<>();
        }

        /**
         * Implementation of deserialization.
         * @return this if {@link #deserializator} is null, otherwise its result
         * @throws ObjectStreamException
         */
        protected final java.lang.Object readResolve() throws ObjectStreamException {
            return deserializator == null ? this : deserializator.call(this);
        }

        @Override
        protected java.lang.Object writeReplace() throws ObjectStreamException {
            return this; //this object is intended for serialization
        }
    }

    /**
     * Simple holder for object to relation assignments.
     * Before implementing consider extending RelationInfo as it has implemented
     * (de)serialization and provides means for maintaining saved reports'
     * compatibility between different client implementations.
     */
    public interface IRelationInfo extends Serializable {

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

        /**
         * Returns assigned object's role.
         * @return assigned object's role
         */
        String getRole();
    }

    /**
     * Abstract ancestor for RelationInfos with implemented serialization.
     */
    public static abstract class RelationInfo implements IRelationInfo {

        /**
         * Factory used during deserialization.
         * If none is provided, RelationInfoProxy will be deserialized
         */
        static public RelationInfoFactory deserializator = null;

        /**
         * Instead of this RelationInfo, serialize the proxy.
         * @return proxy to serialize
         * @throws ObjectStreamException
         */
        protected final java.lang.Object writeReplace()
                throws ObjectStreamException {
            return new RelationInfoProxy(this);
        }

        @FunctionalInterface
        public interface RelationInfoFactory
                extends Callback<RelationInfoProxy, RelationInfo> {
            //nothing
        }

        /**
         * Proxy for (de)serialization.
         * If no {@link #deserializator} is provided, it serves as default fallback.
         */
        public static class RelationInfoProxy implements IRelationInfo {

            /** Assigned object. */
            public final Object object;

            /** Assigned object's order. */
            public final int order;

            /** Assigned objects's role. */
            public final String role;

            /**
             * Only constructor.
             * @param relationInfo blue print
             */
            public RelationInfoProxy(final RelationInfo relationInfo) {
                object = relationInfo.getObject();
                order = relationInfo.getOrder();
                role = relationInfo.getRole();
            }

            @Override
            public Object getObject() {
                return object;
            }

            @Override
            public int getOrder() {
                return order;
            }

            @Override
            public String getRole() {
                return role;
            }

            /**
             * Implementation of deserialization.
             * @return this if {@link #deserializator} is null, otherwise its result
             * @throws ObjectStreamException
             */
            protected final java.lang.Object readResolve() throws ObjectStreamException {
                return deserializator == null ? this : deserializator.call(this);
            }
        }
    }
}
