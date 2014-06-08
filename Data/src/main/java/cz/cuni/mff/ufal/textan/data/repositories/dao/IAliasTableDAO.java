/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 */
public interface IAliasTableDAO extends IOperations<AliasTable, Long> {
    
    /**
     * finds all aliases of the specified object
     * 
     * @param obj object
     * @return list of all aliases of the object
     */
    List<AliasTable> findAllAliasesOfObject(ObjectTable obj);

    /**
     * finds all aliases of the specified object
     * 
     * @param objectId id  of the object
     * @return list of all aliases of the object
     */
    List<AliasTable> findAllAliasesOfObject(Long objectId);
    
}
