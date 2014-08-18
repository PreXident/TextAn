package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.ResultPagination;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;

import java.util.List;

/**
 * DAO interface to get Objects
 * 
 */
public interface IObjectTableDAO extends 
        IObjectRelationDAO<ObjectTable>,
        IGlobalVersionedTableDAO<ObjectTable> {

    List<ObjectTable> findAllByAliasFullText(String pattern);
    ResultPagination<ObjectTable> findAllByAliasFullTextWithPagination(String pattern, int firstResult, int pageSize);

    List<ObjectTable> findAllByObjTypeAndAliasFullText(long objectTypeId, String pattern);
    ResultPagination<ObjectTable> findAllByObjTypeAndAliasFullTextWithPagination(long objectTypeId, String pattern, int firstResult, int pageSize);

    /**
     * finds all objects of specified type and alias
     *
     * @param objectTypeId id of object type
     * @param aliasSubstring
     * @return
     */
    List<ObjectTable> findAllByObjectTypeAndAliasSubStr(long objectTypeId, String aliasSubstring);

    /**
     * finds all objects of specified type and alias
     * 
     * @param objectTypeId id of object type
     * @param aliasSubstring
     * @param firstResult
     * @param pageSize
     * @return 
     */
    ResultPagination<ObjectTable> findAllByObjectTypeAndAliasSubStrWithPagination(long objectTypeId, String aliasSubstring, int firstResult, int pageSize);

    /**
     * finds all objects of specified type
     * 
     * @param objectTypeId id of object type
     * @param firstResult
     * @param pageSize
     * @return 
     */
    ResultPagination<ObjectTable> findAllByObjectTypeWithPagination(long objectTypeId, int firstResult, int pageSize);
    /**
     * finds all objects of specified type
     * 
     * @param objectTypeId id of object type
     * @return 
     */
    List<ObjectTable> findAllByObjectType(long objectTypeId);

    
        /**
     * finds all objects of specified type
     * 
     * @param type
     * @param firstResult
     * @param pageSize
     * @return 
     */
    ResultPagination<ObjectTable> findAllByObjectTypeWithPagination(ObjectTypeTable type, int firstResult, int pageSize);
    /**
     * finds all objects of specified type
     * 
     * @param type
     * @return 
     */
    List<ObjectTable> findAllByObjectType(ObjectTypeTable type);
    
    List<ObjectTable> getNeighbors(ObjectTable object);
    List<ObjectTable> getNeighbors(long objectId);    
    
}
