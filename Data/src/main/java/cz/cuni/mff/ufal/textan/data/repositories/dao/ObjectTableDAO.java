/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;


import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.CommonOperations;
import cz.cuni.mff.ufal.textan.data.tables.AliasOccurrenceTable;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import java.util.List;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Václav Pernička
 */
@Repository
@Deprecated // TODO: Not tested yet
public class ObjectTableDAO extends AbstractHibernateDAO<ObjectTable, Long> {
    
    public ObjectTableDAO() {
        super(ObjectTable.class);
    }
    @Deprecated // not tested yet
    public List<ObjectTable> findAllByObjectType(Long objectTypeId) {
        return super.findAllByProperty(ObjectTable.PROPERTY_NAME_OBJECT_TYPE_ID, objectTypeId);
    }

    @Deprecated // not tested yet
    public List<ObjectTable> findAllByObjectType(ObjectTypeTable objectType) {
        return findAllByObjectType(objectType.getId());
    }

    @Deprecated // not tested yet
    public List<ObjectTable> findAllByAliasEqualTo(String alias) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_ALIASES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.eq(CommonOperations.getAliasPropertyName("alias", AliasTable.PROPERTY_NAME_ALIAS), alias))
                .list();
    }
    @Deprecated // not tested yet
    public List<ObjectTable> findAllByAliasSubstring(String aliasSubstring) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_ALIASES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.like(CommonOperations.getAliasPropertyName("alias", AliasTable.PROPERTY_NAME_ALIAS),
                                       CommonOperations.getLikeSubstring(aliasSubstring)))
                .list();
    }
    @Deprecated // not tested yet
    public List<ObjectTable> findAllByDocumentOccurrence(Long documentId) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_ALIASES_ID), "alias", JoinType.INNER_JOIN)
                .createAlias(CommonOperations.getAliasPropertyName("alias", AliasTable.PROPERTY_NAME_OCCURRENCES),
                             "aliasOccurrence", JoinType.INNER_JOIN)
                .createAlias(CommonOperations.getAliasPropertyName("aliasOccurrence", AliasOccurrenceTable.PROPERTY_NAME_DOCUMENT),
                             "document", JoinType.INNER_JOIN)
                .add(Restrictions.eq(CommonOperations.getAliasPropertyName("document", DocumentTable.PROPERTY_NAME_OCCURRENCES),
                                     documentId))
                .list();
    }
}
