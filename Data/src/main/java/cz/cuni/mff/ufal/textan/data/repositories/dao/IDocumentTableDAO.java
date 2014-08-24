package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.repositories.common.ResultPagination;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
public interface IDocumentTableDAO 
    extends IOperations<DocumentTable, Long>,
        IGlobalVersionedTableDAO<DocumentTable> {

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
    ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithObjectWithPagination(ObjectTable obj, int firstResult, int maxResults);
    /**
     * finds all documents in which is occurred specified object
     * 
     * @param objectId id of object which has to be in all returned documents
     * @param firstResult
     *@param maxResults @return list of documents with the object
     */
    ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithObjectWithPagination(long objectId, int firstResult, int maxResults);

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
    ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithRelationWithPagination(long relationId, int firstResult, int maxResults);

    List<DocumentTable> findAllDocumentsByFullText(String pattern);
    ResultPagination<DocumentTable> findAllDocumentsByFullTextWithPagination(String pattern, int firstResult, int maxResults);

    List<DocumentTable> findAllProcessedDocuments(boolean processed);
    ResultPagination<DocumentTable> findAllProcessedDocumentsWithPagination(boolean processed, int firstResult, int maxResults);

    List<DocumentTable> findAllProcessedDocumentsByFullText(boolean processed, String pattern);
    ResultPagination<DocumentTable> findAllProcessedDocumentsByFullTextWithPagination(boolean processed, String pattern, int firstResult, int maxResults);

    List<Pair<DocumentTable,Integer>> findAllDocumentsWithObjectByFullText(long objectId, String pattern);
    ResultPagination<Pair<DocumentTable,Integer>> findAllDocumentsWithObjectByFullTextWithPagination(long objectId, String pattern, int firstResult, int maxResults);

    List<Pair<DocumentTable,Integer>> findAllDocumentsWithRelationByFullText(long relationId, String pattern);
    ResultPagination<Pair<DocumentTable,Integer>> findAllDocumentsWithRelationByFullTextWithPagination(long relationId, String pattern, int firstResult, int maxResults);
    
    /**
     * finds all documents newer or equal from specified version
     * 
     * @param version global version which auto increments
     * @return 
     */
    List<DocumentTable> findAllSinceGlobalVersion(long version); 
}
