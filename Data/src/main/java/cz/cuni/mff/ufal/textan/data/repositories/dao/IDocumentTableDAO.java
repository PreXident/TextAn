/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
public interface IDocumentTableDAO extends IOperations<DocumentTable, Long> {

    /**
     * finds all documents in which is occurred specified object
     *
     * @param obj object which has to be in all returned documents
     * @return list of documents with the object
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(ObjectTable obj);
    /**
     * finds all documents in which is occurred specified object
     *
     * @param objectId id of object which has to be in all returned documents
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(long objectId);

    /**
     * finds all documents in which is occurred specified object
     * 
     * @param obj object which has to be in all returned documents
     * @return list of documents with the object
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(ObjectTable obj, int firstResult, int maxResults);
    /**
     * finds all documents in which is occurred specified object
     * 
     * @param objectId id of object which has to be in all returned documents
     * @param firstResult
     *@param maxResults @return list of documents with the object
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(long objectId, int firstResult, int maxResults);

    /**
     * finds all documents in which is occurred specified relation
     * 
     * @param relation
     * @return 
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithRelation(RelationTable relation);
    /**
     * finds all documents in which is occurred specified relation
     * 
     * @param relationId id of the relation
     * @return 
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithRelation(long relationId);
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithRelation(long relationId, int firstResult, int maxResults);

    List<DocumentTable> findAllDocumentsByFullText(String pattern);
    List<DocumentTable> findAllDocumentsByFullText(String pattern, int firstResult, int maxResults);

    List<DocumentTable> findAllProcessedDocuments(boolean processed);
    List<DocumentTable> findAllProcessedDocuments(boolean processed, int firstResult, int maxResults);

    List<DocumentTable> findAllProcessedDocumentsByFullText(boolean processed, String pattern);
    List<DocumentTable> findAllProcessedDocumentsByFullText(boolean processed, String pattern, int firstResult, int maxResults);
    List<DocumentTable> findAllProcessed(boolean processed);
}
