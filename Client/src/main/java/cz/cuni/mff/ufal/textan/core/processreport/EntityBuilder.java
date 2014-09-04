package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.ObjectType;
import java.util.List;

/**
 * Simple class for building marked Entity (otherwise immutable).
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list.
 */
public class EntityBuilder extends AbstractBuilder {

    /** Cleaner for cleaning entity builders from words. */
    private static final EntityBuilder CLEANER = new EntityBuilder(null);

    /**
     * Cleans entities from words.
     * @param words list of words
     * @param from starting index
     * @param to final index (inclusive)
     * @param clearer functor to clear trimmed words
     * @throws SplitException if an entity should be split
     */
    static public void clear(final List<Word> words, final int from,
            final int to, final IClearer clearer) throws SplitException {
        CLEANER.clean(words, from, to, clearer);
    }

    /** Type Id. */
    protected final ObjectType type;

    /**
     * Only constructor.
     * @param type entity type
     */
    public EntityBuilder(final ObjectType type) {
        this.type = type;
    }

    /**
     * Returns entity type.
     * @return entity type
     */
    public ObjectType getType() {
        return type;
    }

    @Override
    protected EntityBuilder extract(final Word word) {
        return word.getEntity();
    }

    @Override
    protected void register(final Word word) {
        word.setEntity(this);
    }

    @Override
    protected void unregister(final Word word) {
        word.setEntity(null);
    }
}
