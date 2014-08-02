package cz.cuni.mff.ufal.textan.server.services;

/**
 * @author Petr Fanta
 */
public class DocumentChangedException extends Exception {

    private static final long serialVersionUID = -605380453650483164L;

    private final long documentId;
    private final long documentVersion;
    private final long ticketVersion;

    public DocumentChangedException(long documentId, long documentVersion, long ticketVersion) {
        this(
                "The document with identifier '" + documentId + "' was changed (your version: " + ticketVersion + ", server version: " + documentVersion + ").",
                documentId,
                documentVersion,
                ticketVersion
        );
    }

    public DocumentChangedException(String message, long documentId, long documentVersion, long ticketVersion) {
        super(message);
        this.documentId = documentId;
        this.ticketVersion = ticketVersion;
        this.documentVersion = documentVersion;
    }

    public long getDocumentId() {
        return documentId;
    }

    public long getTicketVersion() {
        return ticketVersion;
    }

    public long getDocumentVersion() {
        return documentVersion;
    }
}
