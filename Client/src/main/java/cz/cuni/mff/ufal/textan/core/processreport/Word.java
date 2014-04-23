package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * Simple holder for information about words.
 */
public class Word {

    /** Word itself. */
    private final String word;

    /** Index in the list of words. */
    private final int index;

    /** Index of the first charater in the report. */
    private final int start;

    /** Index of the last character in the report. */
    private final int end;

    /** Entity that has this word assigned. */
    private EntityBuilder entity;

    /** Relation that has this word assigned. */
    private RelationBuilder relation;

    /**
     * Only constructor
     * @param index {@link #index}
     * @param start {@link #start}
     * @param end {@link #end}
     * @param word {@link #word}
     */
    Word(final int index, final int start, final int end, final String word) {
        this.index = index;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    /**
     * Returns the word itself.
     * @return the word itself
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns index in the list of words/report.
     * @return the index in the list of words
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns index of word's first character in report.
     * @return index of word's first character in report.
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns index of word's last character in report.
     * @return index of word's last character in report.
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns entity which this word is assigned to.
     * @return entity which this word is assigned to
     */
    public EntityBuilder getEntity() {
        return entity;
    }

    /**
     * Sets entity which this word is assigned to
     * @param entity new entity
     */
    public void setEntity(EntityBuilder entity) {
        this.entity = entity;
    }

    /**
     * Returns relation which this word is assigned to.
     * @return relation which this word is assigned to
     */
    public RelationBuilder getRelation() {
        return relation;
    }

    /**
     * Sets relation which this word is assigned to
     * @param relation new relation
     */
    public void setRelation(RelationBuilder relation) {
        this.relation = relation;
    }
}
