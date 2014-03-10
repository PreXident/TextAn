package cz.cuni.mff.ufal.textan.core.processreport;

import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.utils.Pair;
import java.util.List;

/**
 * Simple class representing marked Entity.
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list.
 */
public class EntityBuilder {

    /** Entity type id. */
    protected final int id;

    /** Index in the list of entities. */
    protected int index;

    public EntityBuilder(final int id) {
        this.id = id;
    }

    /**
     * Returns entity type id.
     * @return entity type id
     */
    public int getId() {
        return id;
    }


    /**
     * Returns index in the list of entities.
     * @return index in the list of entitites
     */
    public int getIndex() {
        return index;
    }

    /**
     * Checks whether this entity is nested in another one and throws exception if so.
     * @param words list of words
     * @param from starting index
     * @param to end index
     * @throws SplitEntitiesException if entity should be split
     */
    private void checkAdding(final List<Word> words, final int from, final int to)
            throws SplitEntitiesException {
        if (from == 0 || to == words.size() - 1) {
            return;
        }
        final Word first = words.get(from - 1);
        final Word last = words.get(to + 1);
        if (first.getEntity() == last.getEntity() && first.getEntity() != null) {
            throw new SplitEntitiesException("Split entities not supported!");
        }
    }

    /**
     * Adds words to the entity.
     * Checks input by {@link #checkAdding(java.util.List, int, int)}.
     * Also clears trailing and leading whites (as defined in
     * {@link #ignore(Word)}).
     * @param words list of words
     * @param from starting index
     * @param to final index (inclusive)
     * @return indeces of actually added words
     * @throws SplitEntitiesException if an entity should be split
     * @see #trim(java.util.List, int, int)
     */
    public Pair<Integer, Integer> add(final List<Word> words, final int from, final int to, final IClearer clearer)
            throws SplitEntitiesException {
        checkAdding(words, from, to);
        Pair<Integer, Integer> bounds = trim(words, from, to, clearer);
        for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
            final Word word = words.get(i);
            word.setEntity(this);
        }
        return bounds;
    }

    /**
     * Return true whether word should be ignored.
     * @param w word to assess
     * @return true if word should be ignored false otherwise.
     */
    private boolean ignore(final Word w) {
        return separators.contains(w.getWord().charAt(0));
    }

    /**
     * Clear words entities from the list. Starting from and end to are just
     * recommendation, as there may be adjacent whites as well.
     * @param words list of words
     * @param from starting index
     * @param to end index (inclusive)
     * @return pair with new trimmed from and to indeces
     */
    private Pair<Integer, Integer> trim(final List<Word> words, int from, int to, final IClearer clearer) {
        //clear whites before from
        for (int i = from - 1; i >= 0 && ignore(words.get(i)); --i) {
            final Word w = words.get(i);
            w.setEntity(null);
            clearer.clear(i);
        }
        //clear leading whites
        for (; from <= to && ignore(words.get(from)); ++from) {
            final Word w = words.get(from);
            w.setEntity(null);
            clearer.clear(from);
        }
        //clear whites after to
        for (int i = to + 1; i < words.size() && ignore(words.get(i)); ++i) {
            final Word w = words.get(i);
            w.setEntity(null);
            clearer.clear(i);
        }
        //clear trailing whites
        for (; to > from && ignore(words.get(to)); --to) {
            final Word w = words.get(to);
            w.setEntity(null);
            clearer.clear(to);
        }
        return new Pair<>(from, to);
    }

    public static class SplitEntitiesException extends Exception {
        public SplitEntitiesException(final String msg) {
            super(msg);
        }
    }

    public interface IClearer {
        void clear(final int index);
    }
}
