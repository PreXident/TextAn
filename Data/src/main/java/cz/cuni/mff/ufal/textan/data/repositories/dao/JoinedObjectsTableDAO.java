/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.List;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class JoinedObjectsTableDAO extends AbstractHibernateDAO<JoinedObjectsTable, Long> implements IJoinedObjectsTableDAO {

    @Autowired
    IObjectTableDAO objectDAO;
    
    /**
     *  constructor
     */
    public JoinedObjectsTableDAO() {
        super(JoinedObjectsTable.class);
    }

    @Override
    public ObjectTable join(ObjectTable obj1, ObjectTable obj2) {
        // todo checking
        ObjectTable newObj = new ObjectTable("join(" + obj1.getData() + ", " + obj2.getData() + ")", obj1.getObjectType());
        JoinedObjectsTable joinedObj = new JoinedObjectsTable(newObj, obj1, obj2);
        
        //todo lock
        
        objectDAO.add(newObj);
        add(joinedObj);
        
        setRootInSubTree(obj1, newObj);
        setRootInSubTree(obj2, newObj);
        
        return newObj;
    }
    
    private void setRootInSubTree(ObjectTable obj, ObjectTable root) {
        obj.setRootObject(root);
        
        if (obj.getNewObject() == null) return;
        
        setRootInSubTree(obj.getNewObject().getOldObject1(), root);
        setRootInSubTree(obj.getNewObject().getOldObject2(), root);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<JoinedObjectsTable> findAllSinceGlobalVersion(long version) {
                return findAllCriteria()
                .add(Restrictions.ge(JoinedObjectsTable.PROPERTY_NAME_GLOBAL_VERSION, version))
                .list();
    }
}
