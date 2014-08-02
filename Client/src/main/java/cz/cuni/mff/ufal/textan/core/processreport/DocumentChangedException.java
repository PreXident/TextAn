package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * Simple client side reprezentation of {@link cz.cuni.mff.ufal.textan.core.processreport.DocumentChangedException}.
 */
public class DocumentChangedException extends Exception {

    /**
     * Only constructor.
     * @param ex underlying exception
     */
    public DocumentChangedException(final cz.cuni.mff.ufal.textan.commons.ws.DocumentChangedException ex) {
        super(ex);
    }
}
