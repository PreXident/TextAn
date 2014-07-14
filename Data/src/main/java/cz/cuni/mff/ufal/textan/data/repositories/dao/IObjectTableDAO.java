package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;

import java.util.List;

/**
 * DAO interface to get Objects
 * 
 */
public interface IObjectTableDAO extends IObjectRelationDAO<ObjectTable> {

    List<ObjectTable> findAllByAliasFullText(String pattern);
    List<ObjectTable> findAllByAliasFullText(String pattern, int firstResult, int pageSize);

    List<ObjectTable> findAllByObjTypeAndAliasFullText(long objectTypeId, String pattern);
    List<ObjectTable> findAllByObjTypeAndAliasFullText(long objectTypeId, String pattern, int firstResult, int pageSize);

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
    List<ObjectTable> findAllByObjectTypeAndAliasSubStr(long objectTypeId, String aliasSubstring, int firstResult, int pageSize);

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
    
    /**
     * finds all object newer or equal from specified version
     * 
     * @param version global version which auto increments with object creation or update
     * @return 
     */
    List<ObjectTable> findAllSinceGlobalVersion(long version);   
}
