/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.logging;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IAuditTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.AuditTable;
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
 * @author Václav Pernička
 */
public class LogInterceptor extends EmptyInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    private static boolean enabled = true;

    private final Set<Object> inserts = new HashSet<Object>();
    private final Set<Object> updates = new HashSet<Object>();
    private final Set<Object> deletes = new HashSet<Object>();


    private SessionFactory sessionFactory;
     
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
        LOG.debug("Executing onSave");
//        System.out.println(username + ": save: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Insert, entity.toString()));
        if (!(entity instanceof AuditTable) && enabled) {
            inserts.add(entity);
        }
        return super.onSave(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        LOG.debug("Executing onFlushDirty");
//        System.out.println(username + ": update: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Update, entity.toString()));
        if (!(entity instanceof AuditTable) && enabled) {
            updates.add(entity);
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        LOG.debug("Executing onDelete");
//        System.out.println(username + ": delete: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Delete, entity.toString()));
        if (!(entity instanceof AuditTable) && enabled) {
            deletes.add(entity);
        }
        super.onDelete(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void postFlush(Iterator iterator) {
        LOG.debug("Executing postFlush");
        super.postFlush(iterator);
        
        if (!enabled) return;
        
        try {
            Session session = sessionFactory.openSession();
            try {

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
}
