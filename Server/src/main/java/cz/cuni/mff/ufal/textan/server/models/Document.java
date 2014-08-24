package cz.cuni.mff.ufal.textan.server.models;

import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;

import java.util.Date;

/**
 * A service layer representation of the Document.
 * @author Petr Fanta
 */
public class Document {

    private final long id;
    private final String text;
    private final boolean processed;
    private final Date addTime;
    private final Date lastChangeTime;
    private final Date processTime;

    /**
     * Instantiates a new Document.
     *
     * @param id the id
     * @param text the text
     * @param processed the processed
     * @param addTime the add time
     * @param lastChangeTime the last change time
     * @param processTime the process time
     */
    public Document(long id, String text, boolean processed, Date addTime, Date lastChangeTime, Date processTime) {
        this.id = id;
        this.text = text;
        this.processed = processed;
        this.addTime = addTime;
        this.lastChangeTime = lastChangeTime;
        this.processTime = processTime;
    }

    /**
     * Creates a {@link cz.cuni.mff.ufal.textan.server.models.Document} from a {@link cz.cuni.mff.ufal.textan.data.tables.DocumentTable}
     *
     * @param documentTable the document table
     * @return the document
     */
    public static Document fromDocumentTable(DocumentTable documentTable) {
        return new Document(documentTable.getId(), documentTable.getText(), documentTable.isProcessed(),
                documentTable.getAddedDate(), documentTable.getLastChangeDate(), documentTable.getProcessedDate());
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Indicates if the document was processed.
     *
     * @return the boolean
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Gets the date when the document was added into system.
     *
     * @return the add time
     */
    public Date getAddTime() {
        return addTime;
    }

    /**
     * Gets the date when the document was changed.
     *
     * @return the last change time
     */
    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    /**
     * Gets the date when the document was processed.
     *
     * @return the process time
     */
    public Date getProcessTime() {
        return processTime;
    }

    /**
     * Converts an instance to a {@link cz.cuni.mff.ufal.textan.commons.models.Document}
     *
     * @return the {@link cz.cuni.mff.ufal.textan.commons.models.Document}
     */
    public cz.cuni.mff.ufal.textan.commons.models.Document toCommonsDocument() {

        cz.cuni.mff.ufal.textan.commons.models.Document commonsDocument = new cz.cuni.mff.ufal.textan.commons.models.Document();
        commonsDocument.setId(id);
        commonsDocument.setText(text);
        commonsDocument.setProcessed(processed);
        commonsDocument.setAddTime(addTime);
        commonsDocument.setLastChangeTime(lastChangeTime);
        commonsDocument.setProcessTime(processTime);

        return commonsDocument;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document document = (Document) o;

        if (id != document.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Document{");
        stringBuilder.append("id=").append(id);
        stringBuilder.append(", text='").append(text).append("'");
        stringBuilder.append(", processed=").append(processed);
        stringBuilder.append(", addTime=").append(addTime);
        stringBuilder.append(", lastChangeTime=").append(lastChangeTime);
        stringBuilder.append(", processTime=").append(processTime);
        stringBuilder.append('}');

        return stringBuilder.toString();
    }
}
