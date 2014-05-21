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
     * @return 
     */
    List<T> findAllByAliasEqualTo(String alias);
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
     * @return 
     */
    List<T> findAllByDocumentOccurrence(Long documentId);
    /**
     * finds all objects/relations which occurred in specified document
     * 
     * @param document
     * @return 
     */    
    List<T> findAllByDocumentOccurrence(DocumentTable document);
    
}
