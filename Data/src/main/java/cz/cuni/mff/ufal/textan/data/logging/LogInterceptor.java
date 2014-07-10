/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.logging;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IAuditTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.AuditTable;
import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Vaclav Pernicka
 */
public class LogInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 20156489756123L;
    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    private static boolean enabled = true;

    private final Set<Object> inserts = new HashSet<>();
    private final Set<Object> updates = new HashSet<>();
    private final Set<Object> deletes = new HashSet<>();


    protected SessionFactory sessionFactory;
     
    private String username;

    public LogInterceptor(String username) {
        this.username = username;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static boolean isEnabled() {
        return enabled;
    }
    public static void setEnabled(boolean enabled) {
        LogInterceptor.enabled = enabled;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        LOG.debug("LogInterceptor - Executing onSave");
//        System.out.println(username + ": save: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Insert, entity.toString()));
        if (isLoggableTable(entity) && enabled) {
            inserts.add(entity);
        }
        return super.onSave(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        LOG.debug("LogInterceptor - Executing onFlushDirty");
//        System.out.println(username + ": update: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Update, entity.toString()));
        if (isLoggableTable(entity) && enabled) {
            updates.add(entity);
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        LOG.debug("LogInterceptor - Executing onDelete");
//        System.out.println(username + ": delete: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Delete, entity.toString()));
        if (isLoggableTable(entity) && enabled) {
            deletes.add(entity);
        }
        super.onDelete(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void postFlush(Iterator iterator) {
        LOG.debug("LogInterceptor - Executing postFlush");
        super.postFlush(iterator);
        
        if (!enabled) return;
        
        try {
            Session session = sessionFactory.openSession();
            try {

                //session.save(new AuditTable(username, AuditTable.AuditType.Insert, "NEW TRANSACTION"));
                for (Iterator<Object> it = inserts.iterator(); it.hasNext(); ) {
                    Object entity = it.next();
                    //System.out.println("postFlush - insert");

                    AuditTable newAudit = new AuditTable(username, AuditTable.AuditType.Insert, entity == null ? "NULL" : entity.toString());
                    session.save(newAudit);
                }

                for (Iterator<Object> it = updates.iterator(); it.hasNext(); ) {
                    Object entity = it.next();
                    //System.out.println("postFlush - update");

                    AuditTable newAudit = new AuditTable(username, AuditTable.AuditType.Update, entity == null ? "NULL" : entity.toString());
                    session.save(newAudit);
                }

                for (Iterator<Object> it = deletes.iterator(); it.hasNext(); ) {
                    Object entity = it.next();
                    //System.out.println("postFlush - delete");

                    AuditTable newAudit = new AuditTable(username, AuditTable.AuditType.Delete, entity == null ? "NULL" : entity.toString());
                    session.save(newAudit);
                }


            } finally {
                session.close();
            }
        } finally {
            inserts.clear();
            updates.clear();
            deletes.clear();
        }
    }
    
    private boolean affectsObjectTable(Object entity) {
        return entity instanceof ObjectTable ||
                entity instanceof JoinedObjectsTable;
    }
    
    private boolean isLoggableTable(Object entity) {
        return !(
                    entity instanceof AuditTable || 
                    entity instanceof GlobalVersionTable
                );
    }

}
