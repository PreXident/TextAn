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

/**
 * @author Václav Pernička
 */
public class LogInterceptor extends EmptyInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    private Set inserts = new HashSet();
    private Set updates = new HashSet();
    private Set deletes = new HashSet();

    private IAuditTableDAO audit;

    private String username;

    public LogInterceptor(String username) {
        this.username = username;
    }

    public void setAudit(IAuditTableDAO audit) {
        LOG.debug("Inject AuditTableDao: {}", audit);
        this.audit = audit;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        LOG.debug("Executing onSave");
//        System.out.println(username + ": save: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Insert, entity.toString()));
        if (!(entity instanceof AuditTable)) {
            inserts.add(entity);
        }
        return super.onSave(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        LOG.debug("Executing onFlushDirty");
//        System.out.println(username + ": update: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Update, entity.toString()));
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        LOG.debug("Executing onDelete");
//        System.out.println(username + ": delete: " + entity);
        //audit.add(new AuditTable(username, AuditTable.AuditType.Delete, entity.toString()));
        super.onDelete(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void postFlush(Iterator iterator) {
        LOG.debug("Executing postFlush");
        super.postFlush(iterator);

        try {

            for (Iterator it = inserts.iterator(); it.hasNext(); ) {
                Object entity = it.next();
                System.out.println("postFlush - insert");
                //Session session = sessionFactory.getCurrentSession();

                audit.add(new AuditTable(username, AuditTable.AuditType.Insert, entity == null ? "NULL" : entity.toString()));
            }


        } finally {
            inserts.clear();
            updates.clear();
            deletes.clear();
        }
    }
}
