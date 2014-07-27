package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.ws.DocumentChanged;

/**
 * Simple client side reprezentation of {@link DocumentChanged}.
 */
public class DocumentChangedException extends Exception {

    /**
     * Only constructor.
     * @param ex underlying exception
     */
    public DocumentChangedException(final DocumentChanged ex) {
        super(ex);
    }
}
