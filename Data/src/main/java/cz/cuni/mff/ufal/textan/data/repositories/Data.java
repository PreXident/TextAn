/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import org.hibernate.Session;

/**
 *
 * @author Václav Pernička
 */
public class Data {
    
    public static void AddObjectType(String name) {
        ObjectTypeTable m = new ObjectTypeTable("x");
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        s.save(m);
        s.getTransaction().commit();
        s.close();
    }
    
}
