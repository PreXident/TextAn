package cz.cuni.mff.ufal.textan.server.services;

/**
 * @author Petr Fanta
 */
public class InvalidMergeException extends Exception {

    private static final long serialVersionUID = 4603929706837743735L;

    private Long objectId;

    public InvalidMergeException(String message, long objectId) {
        super(message);
        this.objectId = objectId;
    }


    public InvalidMergeException(String message) {
        super(message);
        this.objectId = null;
    }

    public Long getObjectId() {
        return objectId;
    }
}
