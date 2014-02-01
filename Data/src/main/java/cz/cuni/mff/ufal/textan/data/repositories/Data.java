/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories;

import cz.cuni.mff.ufal.textan.data.tables.AbstractTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import java.io.Serializable;
import org.hibernate.Session;

/**
 *
 * @author Václav Pernička
 */
public class Data {
    
    /**
     * Gets an object from a specified table by given ID
     * 
     * @param clazz Table (class from package data.tables)
     * @param id Id of the record
     * @return null iff there is no record with specified id. Otherwise returns
     *          an instance of specified class (clazz)     *
     */
    public static Object getRecordById(Class clazz, Serializable id) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            s.beginTransaction();
            return s.get(clazz, id);
        } finally {
            s.close();
        }
    }
    
    public static <Clazz extends AbstractTable> boolean addRecord(Clazz m) {
        //ObjectTypeTable m = new ObjectTypeTable(name);
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            s.beginTransaction();
            s.save(m);
            // tady uz ma m nastavene ID
            s.getTransaction().commit();
        } catch (Exception e) {
            return false;
        } finally {
            //System.out.println("closing session");
            s.close();
        }
        return true;
    }
 
       public static <Clazz extends AbstractTable> boolean deleteRecord(Clazz m) {
        //ObjectTypeTable m = new ObjectTypeTable(name);
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            s.beginTransaction();
            s.delete(m);
            // tady uz ma m nastavene ID
            s.getTransaction().commit();
        } catch (Exception e) {
            return false;
        } finally {
            //System.out.println("closing session");
            s.close();
        }
        return true;
    }
 
    
}
