/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.util.List;

/**
 *
 * @author Václav Pernička
 */
public interface IDocumentTableDAO extends IOperations<DocumentTable, Long> {
    
    List<DocumentTable> findAllDocumentsWithObject(ObjectTable obj);
    List<DocumentTable> findAllDocumentsWithObject(Long objectId);

    List<DocumentTable> findAllDocumentsWithRelation(RelationTable relation);
    List<DocumentTable> findAllDocumentsWithRelation(Long relationId);
    
}
