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
    
    /**
     * finds all documents in which is occurred specified object
     * 
     * @param obj object which has to be in all returned documents
     * @return list of documents with the object
     */
    List<DocumentTable> findAllDocumentsWithObject(ObjectTable obj);
    /**
     * finds all documents in which is occurred specified object
     * 
     * @param objectId id of object which has to be in all returned documents
     * @return list of documents with the object
     */
    List<DocumentTable> findAllDocumentsWithObject(Long objectId);

    /**
     * finds all documents in which is occurred specified relation
     * 
     * @param relation
     * @return 
     */
    List<DocumentTable> findAllDocumentsWithRelation(RelationTable relation);
    /**
     * finds all documents in which is occurred specified relation
     * 
     * @param relationId id of the relation
     * @return 
     */
    List<DocumentTable> findAllDocumentsWithRelation(Long relationId);
    
}
