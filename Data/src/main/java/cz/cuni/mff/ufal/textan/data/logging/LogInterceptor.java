/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.logging;

import java.io.Serializable;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

/**
 *
 * @author Václav Pernička
 */
public class LogInterceptor extends EmptyInterceptor {
    private String username;

    public LogInterceptor(String username) {
        this.username = username;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        System.out.println(username + ": save: " + entity);
        return super.onSave(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        System.out.println(username + ": update: " + entity);
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    
        System.out.println(username + ": delete: " + entity);
        super.onDelete(entity, id, state, propertyNames, types); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    public String onPrepareStatement(String sql) {
        System.out.println(username + ": " + sql);
        return super.onPrepareStatement(sql); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
