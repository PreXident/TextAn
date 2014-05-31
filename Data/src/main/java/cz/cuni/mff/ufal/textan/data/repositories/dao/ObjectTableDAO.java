/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;


import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.DAOUtils;
import cz.cuni.mff.ufal.textan.data.tables.*;
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
public class ObjectTableDAO extends AbstractHibernateDAO<ObjectTable, Long> implements IObjectTableDAO {

    /**
     * constructor
     */
    public ObjectTableDAO() {
        super(ObjectTable.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByObjectType(Long objectTypeId) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_OBJECT_TYPE_ID), "objType", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("objType", ObjectTypeTable.PROPERTY_NAME_ID), objectTypeId))
                .list();
    }

    @Override
    public List<ObjectTable> findAllByObjectType(ObjectTypeTable objectType) {
        return findAllByObjectType(objectType.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasEqualTo(String alias) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_ALIASES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("alias", AliasTable.PROPERTY_NAME_ALIAS), alias))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasSubstring(String aliasSubstring) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_ALIASES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.like(DAOUtils.getAliasPropertyName("alias", AliasTable.PROPERTY_NAME_ALIAS),
                                       DAOUtils.getLikeSubstring(aliasSubstring)))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByDocumentOccurrence(Long documentId) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_ALIASES_ID), "alias", JoinType.INNER_JOIN)
                .createAlias(DAOUtils.getAliasPropertyName("alias", AliasTable.PROPERTY_NAME_OCCURRENCES),
                             "aliasOccurrence", JoinType.INNER_JOIN)
                .createAlias(DAOUtils.getAliasPropertyName("aliasOccurrence", AliasOccurrenceTable.PROPERTY_NAME_DOCUMENT),
                             "document", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("document", DocumentTable.PROPERTY_NAME_ID),
                                     documentId))
                .list();
    }

    @Override
    public List<ObjectTable> findAllByDocumentOccurrence(DocumentTable document) {
        return findAllByDocumentOccurrence(document.getId());
    }
}
