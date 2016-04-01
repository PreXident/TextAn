package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.exceptions.JoiningANonRootObjectException;
import cz.cuni.mff.ufal.textan.data.exceptions.JoiningEqualObjectsException;
import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

/**
 * @author Vaclav Pernicka
 */
public interface IJoinedObjectsTableDAO
        extends IOperations<JoinedObjectsTable, Long>,
        IGlobalVersionedTableDAO<JoinedObjectsTable> {

    ObjectTable join(ObjectTable obj1, ObjectTable obj2) throws JoiningANonRootObjectException, JoiningEqualObjectsException;

}
