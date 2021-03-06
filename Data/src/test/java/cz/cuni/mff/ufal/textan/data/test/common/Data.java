package cz.cuni.mff.ufal.textan.data.test.common;

import cz.cuni.mff.ufal.textan.data.tables.AbstractTable;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author Vaclav Pernicka
 */

@Component
@SuppressWarnings("unchecked")
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
     * @param clazz   Table.class (class from package data.tables)
     * @param id      Id of the record
     * @return null iff there is no record with specified id. Otherwise returns
     * an instance of specified class (clazz)     *
     */
    public <Table extends AbstractTable> Table getRecordById(final Class<Table> clazz, final Serializable id) {
        try (Session session = sessionFactory.openSession()) {
            //s.beginTransaction();
            return session.get(clazz, id);
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
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Table record = session.get(clazz, id);
            action.action(record);
            session.update(record);
            session.getTransaction().commit();
        }
    }

    public <Table extends AbstractTable> void selectById(final Class<Table> clazz, final Serializable id, TableAction<Table> action) {
        try (Session session = sessionFactory.openSession()) {
            Table record = session.get(clazz, id);
            action.action(record);
        }
    }

    /**
     * Performs an action on specified table.
     * In the simplest way this method serves as the SELECT ALL query.
     * It can be also used to UPDATE these records returned by SELECT ALL from specified table.
     *
     * @param <Table> Table on which are we executing command SELECT ALL (and/or UPDATE)
     * @param clazz   Just put Table.class here
     * @param action  Specify what you will do on each record of the table. You can only get some
     *                data (it will work like SELECT) or you can change them (it will be UPDATED)
     */
    public <Table extends AbstractTable> void updateAll(final Class<Table> clazz, final TableAction<Table> action) {
        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();
        ScrollableResults customers = session.createCriteria(clazz) //TODO: Customers!?
                .scroll(ScrollMode.FORWARD_ONLY);
        while (customers.next()) {
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
     * @param clazz   Just put Table.class here
     * @param action  Specify what you will do on each record of the table. You can only get some
     *                data (it will work like SELECT) or you can change them (it will be UPDATED)
     */
    public <Table extends AbstractTable> void selectAll(final Class<Table> clazz, final TableAction<Table> action) {
        StatelessSession session = sessionFactory.openStatelessSession();
        ScrollableResults customers = session.createCriteria(clazz) //TODO: Customers!?
                .scroll(ScrollMode.FORWARD_ONLY);
        while (customers.next()) {
            Table record = (Table) customers.get(0);
            action.action(record);
        }

        session.close();
    }

    public <Table extends AbstractTable> boolean addRecord(Table m) {
        //ObjectTypeTable m = new ObjectTypeTable(name);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(m);
            // tady uz ma m nastavene ID
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //System.out.println("closing session");

        return true;
    }

    public <Table extends AbstractTable> boolean deleteRecord(Table m) {
        //ObjectTypeTable m = new ObjectTypeTable(name);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(m);
            // tady uz ma m nastavene ID
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //System.out.println("closing session");

        return true;
    }

}
