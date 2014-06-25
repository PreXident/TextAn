package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import java.util.List;

/**
 * Ancestor of other builders, intended to share code.
 */
public abstract class AbstractBuilder {

    /** Index in the list. */
    protected int index;

    /**
     * Returns index in the list.
     * @return index in the list
     */
    public int getIndex() {
        return index;
    }

    /**
     * Checks whether this builder is nested in another one and throws exception if so.
     * @param words list of words
     * @param from starting index
     * @param to end index
     * @throws SplitException if builders should be split
     */
    private void checkAdding(final List<Word> words, final int from, final int to)
            throws SplitException {
        if (from == 0 || to == words.size() - 1) {
            return;
        }
        final Word first = words.get(from - 1);
        final AbstractBuilder firstBuilder = extract(first);
        final Word last = words.get(to + 1);
        final AbstractBuilder lastBuilder = extract(last);
        if (firstBuilder == lastBuilder && firstBuilder != null) {
            throw new SplitException("Splitting not supported!");
        }
    }

    /**
     * Adds words to the builder.
     * Checks input by {@link #checkAdding(java.util.List, int, int)}.
     * Also clears trailing and leading whites (as defined in
     * {@link #ignore(Word)}).
     * @param words list of words
     * @param from starting index
     * @param to final index (inclusive)
     * @param clearer functor to clear trimmed words
     * @return indeces of actually added words
     * @throws SplitException if an builder should be split
     * @see #trim(java.util.List, int, int)
     */
    public Pair<Integer, Integer> add(final List<Word> words, final int from, final int to, final IClearer clearer)
            throws SplitException {
        checkAdding(words, from, to);
        Pair<Integer, Integer> bounds = trim(words, from, to, clearer);
        for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
            final Word word = words.get(i);
            register(word);
        }
        return bounds;
    }

    /**
     * Cleans the given words from builders.
     * Intended to be use in cleaner descendant.  Checks input by
     * {@link #checkAdding(java.util.List, int, int)}. Also clears trailing and
     * leading whites (as defined in {@link #ignore(Word)}).
     * @param words list of words
     * @param from starting index
     * @param to final index (inclusive)
     * @param clearer functor to clear trimmed words
     * @throws SplitException if an builder should be split
     * @see #trim(java.util.List, int, int)
     */
    protected void clean(final List<Word> words, final int from, final int to, final IClearer clearer) throws SplitException {
        checkAdding(words, from, to);
        final Pair<Integer, Integer> bounds = trim(words, from, to, clearer);
        for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
            final Word word = words.get(i);
            unregister(word);
            clearer.clear(i);
        }
    }

    /**
     * Extract the builder from the word.
     * @param word Word to extract from
     * @return extracted builder
     */
    protected abstract AbstractBuilder extract(Word word);

    /**
     * Return true whether word should be ignored.
     * @param w word to assess
     * @return true if word should be ignored false otherwise.
     */
    private boolean ignore(final Word w) {
        return separators.contains(w.getWord().charAt(0));
    }

    /**
     * Registers builder to the word.
     * @param word word to be register to
     */
    protected abstract void register(Word word);

    /**
     * Clear ignored words from the list. Starting from and end to are just
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
            unregisterOldBuilder(w);
            clearer.clear(i);
        }
        //clear leading whites
        for (; from <= to && ignore(words.get(from)); ++from) {
            final Word w = words.get(from);
            unregisterOldBuilder(w);
            clearer.clear(from);
        }
        //clear whites after to
        for (int i = to + 1; i < words.size() && ignore(words.get(i)); ++i) {
            final Word w = words.get(i);
            unregisterOldBuilder(w);
            clearer.clear(i);
        }
        //clear trailing whites
        for (; to > from && ignore(words.get(to)); --to) {
            final Word w = words.get(to);
            unregisterOldBuilder(w);
            clearer.clear(to);
        }
        return new Pair<>(from, to);
    }

    /**
     * Unregisters the builder from word.
     * @param word  Word to unregister from
     */
    protected abstract void unregister(Word word);

    /**
     * Unregisters the word from its old Builder
     * @param word word to unregister
     */
    protected void unregisterOldBuilder(final Word word) {
        final AbstractBuilder old = extract(word);
        if (old != null) {
            old.unregister(word);
        }
    }

    /**
     * Thrown when splitting would occur.
     */
    public static class SplitException extends Exception {
        /**
         * Only constructor.
         * @param msg exception message
         */
        public SplitException(final String msg) {
            super(msg);
        }
    }

    /**
     * Gets informed when word at index is cleared.
     */
    public interface IClearer {
        /**
         * The word at index is cleared.
         * @param index
         */
        void clear(final int index);
    }
}
