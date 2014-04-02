/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.util.List;

/**
 *
 * @author Václav Pernička
 */
public interface IAliasTableDAO extends IOperations<AliasTable, Long> {
    
    List<AliasTable> findAllAliasesOfObject(ObjectTable obj);
    List<AliasTable> findAllAliasesOfObject(Long objectId);
    
}
