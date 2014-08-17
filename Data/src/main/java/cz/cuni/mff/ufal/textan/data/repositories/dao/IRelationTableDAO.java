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
     * @param firstResult
     * @param pageSize
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
     * @param firstResult
     * @param pageSize
     * @return 
     */
    ResultPagination<RelationTable> findAllByRelationTypeWithPagination(RelationTypeTable type, int firstResult, int pageSize);

    /**
     * 
     * @param version
     * @return 
     */
    List<RelationTable> findAllSinceGlobalVersion(long version);

    List<RelationTable> findAllByRelTypeAndAnchorFullText(long relationTypeId, String anchorFilter);
    ResultPagination<RelationTable> findAllByRelTypeAndAnchorFullTextWithPagination(long relationTypeId, String anchorFilter, int firstResult, int maxResults);


    List<RelationTable> findAllByAnchorFullText(String anchorFilter);
    ResultPagination<RelationTable> findAllByAnchorFullTextWithPagination(String anchorFilter, int firstResult, int maxResults);
}
