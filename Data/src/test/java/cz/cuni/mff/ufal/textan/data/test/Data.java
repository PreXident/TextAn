package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.tables.AbstractTable;
import cz.cuni.mff.ufal.textan.data.test.TableAction;

import java.io.Serializable;

import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vaclav Pernicka
 */

@Component
public class Data {

    private final SessionFactory sessionFactory;

    //TODO: constructor injection or property injection?
    @Autowired
    public Data(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * Gets an object from a specified table by given ID
     * <i><b>Important:</b> This method does not supports relation between tables 
     * (lists of other objects). You also cannot change the object.</i>
     * 
     * @param <Table>
     * @param clazz Table.class (class from package data.tables)
     * @param id Id of the record
     * @return null iff there is no record with specified id. Otherwise returns
     *          an instance of specified class (clazz)     *
     */
    public <Table extends AbstractTable> Table getRecordById(final Class<Table> clazz, final Serializable id) {
        Session session = sessionFactory.openSession();
        try {
            //s.beginTransaction();
            return (Table)session.get(clazz, id);
        } finally {
            session.close();
        }
    }
    
    /**
     * Updates a single record
     * 
     * @param <Table>
     * @param clazz
     * @param id
     * @param action 
     */
    public <Table extends AbstractTable> void updateRecordById(final Class<Table> clazz, final Serializable id, TableAction<Table> action) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Table record = (Table)session.get(clazz, id);
            action.action(record);
            session.update(record);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public <Table extends AbstractTable> void selectById(final Class<Table> clazz, final Serializable id, TableAction<Table> action) {
        Session session = sessionFactory.openSession();
        try {
            Table record = (Table)session.get(clazz, id);
            action.action(record);
        } finally {
            session.close();
        }
    }
    
    /**
     * Performs an action on specified table.
     * In the simplest way this method serves as the SELECT ALL query.
     * It can be also used to UPDATE these records returned by SELECT ALL from specified table.
     * 
     * @param <Table> Table on which are we executing command SELECT ALL (and/or UPDATE)
     * @param clazz Just put Table.class here
     * @param action Specify what you will do on each record of the table. You can only get some
     * data (it will work like SELECT) or you can change them (it will be UPDATED)
     */
    public <Table extends AbstractTable> void updateAll(final Class<Table> clazz, final TableAction<Table> action) {
        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();
        ScrollableResults customers = session.createCriteria(clazz) //TODO: Customers!?
            .scroll(ScrollMode.FORWARD_ONLY);
        while ( customers.next() ) {
            Table record = (Table) customers.get(0);
            action.action(record);
            session.update(record);
        }

        tx.commit();
        session.close();
    }
    
    /**
     * Performs an action on specified table.
     * In the simplest way this method serves as the SELECT ALL query.
     * It can be also used to UPDATE these records returned by SELECT ALL from specified table.
     * 
     * @param <Table> Table on which are we executing command SELECT ALL (and/or UPDATE)
     * @param clazz Just put Table.class here
     * @param action Specify what you will do on each record of the table. You can only get some
     * data (it will work like SELECT) or you can change them (it will be UPDATED)
     */
    public <Table extends AbstractTable> void selectAll(final Class<Table> clazz, final TableAction<Table> action) {
        StatelessSession session = sessionFactory.openStatelessSession();
        ScrollableResults customers = session.createCriteria(clazz) //TODO: Customers!?
            .scroll(ScrollMode.FORWARD_ONLY);
        while ( customers.next() ) {
            Table record = (Table) customers.get(0);
            action.action(record);
        }

        session.close();
    }

    public <Table extends AbstractTable> boolean addRecord(Table m) {
        //ObjectTypeTable m = new ObjectTypeTable(name);
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(m);
            // tady uz ma m nastavene ID
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //System.out.println("closing session");
            session.close();
        }
        return true;
    }
 
       public <Table extends AbstractTable> boolean deleteRecord(Table m) {
        //ObjectTypeTable m = new ObjectTypeTable(name);
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(m);
            // tady uz ma m nastavene ID
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //System.out.println("closing session");
            session.close();
        }
        return true;
    }

}
