package cz.cuni.mff.ufal.textan.core.processreport;

import java.util.List;

/**
 * Simple class representing marked Entity.
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list.
 */
public class EntityBuilder extends AbstractBuilder {

    /** Cleaner for cleaning entity builders from words. */
    private static final EntityBuilder CLEANER = new EntityBuilder(-1);

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
    protected final long id;

    /**
     * Only constructor.
     * @param id entity type id
     */
    public EntityBuilder(final long id) {
        this.id = id;
    }

    /**
     * Returns entity id.
     * @return entity id
     */
    public long getId() {
        return id;
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
