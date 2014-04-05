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
import java.util.List;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Václav Pernička
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
    public List<AliasTable> findAllAliasesOfObject(Long objectId) {
                return findAllCriteria()
                .createAlias(getAliasPropertyName(AliasTable.PROPERTY_NAME_OBJECT_ID), "obj", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("obj", ObjectTable.PROPERTY_NAME_ID), objectId))
                .list();
    }

 
    
}
