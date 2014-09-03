package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.common.ResultPagination;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;

import java.util.List;

/**
 * DAO interface to get Objects.
 *
 */
public interface IObjectTableDAO extends
        IObjectRelationDAO<ObjectTable>,
        IGlobalVersionedTableDAO<ObjectTable> {

    /**
     * Finds all objects satisfying alias pattern by full-text.
     *
     * @param pattern pattern of object alias.
     * @return List of objects.
     */
    List<ObjectTable> findAllByAliasFullText(String pattern);

    /**
     * Finds all objects satisfying alias pattern by full-text.
     * Supporting pagination.
     *
     * @param pattern pattern of object alias.
     * @param firstResult index of first result
     * @param pageSize maximal number of records to return
     * @return List of objects.
     */
    ResultPagination<ObjectTable> findAllByAliasFullTextWithPagination(String pattern, int firstResult, int pageSize);

    /**
     * Finds all objects of given type and matching alias pattern by full-text.
     *
     * @param objectTypeId type id
     * @param pattern Alias pattern
     * @return all objects of given type and matching alias pattern by full-text
     */
    List<ObjectTable> findAllByObjTypeAndAliasFullText(long objectTypeId, String pattern);

    /**
     * Finds all objects of given type and matching alias pattern by full-text.
     * Supporting pagination.
     *
     * @param objectTypeId type id
     * @param pattern Alias pattern
     * @param firstResult index of first result
     * @param pageSize maximal number of records to return
     * @return all objects of given type and matching alias pattern by full-text
     */
    ResultPagination<ObjectTable> findAllByObjTypeAndAliasFullTextWithPagination(long objectTypeId, String pattern, int firstResult, int pageSize);

    /**
     * finds all objects of specified type and alias
     *
     * @param objectTypeId id of object type
     * @param aliasSubstring substring to search for
     * @return all objects with matching alias
     */
    List<ObjectTable> findAllByObjectTypeAndAliasSubStr(long objectTypeId, String aliasSubstring);

    /**
     * finds all objects of specified type and alias
     *
     * @param objectTypeId id of object type
     * @param aliasSubstring substring to search for
     * @param firstResult index of first result
     * @param pageSize maximal number of records to return
     * @return all objects with matching alias
     */
    ResultPagination<ObjectTable> findAllByObjectTypeAndAliasSubStrWithPagination(long objectTypeId, String aliasSubstring, int firstResult, int pageSize);

    /**
     * finds all objects of specified type
     *
     * @param objectTypeId id of object type
     * @param firstResult index of first result
     * @param pageSize maximal number of records to return
     * @return all objects with matching alias
     */
    ResultPagination<ObjectTable> findAllByObjectTypeWithPagination(long objectTypeId, int firstResult, int pageSize);

    /**
     * finds all objects of specified type
     *
     * @param objectTypeId id of object type
     * @return all object with given type
     */
    List<ObjectTable> findAllByObjectType(long objectTypeId);

    /**
     * finds all objects of specified type
     *
     * @param type object type
     * @param firstResult index of first result
     * @param pageSize maximal number of records to return
     * @return all object with given type
     */
    ResultPagination<ObjectTable> findAllByObjectTypeWithPagination(ObjectTypeTable type, int firstResult, int pageSize);

    /**
     * finds all objects of specified type
     *
     * @param type object type
     * @return all object with given type
     */
    List<ObjectTable> findAllByObjectType(ObjectTypeTable type);

    /**
     * Finds all objects related to a specified object.
     *
     * @param object Object, to which are connected all objects in result.
     * @return Objects and relation that connects given object and object in result.
     */
    List<Pair<ObjectTable, RelationTable>> getNeighbors(ObjectTable object);

    /**
     * Finds all objects related to a specified object.
     *
     * @param objectId ID of an object, to which are connected all objects in result.
     * @return Objects and relation that connects given object and object in result.
     */
    List<Pair<ObjectTable, RelationTable>> getNeighbors(long objectId);
}
