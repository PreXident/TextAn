package cz.cuni.mff.ufal.textan.data.repositories.common;

import java.io.Serializable;
import java.util.List;


/**
 * Basic DAO operations.
 *
 * @param <E> the type of the entity
 * @param <K> the type of the identifier of the entity
 */
public interface IOperations<E, K extends Serializable> {

    /**
     * Finds an entity in a repository by identifier.
     *
     * @param key the identifier
     * @return the entity
     */
    E find(final K key);


    //<T> List<E> findAllByColumn(String columnName, T columnValue);

    /**
     * Finds all entities in a repository.
     *
     * @return the list of entities
     */
    List<E> findAll();

    /**
     * Finds all entities in a repository with pagination support.
     *
     * @param firstResult number of the first returned result indexed from 0.
     * @param pageSize    maximum count of returned results
     * @return the list of entities
     */
    ResultPagination<E> findAllWithPagination(int firstResult, int pageSize);

    /**
     * Adds an entity into a repository.
     *
     * @param entity the entity
     * @return the identifier of the added entity
     */
    K add(E entity);

    /**
     * Updates an entity in a repository.
     *
     * @param entity the entity
     */
    void update(E entity);

    /**
     * Deletes an entity from a repository.
     *
     * @param entity the entity
     */
    void delete(E entity);

    /**
     * Deletes an entity from a repository by identifier.
     *
     * @param key the entity identifier
     */
    void delete(final K key);

}