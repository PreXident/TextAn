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

    @Override
    public List<DocumentTable> findAllDocumentsWithObject(ObjectTable obj) {
        return findAllDocumentsWithObject(obj.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllDocumentsWithObject(Long objectId) {

        Query hq = currentSession().createQuery(
                "select distinct doc from DocumentTable as doc "
                    + "inner join doc.aliasOccurrences as occ "
                    + "inner join occ.alias as alias "
                    + "inner join alias.object as obj "
                +"where obj.id = :objectId"
        );
        hq.setParameter("objectId", objectId);

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

    @Override
    public List<DocumentTable> findAllProcessed(boolean processed) {
        if (processed)
            return findAllCriteria()
                    .add(Restrictions.isNotNull(DocumentTable.PROPERTY_NAME_PROCESSED))
                    .list();
        else
            return findAllCriteria()
                    .add(Restrictions.isNull(DocumentTable.PROPERTY_NAME_PROCESSED))
                    .list();
            
    }
    
}
