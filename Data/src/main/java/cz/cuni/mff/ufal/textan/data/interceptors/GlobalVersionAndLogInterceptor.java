package cz.cuni.mff.ufal.textan.data.interceptors;

import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.io.Serializable;
import java.util.Iterator;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 *
 * @author Vaclav Pernicka
 */
public class GlobalVersionAndLogInterceptor extends LogInterceptor {
    
    private static final long serialVersionUID = 20156489756124L;
    
    private long curVersion;
    private boolean increaseVersion;
    
    public GlobalVersionAndLogInterceptor(String username) {
        super(username);
    }

    @Override
    public void afterTransactionBegin(Transaction tx) {
        super.afterTransactionBegin(tx);
        increaseVersion = true;
    }
    
    
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (isGlobalVersionTable(entity)) {

            tryIncreaseVersion();

            for (int i = 0; i < propertyNames.length; i++) {
                if ("globalVersion".equals(propertyNames[i])) state[i] = curVersion;
            }
            
            super.onSave(entity, id, state, propertyNames, types);             
            return true;
        }

        return super.onSave(entity, id, state, propertyNames, types); 

    }
    
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (isGlobalVersionTable(entity)) {
            
            tryIncreaseVersion();

            for (int i = 0; i < propertyNames.length; i++) {
                if ("globalVersion".equals(propertyNames[i])) currentState[i] = curVersion;
            }
            
            super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
            return true;
        }

        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    private long getAndIncreaseGlobalVersion() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        // get version record
        GlobalVersionTable gvt =
        (GlobalVersionTable)session.createCriteria(GlobalVersionTable.class)
                .setLockMode(LockMode.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .list().get(0);
        
        //get version
        long result = gvt.getVersion();
        
        // update version
        gvt.setVersion(result + 1);
        //session.update(gvt);
        session.saveOrUpdate(gvt);

        tx.commit();
        session.close();
        
        return result;
    }
    
    private boolean isGlobalVersionTable(Object entity) {
        return entity instanceof ObjectTable || 
                entity instanceof JoinedObjectsTable ||
                entity instanceof RelationTable;
    }
    
    private void tryIncreaseVersion() {
        if (shouldIncreaseVersion())
            curVersion = getAndIncreaseGlobalVersion();
    }
    private boolean shouldIncreaseVersion() {
        if (increaseVersion)
        {
            increaseVersion = false;
            return true;
        }
        return false;
    }
}
