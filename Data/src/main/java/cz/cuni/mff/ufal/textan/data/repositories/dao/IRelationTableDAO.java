package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.ResultPagination;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;

import java.util.List;


/**
 * DAO interface to get Relations
 * 
 */
public interface IRelationTableDAO extends 
        IObjectRelationDAO<RelationTable>,
        IGlobalVersionedTableDAO<RelationTable> {

    /**
     * finds all relations of specified type
     * 
     * @param relationTypeId id of the relation type
     * @return 
     */
    List<RelationTable> findAllByRelationType(long relationTypeId);
        /**
     * finds all relations of specified type
     * 
     * @param relationTypeId id of the relation type
     * @param firstResult index of first result
     * @param pageSize number of results on the page
     * @return 
     */
    ResultPagination<RelationTable> findAllByRelationTypeWithPagination(long relationTypeId, int firstResult, int pageSize);
    
    /**
     * finds all relations of specified type
     * 
     * @param type
     * @return 
     */
    List<RelationTable> findAllByRelationType(RelationTypeTable type);
    /**
     * finds all relations of specified type
     * 
     * @param type
     * @param firstResult index of first result
     * @param pageSize number of results on the page
     * @return 
     */
    ResultPagination<RelationTable> findAllByRelationTypeWithPagination(RelationTypeTable type, int firstResult, int pageSize);

    /**
     * Finds all relations of given type and matching a given pattern in any occurrence by full-text.
     * 
     * @param relationTypeId ID of a relation type.
     * @param anchorFilter Anchor pattern for full-text.
     * @return Relations.
     */
    List<RelationTable> findAllByRelTypeAndAnchorFullText(long relationTypeId, String anchorFilter);
    /**
     * Finds all relations of given type and matching a given pattern in any occurrence by full-text.
     * Supports pagination.
     * 
     * @param relationTypeId ID of a relation type.
     * @param anchorFilter Anchor pattern for full-text.
     * @param firstResult index of first result
     * @param maxResults number of results on the page
     * @return Relations.
     */
    ResultPagination<RelationTable> findAllByRelTypeAndAnchorFullTextWithPagination(long relationTypeId, String anchorFilter, int firstResult, int maxResults);

    /**
     * Finds all relations matching a given pattern in any occurrence by full-text.
     * 
     * @param anchorFilter Anchor pattern for full-text.
     * @return Relations.
     */
    List<RelationTable> findAllByAnchorFullText(String anchorFilter);
 /**
     * Finds all relations matching a given pattern in any occurrence by full-text.
     * Supports pagination.
     * 
     * @param anchorFilter Anchor pattern for full-text.
     * @param firstResult index of first result
     * @param maxResults number of results on the page
     * @return Relations.
     */
    ResultPagination<RelationTable> findAllByAnchorFullTextWithPagination(String anchorFilter, int firstResult, int maxResults);
}
