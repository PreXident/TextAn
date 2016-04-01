/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.exceptions;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

/**
 * Exception thrown if it is tried to join objects that are not root
 *
 * @author Vaclav Pernicka
 */
public class JoiningANonRootObjectException extends AbstractDatabaseTextanException {
    private static final long serialVersionUID = 30002;

    public JoiningANonRootObjectException(ObjectTable badObject) {
        super(badObject);
    }

}
