/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.DAOUtils;
import cz.cuni.mff.ufal.textan.data.tables.*;
import org.hibernate.Query;
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
public class DocumentTableDAO extends AbstractHibernateDAO<DocumentTable, Long> implements IDocumentTableDAO{

    private Query findAllDocumentsWithObjectQuery(long objectId) {
        Query hq = currentSession().createQuery(
                "select distinct doc from DocumentTable as doc "
                        + "inner join doc.aliasOccurrences as occ "
                        + "inner join occ.alias as alias "
                        + "inner join alias.object as obj "
                        +"where obj.id = :objectId"
        );
        hq.setParameter("objectId", objectId);

        return hq;
    }

    @Override
    public List<DocumentTable> findAllDocumentsWithObject(ObjectTable obj) {
        return findAllDocumentsWithObject(obj.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllDocumentsWithObject(long objectId) {
        return findAllDocumentsWithObjectQuery(objectId).list();
    }

    @Override
    public List<DocumentTable> findAllDocumentsWithObject(ObjectTable obj, int firstResult, int maxResults) {
        return findAllDocumentsWithObject(obj.getId(), firstResult, maxResults);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllDocumentsWithObject(long objectId, int firstResult, int maxResults) {
        Query hq = findAllDocumentsWithObjectQuery(objectId);
        hq.setFirstResult(firstResult);
        hq.setMaxResults(maxResults);

        return hq.list();
    }

    @Override
    public List<DocumentTable> findAllDocumentsWithRelation(RelationTable relation) {
        return findAllDocumentsWithRelation(relation.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllDocumentsWithRelation(Long relationId) {
        return findAllCriteria()
            .createAlias(getAliasPropertyName(DocumentTable.PROPERTY_NAME_RELATION_OCCURRENCES), "relOccurrence", JoinType.INNER_JOIN)
            .createAlias(DAOUtils.getAliasPropertyName("relOccurrence", RelationOccurrenceTable.PROPERTY_NAME_RELATION),
                         "relation", JoinType.INNER_JOIN)
            .add(Restrictions.eq(DAOUtils.getAliasPropertyName("relation", RelationTable.PROPERTY_NAME_ID),
                                 relationId))
            .list();
    }
    
}
