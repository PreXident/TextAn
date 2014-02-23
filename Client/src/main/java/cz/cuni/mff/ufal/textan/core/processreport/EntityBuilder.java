package cz.cuni.mff.ufal.textan.core.processreport;

import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.utils.Pair;

/**
 * Simple class representing marked Entity.
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list.
 */
public class EntityBuilder {
    final int id;

    public EntityBuilder(final int id) {
        this.id = id;
    }

    /**
     * Checks whether this entity is nested in another one and throws exception if so.
     * @param words list of words
     * @param from starting index
     * @param to end index
     */
    private void checkAdding(final Word[] words, final int from, final int to)
            throws SplitEntitiesException {
        if (from == 0 || to == words.length - 1) {
            return;
        }
        final Word first = words[from - 1];
        final Word last = words[to + 1];
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
    public Pair<Integer, Integer> add(final Word[] words, final int from, final int to, final IClearer clearer)
            throws SplitEntitiesException {
        checkAdding(words, from, to);
        Pair<Integer, Integer> bounds = trim(words, from, to, clearer);
        for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
            final Word word = words[i];
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
    private Pair<Integer, Integer> trim(final Word[] words, int from, int to, final IClearer clearer) {
        //clear whites before from
        for (int i = from - 1; i >= 0 && ignore(words[i]); --i) {
            final Word w = words[i];
            w.setEntity(null);
            clearer.clear(i);
        }
        //clear leading whites
        for (; from <= to && ignore(words[from]); ++from) {
            final Word w = words[from];
            w.setEntity(null);
            clearer.clear(from);
        }
        //clear whites after to
        for (int i = to + 1; i < words.length && ignore(words[i]); ++i) {
            final Word w = words[i];
            w.setEntity(null);
            clearer.clear(i);
        }
        //clear trailing whites
        for (; to > from && ignore(words[to]); --to) {
            final Word w = words[to];
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
