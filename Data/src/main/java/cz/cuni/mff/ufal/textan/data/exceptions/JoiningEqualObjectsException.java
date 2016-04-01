/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.exceptions;

/**
 * This exception is thrown when it is attempted to join two equal objects.
 *
 * @author Vaclav Pernicka
 */
public class JoiningEqualObjectsException extends AbstractDatabaseTextanException {
    private static final long serialVersionUID = 30003;

    public JoiningEqualObjectsException() {
        super();
    }

}
