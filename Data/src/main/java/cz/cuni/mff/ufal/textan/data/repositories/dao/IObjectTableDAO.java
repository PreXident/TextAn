package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;

import java.util.List;

/**
 * DAO interface to get Objects
 * 
 */
public interface IObjectTableDAO extends IObjectRelationDAO<ObjectTable> {

    /**
     * finds all objects of specified type
     * 
     * @param objectTypeId id of object type
     * @param aliasSubStr
     * @param firstResult
     * @param pageSize
     * @return 
     */
    List<ObjectTable> findAllByObjectTypeAndAliasSubStr(long objectTypeId, String aliasSubStr, int firstResult, int pageSize);

    /**
     * finds all objects of specified type
     * 
     * @param objectTypeId id of object type
     * @param firstResult
     * @param pageSize
     * @return 
     */
    List<ObjectTable> findAllByObjectType(long objectTypeId, int firstResult, int pageSize);
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
    List<ObjectTable> findAllByObjectType(ObjectTypeTable type, int firstResult, int pageSize);
    /**
     * finds all objects of specified type
     * 
     * @param type
     * @return 
     */
    List<ObjectTable> findAllByObjectType(ObjectTypeTable type);
}
