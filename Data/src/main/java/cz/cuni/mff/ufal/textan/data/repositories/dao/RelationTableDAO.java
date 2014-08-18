package cz.cuni.mff.ufal.textan.data.repositories.dao;


import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.DAOUtils;
import cz.cuni.mff.ufal.textan.data.repositories.common.ResultPagination;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationOccurrenceTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
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
public class RelationTableDAO extends AbstractHibernateDAO<RelationTable, Long> implements IRelationTableDAO {

    /**
     *  constructor
     */
    public RelationTableDAO() {
        super(RelationTable.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByRelationType(long relationTypeId) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_RELATION_TYPE_ID), "objType", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("objType", RelationTypeTable.PROPERTY_NAME_ID), relationTypeId))
                .list();
    }
    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<RelationTable> findAllByRelationTypeWithPagination(long relationTypeId, int firstResult, int pageSize) {
        Criteria criteria = findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_RELATION_TYPE_ID), "objType", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("objType", RelationTypeTable.PROPERTY_NAME_ID), relationTypeId));
        int count = criteria.list().size();

        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(pageSize);

        List<RelationTable> results = criteria.list();

        return new ResultPagination<>(firstResult, pageSize, results, count);
    }

    @Override
    public List<RelationTable> findAllByRelationType(RelationTypeTable relationType) {
        return findAllByRelationType(relationType.getId());
    }
    @Override
    public ResultPagination<RelationTable> findAllByRelationTypeWithPagination(RelationTypeTable relationType, int firstResult, int pageSize) {
        return findAllByRelationTypeWithPagination(relationType.getId(), firstResult, pageSize);
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
    public List<RelationTable> findAllByAliasEqualTo(String alias, int firstResult, int pageSize) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_OCCURRENCES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("alias", RelationOccurrenceTable.PROPERTY_NAME_ANCHOR), alias))
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
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
    public List<RelationTable> findAllByAliasSubstring(String aliasSubstring, int firstResult, int pageSize) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_OCCURRENCES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.like(DAOUtils.getAliasPropertyName("alias", RelationOccurrenceTable.PROPERTY_NAME_ANCHOR),
                                       DAOUtils.getLikeSubstring(aliasSubstring)))
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByDocumentOccurrence(long documentId) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_OCCURRENCES_ID), "alias", JoinType.INNER_JOIN)
                .createAlias(DAOUtils.getAliasPropertyName("alias", RelationOccurrenceTable.PROPERTY_NAME_DOCUMENT),
                             "document", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("document", DocumentTable.PROPERTY_NAME_ID),
                                     documentId))
                .list();
    }
    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByDocumentOccurrence(long documentId, int firstResult, int pageSize) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(RelationTable.PROPERTY_NAME_OCCURRENCES_ID), "alias", JoinType.INNER_JOIN)
                .createAlias(DAOUtils.getAliasPropertyName("alias", RelationOccurrenceTable.PROPERTY_NAME_DOCUMENT),
                             "document", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("document", DocumentTable.PROPERTY_NAME_ID),
                                     documentId))
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .list();
    }

    @Override
    public List<RelationTable> findAllByDocumentOccurrence(DocumentTable document) {
        return findAllByDocumentOccurrence(document.getId());
    }
    @Override
    public List<RelationTable> findAllByDocumentOccurrence(DocumentTable document, int firstResult, int pageSize) {
        return findAllByDocumentOccurrence(document.getId(), firstResult, pageSize);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllSinceGlobalVersion(long version) {
                return findAllCriteria()
                .add(Restrictions.ge(RelationTable.PROPERTY_NAME_GLOBAL_VERSION, version))
                .list();
    }

    private FullTextQuery findAllByRelTypeAndAnchorFullTextQuery(long relationTypeId, String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query fullTextQuery = builder
                .phrase()
                .onField("occurrences.anchor")
                .sentence(pattern)
                .createQuery();

        org.apache.lucene.search.Query typeQuery = builder
                .keyword()
                .onField("relationType.id")
                .matching(relationTypeId)
                .createQuery();

        org.apache.lucene.search.Query query = builder
                .bool()
                .must(fullTextQuery)
                .must(typeQuery)
                .createQuery();

        return fullTextSession.createFullTextQuery(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByRelTypeAndAnchorFullText(long relationTypeId, String anchorFilter) {
        return findAllByRelTypeAndAnchorFullTextQuery(relationTypeId, anchorFilter).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<RelationTable> findAllByRelTypeAndAnchorFullTextWithPagination(long relationTypeId, String anchorFilter, int firstResult, int maxResults) {
        FullTextQuery query = findAllByRelTypeAndAnchorFullTextQuery(relationTypeId, anchorFilter);
        List<RelationTable> results = addPagination(query, firstResult, maxResults).list();
        int count = query.getResultSize();

        return new ResultPagination<>(firstResult, maxResults, results, count);
    }

    private FullTextQuery findAllByAnchorFullTextQuery(String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query query = builder
                .phrase()
                .onField("occurrences.anchor")
                .sentence(pattern)
                .createQuery();

        return fullTextSession.createFullTextQuery(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelationTable> findAllByAnchorFullText(String anchorFilter) {
        return findAllByAnchorFullTextQuery(anchorFilter).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<RelationTable> findAllByAnchorFullTextWithPagination(String anchorFilter, int firstResult, int maxResults) {
        FullTextQuery query = findAllByAnchorFullTextQuery(anchorFilter);
        List<RelationTable> results = addPagination(query, firstResult, maxResults).list();
        int count = query.getResultSize();

        return new ResultPagination<>(firstResult, maxResults, results, count);
    }
}
