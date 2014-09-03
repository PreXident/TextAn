/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.exceptions;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

/**
 *
 * @author Vaclav Pernicka
 */
public class PathDoesNotExistException extends AbstractDatabaseTextanException {
    private static final long serialVersionUID = 30004;

    public PathDoesNotExistException() {
        super();
    }
    
}
