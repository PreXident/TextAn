package cz.cuni.mff.ufal.textan.server.models;

/**
 * Created by Vlcak on 5.6.2014.
 */
public class Tag {
    private final String value;
    private final String lemma;
    private final String tag;

    /**
     * Instantiates a new Entity.
     *
     * @param value the value
     * @param lemma the lemma
     * @param tag the tag
     */
    public Tag(String value, String lemma, String tag) {
        this.value = value;
        this.lemma = lemma;
        this.tag = tag;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets lemma.
     *
     * @return the lemma
     */
    public String getLemma() {
        return lemma;
    }

    /**
     * Gets tag.
     *
     * @return the tag
     */
    public String getTag() {
        return tag;
    }
}
