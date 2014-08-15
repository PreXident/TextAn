package cz.cuni.mff.ufal.textan.data.interceptors;

import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.io.Serializable;
import java.util.Calendar;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 *
 * @author Vaclav Pernicka
 */
public class DocumentLastChangeInterceptor extends LogInterceptor {
    
    private static final long serialVersionUID = 20156489756128L;
 
    public DocumentLastChangeInterceptor(String username) {
        super(username);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (isLastChangeTable(entity)) {

            for (int i = 0; i < propertyNames.length; i++) {
                if ("lastChangeDate".equals(propertyNames[i])) currentState[i] = Calendar.getInstance().getTime();
            }
            
            super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
            return true;
        }

        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean isLastChangeTable(Object entity) {
        return entity instanceof DocumentTable;
    }

}
