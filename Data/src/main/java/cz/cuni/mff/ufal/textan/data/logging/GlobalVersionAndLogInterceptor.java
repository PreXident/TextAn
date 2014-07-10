package cz.cuni.mff.ufal.textan.data.logging;

import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.io.Serializable;
import java.util.Iterator;
import org.hibernate.EmptyInterceptor;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 *
 * @author Vaclav Pernicka
 */
public class GlobalVersionAndLogInterceptor extends LogInterceptor {
    private static final long serialVersionUID = 20156489756124L;

    
    public GlobalVersionAndLogInterceptor(String username) {
        super(username);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (entity instanceof ObjectTable) {
            
            Long version = getAndIncreaseGlobalVersion();
            // TODO SET NEW VERSION
            for (int i = 0; i < propertyNames.length; i++) {
                if ("globalVersion".equals(propertyNames[i])) currentState[i] = version;
                if ("data".equals(propertyNames[i])) currentState[i] += " processed";
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
}
