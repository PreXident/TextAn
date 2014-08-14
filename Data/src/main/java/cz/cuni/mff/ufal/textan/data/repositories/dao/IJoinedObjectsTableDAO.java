/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.exceptions.*;
import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 */
public interface IJoinedObjectsTableDAO extends IOperations<JoinedObjectsTable, Long> {

    ObjectTable join(ObjectTable obj1, ObjectTable obj2) throws JoiningANonRootObjectException, JoiningEqualObjectsException;
    
    /**
     *
     * @param version
     * @return
     */
    List<JoinedObjectsTable> findAllSinceGlobalVersion(long version); 
}
