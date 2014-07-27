package cz.cuni.mff.ufal.textan.server.services;

import java.util.Date;

/**
 * @author Petr Fanta
 */
public class DocumentAlreadyProcessedException extends Exception {

    private static final long serialVersionUID = -8355602739609373687L;

    private final long documentId;
    private final Date processedDate;

    public DocumentAlreadyProcessedException(long documentId, Date processedDate) {
        this(
                "The document with identifier '" + documentId + "' was already processed (" + processedDate + ")." ,
                documentId,
                processedDate
        );
    }

    public DocumentAlreadyProcessedException(String message, long documentId, Date processedDate) {
        super(message);
        this.documentId = documentId;
        this.processedDate = processedDate;
    }

    public long getDocumentId() {
        return documentId;
    }

    public Date getProcessedDate() {
        return processedDate;
    }
}
