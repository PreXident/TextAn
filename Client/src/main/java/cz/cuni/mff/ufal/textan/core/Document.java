package cz.cuni.mff.ufal.textan.core;

import java.util.Date;

/**
 * Client side reprezentation of {@link cz.cuni.mff.ufal.textan.commons.models.Document}.
 */
public class Document {

    /** Document id. */
    final private long id;

    /** Document text. */
    final private String text;

    /** Indicator whether the document has already been processed. */
    final private boolean processed;

    /** The document has been added to the db at this time. */
    final private Date addTime;

    /** The document has been changed at this time. */
    final private Date lastChangeTime;

    /** The document has been processed at this time. */
    final private Date processTime;

    /**
     * Only constructor.
     * @param document document blueprint
     */
    public Document(final cz.cuni.mff.ufal.textan.commons.models.Document document) {
        id = document.getId();
        text = document.getText();
        processed = document.isProcessed();
        addTime = document.getAddTime();
        lastChangeTime = document.getLastChangeTime();
        processTime = document.getProcessTime();
    }

    /**
     * Returns {@link #addTime}.
     * @return {@link #addTime}
     */
    public Date getAddTime() {
        return addTime;
    }

    /**
     * Returns {@link #id}.
     * @return {@link #id}
     */
    public long getId() {
        return id;
    }

    /**
     * Returns {@link #lastChangeTime}.
     * @return {@link #lastChangeTime}
     */
    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    /**
     * Returns {@link #processed}.
     * @return {@link #processed}
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Returns {@link #processTime}.
     * @return {@link #processTime}
     */
    public Date getProcessTime() {
        return processTime;
    }

    /**
     * Returns {@link #text}.
     * @return {@link #text}
     */
    public String getText() {
        return text;
    }
}
