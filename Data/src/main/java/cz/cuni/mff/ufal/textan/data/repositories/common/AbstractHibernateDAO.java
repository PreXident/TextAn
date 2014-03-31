package cz.cuni.mff.ufal.textan.data.repositories.common;

import cz.cuni.mff.ufal.textan.data.tables.AbstractTable;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.hibernate.Criteria;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Basic DAO implemented with Hibernate
 * @param <E>  the type of the entity - extends AbstractTable
 * @param <K>  the type of the identifier of the entity
 * @see cz.cuni.mff.ufal.textan.data.repositories.common.IOperations
 * @see cz.cuni.mff.ufal.textan.data.tables.AbstractTable
 * @see org.hibernate.SessionFactory
 * @see org.hibernate.Session
 * @see org.springframework.transaction.annotation.Transactional
 */
//TODO: inherited classes should be annotated with @org.springframework.stereotype.Repository
@Transactional
//@Service
public abstract class AbstractHibernateDAO<E extends AbstractTable, K extends Serializable> implements IOperations<E, K>   {

    // -----------------------------------------------------
    // --------------- STATIC MEMBERS-----------------------
    // -----------------------------------------------------
    protected final static String thisAlias = "this";
    
    protected final static String getAliasPropertyName(String propertyName) {
        return CommonOperations.getAliasPropertyName(thisAlias, propertyName);
    }  
    // -----------------------------------------------------
    // --------------- NON-STATIC MEMBERS-------------------
    // -----------------------------------------------------
    
    
    protected SessionFactory sessionFactory;
    /**
     * The class of an entity type.
     */
    protected Class<? extends E> type;
    
    protected AbstractHibernateDAO(Class<? extends E> type) {
        this.type = type;
    }

    /**
     * Instantiates a new abstract hibernate DAO.
     */
    @SuppressWarnings("unchecked")
    protected AbstractHibernateDAO() {
        this.type = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Sets session factory.
     *
     * @param sessionFactory the session factory
     */
    //@Autowired
    public final void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns a current session.
     *
     * @return the session
     */
    protected final Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Finds an entity in a repository by identifier.
     *
     * @param key the identifier
     * @return the entity
     * @see org.hibernate.Session#get(Class, java.io.Serializable)
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    @Override
    public E find(K key) {
        return (E)currentSession().get(type, key);
    }

    /**
     * Finds all entities in a repository.
     *
     * @return the list of entities
     * @see org.hibernate.Session#createCriteria(Class)
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    @Override
    public List<E> findAll() {
        return currentSession().createCriteria(type).list();
    }

    /**
     * Finds all entities in a repository which have specified value in some column.
     * Equals to "SELECT * WHERE columnName = columnValue" sql query
     * 
     * @param <T> Type of the column
     * @param propertyName Name of the column in database
     * @param columnValue Value in the column
     * @return List of entities satisfying the column constraint
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    //@Override
    protected <T> List<E> findAllByProperty(String propertyName, T columnValue) {
        return findAllByCriteria(new Criterion[]{Restrictions.eq(propertyName, columnValue)} );
    }
    
    /**
     * Finds all entities in a repository by the specified criteria.
     * Equals to "SELECT * WHERE {criteria}" sql query
     * 
     * @param <T> Type of the column
     * @param criteria Criteria to filter results
     * @return List of entities satisfying criteria constraints
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    protected <T> List<E> findAllByCriteria(Collection<Criterion> criteria) {
        Criteria result = findAllCriteria();
        for (Criterion criterion : criteria) {
            result.add(criterion);
        }
        return result.list();
    }
    /**
     * Finds all entities in a repository by the specified criteria.
     * Equals to "SELECT * WHERE {criteria}" sql query
     * 
     * @param <T> Type of the column
     * @param criteria Criteria to filter results
     * @return List of entities satisfying criteria constraints
     */

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    protected <T> List<E> findAllByCriteria(Criterion[] criteria) {
        return findAllByCriteria(Arrays.asList(criteria));
    }
    
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    protected Criteria findAllCriteria() {
        return currentSession().createCriteria(type, thisAlias);
    }
    

    /**
     * Adds an entity into a repository.
     *
     * @param entity the entity
     * @return the identifier of the added entity
     * @see org.hibernate.Session#save(Object)
     */
    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public K add(E entity) {
        return (K)currentSession().save(entity);
    }

    /**
     * Updates an entity in a repository.
     *
     * @param entity the entity
     * @return the updated entity
     * @see org.hibernate.Session#update(Object)
     */
    @Override
    public void update(E entity) {
        //TODO: merge or update?
        currentSession().update(entity);
    }

    /**
     * Deletes an entity from a repository.
     *
     * @param entity the entity
     * @see org.hibernate.Session#delete(Object)
     */
    @Override
    public void delete(E entity) {
        currentSession().delete(entity);
    }

    /**
     * Deletes an entity from a repository by identifier.
     *
     * @param key the entity identifier
     * @see cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO#find(java.io.Serializable)
     * @see cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO#delete(Object)
     */
    @Override
    public void delete(K key) {
        E entity = find(key);
        delete(entity);
    }
}