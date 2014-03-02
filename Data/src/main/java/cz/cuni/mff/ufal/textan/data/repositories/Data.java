/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories;

import cz.cuni.mff.ufal.textan.data.tables.AbstractTable;
import java.io.Serializable;
import java.util.List;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

/**
 *
 * @author Václav Pernička
 */
public class Data {
    
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
    @Deprecated
    public <Table extends AbstractTable> Table getRecordById(final Class<Table> clazz, final Serializable id) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            //s.beginTransaction();
            return (Table)s.get(clazz, id);
        } finally {
            s.close();
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
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            s.beginTransaction();
            Table record = (Table)s.get(clazz, id);
            action.action(record);
            s.update(record);
            s.getTransaction().commit();
        } finally {
            s.close();
        }
    }

    public <Table extends AbstractTable> void selectById(final Class<Table> clazz, final Serializable id, TableAction<Table> action) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            Table record = (Table)s.get(clazz, id);
            action.action(record);
        } finally {
            s.close();
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
        StatelessSession session = HibernateUtil.getSessionFactory().openStatelessSession();
        Transaction tx = session.beginTransaction();
        ScrollableResults customers = session.createCriteria(clazz)
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
        StatelessSession session = HibernateUtil.getSessionFactory().openStatelessSession();
        ScrollableResults customers = session.createCriteria(clazz)
            .scroll(ScrollMode.FORWARD_ONLY);
        while ( customers.next() ) {
            Table record = (Table) customers.get(0);
            action.action(record);
        }

        session.close();
    }

    public <Table extends AbstractTable> boolean addRecord(Table m) {
        //ObjectTypeTable m = new ObjectTypeTable(name);
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            s.beginTransaction();
            s.save(m);
            // tady uz ma m nastavene ID
            s.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //System.out.println("closing session");
            s.close();
        }
        return true;
    }
 
       public <Table extends AbstractTable> boolean deleteRecord(Table m) {
        //ObjectTypeTable m = new ObjectTypeTable(name);
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            s.beginTransaction();
            s.delete(m);
            // tady uz ma m nastavene ID
            s.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //System.out.println("closing session");
            s.close();
        }
        return true;
    }
 
       

    
}
