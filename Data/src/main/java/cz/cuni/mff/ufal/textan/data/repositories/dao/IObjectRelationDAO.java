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
     * @param firstResult
     * @param pageSize
     * @return 
     */
    List<T> findAllByAliasEqualTo(String alias, int firstResult, int pageSize);
    /**
     * finds all Objects/Relations that have alias equal to specified string
     * 
     * @param alias Alias (for objects) or anchor (for relations)
     * @return 
     */
    List<T> findAllByAliasEqualTo(String alias);
    
    /**
     * finds all Objects/Relations that have alias equal to specified string
     * 
     * @param aliasSubstring substring of Alias (for objects) or anchor (for relations)
     * @param firstResult
     * @param pageSize
     * @return 
     */
    List<T> findAllByAliasSubstring(String aliasSubstring, int firstResult, int pageSize);
    /**
     * finds all Objects/Relations that have alias equal to specified string
     * 
     * @param aliasSubstring substring of Alias (for objects) or anchor (for relations)
     * @return 
     */
    List<T> findAllByAliasSubstring(String aliasSubstring);

    /**
     * finds all objects/relations which occurred in specified document
     * 
     * @param documentId Id of document
     * @param firstResult
     * @param pageSize
     * @return 
     */
    List<T> findAllByDocumentOccurrence(long documentId, int firstResult, int pageSize);
    /**
     * finds all objects/relations which occurred in specified document
     * 
     * @param documentId Id of document
     * @return 
     */
    List<T> findAllByDocumentOccurrence(long documentId);

    /**
     * finds all objects/relations which occurred in specified document
     * 
     * @param document
     * @param firstResult
     * @param pageSize
     * @return 
     */    
    List<T> findAllByDocumentOccurrence(DocumentTable document, int firstResult, int pageSize);
    /**
     * finds all objects/relations which occurred in specified document
     * 
     * @param document
     * @return 
     */    
    List<T> findAllByDocumentOccurrence(DocumentTable document);
    
}
