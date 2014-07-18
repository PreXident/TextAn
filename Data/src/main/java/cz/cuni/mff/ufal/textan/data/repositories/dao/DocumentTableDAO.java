package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.DAOUtils;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationOccurrenceTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
@Repository
@Transactional
public class DocumentTableDAO extends AbstractHibernateDAO<DocumentTable, Long> implements IDocumentTableDAO{

    private Query findAllDocumentsWithObjectQuery(long objectId) {
        Query hq = currentSession().createQuery(
                "select doc, count(occ) as num from DocumentTable as doc "
                        + "inner join doc.aliasOccurrences as occ "
                        + "inner join occ.alias as alias "
                        + "inner join alias.object as obj "
                        +"where obj.id = :objectId "
                + "group by doc.id "
                + "order by num desc"
        );
        hq.setParameter("objectId", objectId);

        return hq;
    }

    @Override
    public List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(ObjectTable obj) {
        return findAllDocumentsWithObject(obj.getId());

    }

    @Override
    public List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(long objectId) {
        @SuppressWarnings("unchecked")
        List<Object[]> results = findAllDocumentsWithObjectQuery(objectId).list();
        return results.stream()
                .map(result -> new Pair<>((DocumentTable) result[0], ((Long)result[1]).intValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(ObjectTable obj, int firstResult, int maxResults) {
        return findAllDocumentsWithObject(obj.getId(), firstResult, maxResults);
    }

    @Override
    public List<Pair<DocumentTable, Integer>> findAllDocumentsWithObject(long objectId, int firstResult, int maxResults) {
        @SuppressWarnings("unchecked")
        List<Object[]> results = addPagination(findAllDocumentsWithObjectQuery(objectId), firstResult, maxResults).list();
        return results.stream()
                .map(result -> new Pair<>((DocumentTable) result[0], ((Long)result[1]).intValue()))
                .collect(Collectors.toList());
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
    @SuppressWarnings("unchecked")
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
    
    private Query findAllDocumentsByFullTextQuery(String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query query = builder
                .phrase()
                .onField("text")
                .sentence(pattern)
                .createQuery();

        return fullTextSession.createFullTextQuery(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllDocumentsByFullText(String pattern) {
        return findAllDocumentsByFullTextQuery(pattern).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllDocumentsByFullText(String pattern, int firstResult, int maxResults) {
        return addPagination(findAllDocumentsByFullTextQuery(pattern), firstResult, maxResults).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllProcessedDocuments(boolean processed) {
        Criteria criteria = findAllCriteria();
        if (processed) {
            criteria.add(Restrictions.isNotNull(DocumentTable.PROPERTY_NAME_PROCESSED));
        } else {
            criteria.add(Restrictions.isNull(DocumentTable.PROPERTY_NAME_PROCESSED));
        }

        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllProcessedDocuments(boolean processed, int firstResult, int maxResults) {
        Criteria criteria = findAllCriteria();
        if (processed) {
            criteria.add(Restrictions.isNotNull(DocumentTable.PROPERTY_NAME_PROCESSED));
        } else {
            criteria.add(Restrictions.isNull(DocumentTable.PROPERTY_NAME_PROCESSED));
        }
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);

        return criteria.list();
    }

    public Query findAllProcessedDocumentsByFullTextQuery(boolean processed, String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query queryFullText = builder
                .phrase()
                .onField("text")
                .sentence(pattern)
                .createQuery();

        org.apache.lucene.search.Query queryProcessed = builder
                .keyword()
                .onField("processedBool")
                .matching(processed)
                .createQuery();

        org.apache.lucene.search.Query query = builder
                .keyword()
                .onField("processedBool")
                .matching(processed)
                .createQuery();

        return fullTextSession.createFullTextQuery(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllProcessedDocumentsByFullText(boolean processed, String pattern) {
        return findAllProcessedDocumentsByFullTextQuery(processed, pattern).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllProcessedDocumentsByFullText(boolean processed, String pattern, int firstResult, int maxResults) {
        return addPagination(findAllProcessedDocumentsByFullTextQuery(processed, pattern), firstResult, maxResults).list();
    }

}
