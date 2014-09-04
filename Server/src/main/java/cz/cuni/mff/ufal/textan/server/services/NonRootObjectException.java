package cz.cuni.mff.ufal.textan.server.services;

/**
 * @author Petr Fanta
 */
public class NonRootObjectException extends Exception {

    private static final long serialVersionUID = -3876726841931601548L;

    private long objectId;
    private long rootObjectId;

    public NonRootObjectException(String message, long objectId, long rootObjectId) {
        super(message);
        this.objectId = objectId;
        this.rootObjectId = rootObjectId;
    }

    public NonRootObjectException(long objectId, long rootObjectId) {
        this("The object with id '" + objectId +"' is not root object, its root object is the object with id '" + rootObjectId + "'.", objectId, rootObjectId);
    }

    public long getObjectId() {
        return objectId;
    }

    public long getRootObjectId() {
        return rootObjectId;
    }
}
