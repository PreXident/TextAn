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
     * @return list of documents with the object along with total number of documents
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(ObjectTable obj);

    /**
     * finds all documents in which is occurred specified object
     *
     * @param objectId id of object which has to be in all returned documents
     * @return list of documents with the object along with total number of documents
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(long objectId);

    /**
     * finds all documents in which is occurred specified object
     *
     * @param obj object which has to be in all returned documents
     * @param firstResult index of first document to return
     * @param maxResults maximal number of documents to return
     * @return list of documents with the object
     */
    ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithObjectWithPagination(ObjectTable obj, int firstResult, int maxResults);

    /**
     * finds all documents in which is occurred specified object
     *
     * @param objectId id of object which has to be in all returned documents
     * @param firstResult index of first document to return
     * @param maxResults maximal number of documents to return
     * @return list of documents with the object along with total number of documents
     */
    ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithObjectWithPagination(long objectId, int firstResult, int maxResults);

    /**
     * finds all documents in which is occurred specified relation
     *
     * @param relation relation to find documents for
     * @return list of documents with the relation along with total number of documents
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithRelation(RelationTable relation);

    /**
     * finds all documents in which is occurred specified relation
     *
     * @param relationId id of the relation
     * @return all documents containing given relation and their total number
     */
    List<Pair<DocumentTable, Integer>> findAllDocumentsWithRelation(long relationId);

    /**
     * finds all documents in which is occurred specified relation.
     * Supporting pagination.
     *
     * @param relationId id of the relation
     * @param firstResult index of first result
     * @param maxResults number of max page size
     * @return all documents containing given relation and their total number
     */
    ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithRelationWithPagination(long relationId, int firstResult, int maxResults);

    /**
     * Finds all documents including given pattern by full-text.
     *
     * @param pattern pattern to search for in documents
     * @return All documents with pattern
     */
    List<DocumentTable> findAllDocumentsByFullText(String pattern);

    /**
     * Finds all documents including given pattern by full-text.
     * Supporting pagination.
     *
     * @param pattern pattern to search for in documents
     * @param firstResult index of first result
     * @param maxResults number of max page size
     * @return All documents with pattern
     */
    ResultPagination<DocumentTable> findAllDocumentsByFullTextWithPagination(String pattern, int firstResult, int maxResults);

    /**
     * Finds all processed or unprocessed documents.
     *
     * @param processed if true, returns only processed documents. Otherwise returns unprocessed documents.
     * @return List of processed/unprocessed documents.
     */
    List<DocumentTable> findAllProcessedDocuments(boolean processed);

    /**
     * Finds all processed or unprocessed documents.
     * Supporting pagination.
     *
     * @param firstResult index of first result
     * @param maxResults number of max page size
     * @param processed if true, returns only processed documents. Otherwise returns unprocessed documents.
     * @return List of processed/unprocessed documents.
     */
    ResultPagination<DocumentTable> findAllProcessedDocumentsWithPagination(boolean processed, int firstResult, int maxResults);

    /**
     * Finds all processed or unprocessed documents by full-text.
     *
     * @param processed if true, returns only processed documents. Otherwise returns unprocessed documents.
     * @param pattern pattern to search for in documents
     * @return all processed or unprocessed documents matching pattern
     */
    List<DocumentTable> findAllProcessedDocumentsByFullText(boolean processed, String pattern);

    /**
     * Finds all processed or unprocessed documents by full-text.
     * Supporting pagination.
     *
     * @param processed if true, returns only processed documents. Otherwise returns unprocessed documents.
     * @param pattern pattern to search for in documents
     * @param firstResult index of first result
     * @param maxResults number of max page size
     * @return all processed or unprocessed documents matching pattern
     */
    ResultPagination<DocumentTable> findAllProcessedDocumentsByFullTextWithPagination(boolean processed, String pattern, int firstResult, int maxResults);

    /**
     * Finds all documents with an occurrence of given object and alias by full-text.
     *
     * @param objectId Id of object that must be in returned document.
     * @param pattern pattern of alias of the object.
     * @return document and count of occurrences of given object in this document.
     */
    List<Pair<DocumentTable,Integer>> findAllDocumentsWithObjectByFullText(long objectId, String pattern);

    /**
     * Finds all documents with an occurrence of given object and alias by full-text.
     * Supports pagination.
     *
     * @param objectId Id of object that must be in returned document.
     * @param pattern pattern to search for in documents
     * @param firstResult index of first result
     * @param maxResults number of max page size
     * @return document and count of occurrences of given object in this document.
     */
    ResultPagination<Pair<DocumentTable,Integer>> findAllDocumentsWithObjectByFullTextWithPagination(long objectId, String pattern, int firstResult, int maxResults);

    /**
     * Finds all documents with an occurrence of given object and alias by full-text.
     *
     * @param relationId Id of relation that must be in returned document.
     * @param pattern pattern to search for in documents
     * @return document and count of occurrences of given object in this document.
     */
    List<Pair<DocumentTable,Integer>> findAllDocumentsWithRelationByFullText(long relationId, String pattern);

    /**
     * Finds all documents with an occurrence of given object and alias by full-text.
     *
     * @param relationId Id of relation that must be in returned document.
     * @param pattern pattern to search for in documents
     * @param firstResult index of first result
     * @param maxResults number of max page size
     * @return document and count of occurrences of given object in this document.
     */
    ResultPagination<Pair<DocumentTable,Integer>> findAllDocumentsWithRelationByFullTextWithPagination(long relationId, String pattern, int firstResult, int maxResults);
}
