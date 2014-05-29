package cz.cuni.mff.ufal.textan.core;

/**
 * Client side representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException}.
 */
public class IdNotFoundException extends Exception {

    /**
     * Only constructor.
     * @param cause underlying exception
     */
    public IdNotFoundException(final Throwable cause) {
        super(cause);
    }
}
