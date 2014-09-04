package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;

import java.util.List;

/**
 * Common operations of Object and Relation DAOs
 *
 * @param <T> ObjectTable/RelationTable
 */
public interface IObjectRelationDAO<T> extends IOperations<T, Long> {

    /**
     * finds all Objects/Relations that have alias equal to specified string
     *
     * @param alias Alias (for objects) or anchor (for relations)
     * @param firstResult index of the first record to return
     * @param pageSize maximal number of records to return
     * @return all records with specified to alias
     */
    List<T> findAllByAliasEqualTo(String alias, int firstResult, int pageSize);

    /**
     * finds all Objects/Relations that have alias equal to specified string
     *
     * @param alias Alias (for objects) or anchor (for relations)
     * @return all records with specified to alias
     */
    List<T> findAllByAliasEqualTo(String alias);

    /**
     * finds all Objects/Relations that have alias containing given substring
     *
     * @param aliasSubstring substring of Alias (for objects) or anchor (for relations)
     * @param firstResult index of the first record to return
     * @param pageSize maximal number of records to return
     * @return all records with alias containing given substring
     */
    List<T> findAllByAliasSubstring(String aliasSubstring, int firstResult, int pageSize);

    /**
     * finds all Objects/Relations that have alias containing given substring
     *
     * @param aliasSubstring substring of Alias (for objects) or anchor (for relations)
     * @return all records with alias containing given substring
     */
    List<T> findAllByAliasSubstring(String aliasSubstring);

    /**
     * finds all objects/relations which occurred in specified document
     *
     * @param documentId Id of document
     * @param firstResult index of the first record to return
     * @param pageSize maximal number of records to return
     * @return all records that occurred in specified document
     */
    List<T> findAllByDocumentOccurrence(long documentId, int firstResult, int pageSize);

    /**
     * finds all objects/relations which occurred in specified document
     *
     * @param documentId Id of document
     * @return all records that occurred in specified document
     */
    List<T> findAllByDocumentOccurrence(long documentId);

    /**
     * finds all objects/relations which occurred in specified document
     *
     * @param document document to list records
     * @param firstResult index of the first record to return
     * @param pageSize maximal number of records to return
     * @return all records that occurred in specified document
     */
    List<T> findAllByDocumentOccurrence(DocumentTable document, int firstResult, int pageSize);

    /**
     * finds all objects/relations which occurred in specified document
     *
     * @param document document to list records
     * @return all records that occurred in specified document
     */
    List<T> findAllByDocumentOccurrence(DocumentTable document);
}
