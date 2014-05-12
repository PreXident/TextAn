/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.logging;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAuditTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.AuditTable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Václav Pernička
 */
public class LogInterceptor extends EmptyInterceptor {
   
    private Set inserts = new HashSet();
    private Set updates = new HashSet();
    private Set deletes = new HashSet();
      
    @Autowired
    private IAuditTableDAO audit;
    
    private String username;

    public LogInterceptor(String username) {
        this.username = username;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
//        System.out.println(username + ": save: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Insert, entity.toString()));
        if (!(entity instanceof AuditTable)) {
            inserts.add(entity);
        }
        return super.onSave(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
//        System.out.println(username + ": update: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Update, entity.toString()));
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    
//        System.out.println(username + ": delete: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Delete, entity.toString()));
        super.onDelete(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override 
    public void postFlush(Iterator iterator) {
        super.postFlush(iterator);
		System.out.println("postFlush");
 
	try{
 
		for (Iterator it = inserts.iterator(); it.hasNext();) {
		    Object entity = it.next();
		    System.out.println("postFlush - insert");
                    //Session session = sessionFactory.getCurrentSession();
                    
		    audit.add(new AuditTable(username, AuditTable.AuditType.Insert, entity == null ? "NULL" :  entity.toString()));
		}	
 
			
 
	} finally {
		inserts.clear();
		updates.clear();
		deletes.clear();
	}
       }
    
    
    
}
