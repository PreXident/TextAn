/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.exceptions;

/**
 * This exception is thrown when there does not exist any path between given objects.
 *
 * @author Vaclav Pernicka
 */
public class PathDoesNotExistException extends AbstractDatabaseTextanException {
    private static final long serialVersionUID = 30004;

    public PathDoesNotExistException() {
        super();
    }

}
