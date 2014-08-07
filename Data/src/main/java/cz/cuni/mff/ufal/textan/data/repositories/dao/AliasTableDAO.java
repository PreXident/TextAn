/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.DAOUtils;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class AliasTableDAO extends AbstractHibernateDAO<AliasTable, Long> implements IAliasTableDAO {

    /**
     * finds all aliases of the specified object
     * 
     * @param obj object
     * @return list of all aliases of the object
     */
    @Override
    public List<AliasTable> findAllAliasesOfObject(ObjectTable obj) {
        return findAllAliasesOfObject(obj.getId());
    }

    /**
     * finds all aliases of the specified object
     * 
     * @param objectId id  of the object
     * @return list of all aliases of the object
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AliasTable> findAllAliasesOfObject(Long objectId) {
                return currentSession().createQuery(
                "select distinct al "
              + "from ObjectTable as obj "
                        + "inner join obj.rootObject as root "
                        + "inner join root.rootOfObjects as rootOf "
                        + "inner join rootOf.aliases as al "
              + "where obj.id = :objId"
        )        
        .setParameter("objId", objectId)
        .list();
    }

 
    
}
