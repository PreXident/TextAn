package cz.cuni.mff.ufal.textan.data.repositories.common;

import cz.cuni.mff.ufal.textan.data.tables.AbstractTable;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
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
public abstract class AbstractHibernateDAO<E extends AbstractTable, K extends Serializable> implements IOperations<E, K>   {

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
    @Autowired
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
     * @param columnName Name of the column in database
     * @param columnValue Value in the column
     * @return List of entities satisfying the column constraint
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    //@Override
    protected <T> List<E> findAllByProperty(String propertyName, T columnValue) {
        return currentSession().createCriteria(type)
                .add(Restrictions.eq(propertyName, columnValue))
                .list();
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