package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * Simple client side reprezentation of {@link DocumentChangedException}.
 */
public class DocumentAlreadyProcessedException extends Exception {

    /**
     * Only constructor.
     * @param e underlying exception
     */
    public DocumentAlreadyProcessedException(
            final cz.cuni.mff.ufal.textan.commons.ws.DocumentAlreadyProcessedException e) {
        super(e);
    }
}
