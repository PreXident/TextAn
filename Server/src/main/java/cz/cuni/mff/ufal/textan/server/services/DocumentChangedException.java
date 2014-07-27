package cz.cuni.mff.ufal.textan.server.services;

/**
 * @author Petr Fanta
 */
public class DocumentChangedException extends Exception {

    private final long documentId;
    private final int documentVersion;
    private final int ticketVersion;

    public DocumentChangedException(long documentId, int documentVersion, int ticketVersion) {
        this(
                "The document with identifier '" + documentId + "' was changed (your version: " + ticketVersion + ", server version: " + documentVersion + ".", //TODO
                documentId,
                documentVersion,
                ticketVersion
        );
    }

    public DocumentChangedException(String message, long documentId, int documentVersion, int ticketVersion) {
        super(message);
        this.documentId = documentId;
        this.ticketVersion = ticketVersion;
        this.documentVersion = documentVersion;
    }

    public long getDocumentId() {
        return documentId;
    }

    public int getTicketVersion() {
        return ticketVersion;
    }

    public int getDocumentVersion() {
        return documentVersion;
    }
}
