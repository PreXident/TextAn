package cz.cuni.mff.ufal.textan.data.repositories.common;

import java.io.Serializable;
import java.util.List;


/**
 * Basic DAO operations.
 * @param <E>  the type of the entity
 * @param <K>  the type of the identifier of the entity
 */
public interface IOperations<E, K extends Serializable> {

    /**
     * Finds an entity in a repository by identifier.
     *
     * @param key the identifier
     * @return the entity
     */
    E find(final K key);

    /**
     * Finds all entities in a repository.
     *
     * @return the list of entities
     */
    List<E> findAll();

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
     * @return the updated entity
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