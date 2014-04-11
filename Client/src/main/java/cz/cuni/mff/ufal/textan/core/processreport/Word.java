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
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @return the entity
     */
    public EntityBuilder getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(EntityBuilder entity) {
        this.entity = entity;
    }

    /**
     * @return the relation
     */
    public RelationBuilder getRelation() {
        return relation;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation(RelationBuilder relation) {
        this.relation = relation;
    }
}
