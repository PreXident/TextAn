/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;


import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.DAOUtils;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationOccurrenceTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Václav Pernička
 */
@Repository
@Transactional
public class RelationTableDAO extends AbstractHibernateDAO<RelationTable, Long> implements IRelationTableDAO {

    /**
     *  constructor
     */
    public RelationTableDAO() {
        super(RelationTable.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByRelationType(Long relationTypeId) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_RELATION_TYPE_ID), "objType", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("objType", RelationTypeTable.PROPERTY_NAME_ID), relationTypeId))
                .list();
    }

    @Override
    public List<RelationTable> findAllByRelationType(RelationTypeTable relationType) {
        return findAllByRelationType(relationType.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByAliasEqualTo(String alias) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_OCCURRENCES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("alias", RelationOccurrenceTable.PROPERTY_NAME_ANCHOR), alias))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByAliasSubstring(String aliasSubstring) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_OCCURRENCES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.like(DAOUtils.getAliasPropertyName("alias", RelationOccurrenceTable.PROPERTY_NAME_ANCHOR),
                                       DAOUtils.getLikeSubstring(aliasSubstring)))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByDocumentOccurrence(Long documentId) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_OCCURRENCES_ID), "alias", JoinType.INNER_JOIN)
                .createAlias(DAOUtils.getAliasPropertyName("alias", RelationOccurrenceTable.PROPERTY_NAME_DOCUMENT),
                             "document", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("document", DocumentTable.PROPERTY_NAME_ID),
                                     documentId))
                .list();
    }

    @Override
    public List<RelationTable> findAllByDocumentOccurrence(DocumentTable document) {
        return findAllByDocumentOccurrence(document.getId());
    }
}
