package cz.cuni.mff.ufal.textan.data.interceptors;

import cz.cuni.mff.ufal.textan.data.tables.AuditTable;
import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Vaclav Pernicka
 */
public class LogInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 20156489756123L;
    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    private static boolean enabled = true;

    //TODO: make thread local variables static?
    private final ThreadLocal<Set<Object>> inserts = ThreadLocal.withInitial(HashSet::new);
    private final ThreadLocal<Set<Object>> updates = ThreadLocal.withInitial(HashSet::new);
    private final ThreadLocal<Set<Object>> deletes = ThreadLocal.withInitial(HashSet::new);
    private final ThreadLocal<String> username = new ThreadLocal<>();
    protected SessionFactory sessionFactory;

    public LogInterceptor(String username) {
        this.username.set(username);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        LogInterceptor.enabled = enabled;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        LOG.debug("LogInterceptor - Executing onSave");
        if (isLoggableTable(entity) && enabled) {
            inserts.get().add(entity);
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        LOG.debug("LogInterceptor - Executing onFlushDirty");
        if (isLoggableTable(entity) && enabled) {
            updates.get().add(entity);
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        LOG.debug("LogInterceptor - Executing onDelete");
        if (isLoggableTable(entity) && enabled) {
            deletes.get().add(entity);
        }
        super.onDelete(entity, id, state, propertyNames, types);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void postFlush(Iterator iterator) {
        LOG.debug("LogInterceptor - Executing postFlush");
        super.postFlush(iterator);

        if (!enabled) return;

        try {
            try (Session session = sessionFactory.openSession()) {
                for (Object entity : inserts.get()) {
                    LOG.debug("postFlush - insert");
                    AuditTable newAudit = new AuditTable(username.get(), AuditTable.AuditType.Insert, entity == null ? "NULL" : entity.toString());
                    session.save(newAudit);
                }

                for (Object entity : updates.get()) {
                    LOG.debug("postFlush - update");
                    AuditTable newAudit = new AuditTable(username.get(), AuditTable.AuditType.Update, entity == null ? "NULL" : entity.toString());
                    session.save(newAudit);
                }

                for (Object entity : deletes.get()) {
                    LOG.debug("postFlush - delete");
                    AuditTable newAudit = new AuditTable(username.get(), AuditTable.AuditType.Delete, entity == null ? "NULL" : entity.toString());
                    session.save(newAudit);
                }
            }
        } finally {
            inserts.get().clear();
            updates.get().clear();
            deletes.get().clear();
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
