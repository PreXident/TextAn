package cz.cuni.mff.ufal.textan.core;

/**
 * Client side representation of
 * {@link cz.cuni.mff.ufal.textan.commons.ws.NonRootObjectException}.
 */
public class NonRootObjectException extends Exception {
    
    /** Object which is no longer root. */
    final protected long objectId;
    
    /** Object's new root. */
    final protected long newRootId;
    
    /**
     * Only constructor.
     * @param e blue print
     */
    public NonRootObjectException(
            final cz.cuni.mff.ufal.textan.commons.ws.NonRootObjectException e) {
        super(e);
        objectId = e.getFaultInfo().getObjectId();
        newRootId = e.getFaultInfo().getRootObjectId();
    }

    /**
     * Returns {@link #objectId}.
     * @return {@link #objectId}.
     */
    public long getObjectId() {
        return objectId;
    }

    /**
     * Returns {@link #newRootId}.
     * @return {@link #newRootId}
     */
    public long getNewRootId() {
        return newRootId;
    }
}
