package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.ResultPagination;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
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
public class DocumentTableDAO extends AbstractHibernateDAO<DocumentTable, Long> implements IDocumentTableDAO {

    
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
    public ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithObjectWithPagination(ObjectTable obj, int firstResult, int maxResults) {
        return findAllDocumentsWithObjectWithPagination(obj.getId(), firstResult, maxResults);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithObjectWithPagination(long objectId, int firstResult, int maxResults) {
        Query query = findAllDocumentsWithObjectQuery(objectId);
        int count = query.list().size();
        List<Object[]> untypedResults = addPagination(query, firstResult, maxResults).list();
        List<Pair<DocumentTable, Integer>> results = untypedResults.stream()
                .map(result -> new Pair<>((DocumentTable) result[0], ((Long) result[1]).intValue()))
                .collect(Collectors.toList());

        return new ResultPagination<>(firstResult, maxResults, results, count);
    }

    private FullTextQuery findAllDocumentsWithObjectByFullTextQuery(long objectId, String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query queryFullText = builder
                .phrase()
                .onField("text")
                .sentence(pattern)
                .createQuery();

        org.apache.lucene.search.Query queryObject = builder
                .keyword()
                .onField("aliasOccurrences.alias.object.id") //FIXME!!
                .matching(objectId)
                .createQuery();

        org.apache.lucene.search.Query query = builder
                .bool()
                .must(queryFullText)
                .must(queryObject)
                .createQuery();

        return fullTextSession.createFullTextQuery(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Pair<DocumentTable, Integer>> findAllDocumentsWithObjectByFullText(long objectId, String pattern) {
        List<DocumentTable> documents = findAllDocumentsWithObjectByFullTextQuery(objectId, pattern).list();
        List<Pair<DocumentTable, Integer>> documentCountPairs = documents.stream()
                .map(x -> new Pair<>(x, getNumberOfObjectOccurrencesInDocument(x.getId(), objectId)))
                .collect(Collectors.toList());

        return documentCountPairs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithObjectByFullTextWithPagination(long objectId, String pattern, int firstResult, int maxResults) {
        FullTextQuery query = findAllDocumentsWithObjectByFullTextQuery(objectId, pattern);
        List<DocumentTable> documents = addPagination(query, firstResult, maxResults).list();
        int count = query.getResultSize();

        List<Pair<DocumentTable, Integer>> documentCountPairs = documents.stream()
                .map(x -> new Pair<>(x, getNumberOfObjectOccurrencesInDocument(x.getId(), objectId)))
                .collect(Collectors.toList());

        return new ResultPagination<>(firstResult, maxResults, documentCountPairs, count);
    }

    @Override
    public List<Pair<DocumentTable, Integer>> findAllDocumentsWithRelation(RelationTable relation) {
        return findAllDocumentsWithRelation(relation.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Pair<DocumentTable, Integer>> findAllDocumentsWithRelation(long relationId) {
        List<Object[]> result = findAllDocumentsWithRelationQuery(relationId).list();
        return result.stream()
                .map(x ->  new Pair<>((DocumentTable) x[0], ((Long)x[1]).intValue()))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithRelationWithPagination(long relationId, int firstResult, int maxResults) {
        Query query = findAllDocumentsWithRelationQuery(relationId);
        int count = query.list().size();
        List<Object[]> untypedResults = addPagination(query, firstResult, maxResults).list();
        List<Pair<DocumentTable, Integer>> results = untypedResults.stream()
                .map(x -> new Pair<>((DocumentTable) x[0], ((Long) x[1]).intValue()))
                .collect(Collectors.toList());

        return new ResultPagination<>(firstResult, maxResults, results,count);
    }

    private FullTextQuery findAllDocumentsWithRelationByFullTextQuery(long relationId, String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query queryFullText = builder
                .phrase()
                .onField("text")
                .sentence(pattern)
                .createQuery();

        org.apache.lucene.search.Query queryObject = builder
                .keyword()
                .onField("relationOccurrences.relation.id")
                .matching(relationId)
                .createQuery();

        org.apache.lucene.search.Query query = builder
                .bool()
                .must(queryFullText)
                .must(queryObject)
                .createQuery();

        return fullTextSession.createFullTextQuery(query);
    }

    @Override
    public List<Pair<DocumentTable, Integer>> findAllDocumentsWithRelationByFullText(long relationId, String pattern) {
        @SuppressWarnings("unchecked")
        List<DocumentTable> documents = findAllDocumentsWithRelationByFullTextQuery(relationId, pattern).list();

        List<Pair<DocumentTable, Integer>> documentCountPairs = documents.stream()
                .map(x -> new Pair<>(x, getNumberOfRelationOccurrencesInDocument(x.getId(), relationId)))
                .collect(Collectors.toList());

        return documentCountPairs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<Pair<DocumentTable, Integer>> findAllDocumentsWithRelationByFullTextWithPagination(long relationId, String pattern, int firstResult, int maxResults) {
        FullTextQuery query = findAllDocumentsWithRelationByFullTextQuery(relationId, pattern);
        List<DocumentTable> documents = addPagination(query, firstResult, maxResults).list();
        int count = query.getResultSize();

        List<Pair<DocumentTable, Integer>> documentCountPairs = documents.stream()
                .map(x -> new Pair<>(x, getNumberOfRelationOccurrencesInDocument(x.getId(), relationId)))
                .collect(Collectors.toList());

        return new ResultPagination<>(firstResult, maxResults, documentCountPairs, count);
    }
    
    private FullTextQuery findAllDocumentsByFullTextQuery(String pattern) {
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
    public ResultPagination<DocumentTable> findAllDocumentsByFullTextWithPagination(String pattern, int firstResult, int maxResults) {
        FullTextQuery query = findAllDocumentsByFullTextQuery(pattern);
        List<DocumentTable> results = addPagination(query, firstResult, maxResults).list();
        int count = query.getResultSize();

        return new ResultPagination<>(firstResult, maxResults, results, count);
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
    public ResultPagination<DocumentTable> findAllProcessedDocumentsWithPagination(boolean processed, int firstResult, int maxResults) {
        Criteria criteria = findAllCriteria();

        if (processed) {
            criteria.add(Restrictions.isNotNull(DocumentTable.PROPERTY_NAME_PROCESSED));
        } else {
            criteria.add(Restrictions.isNull(DocumentTable.PROPERTY_NAME_PROCESSED));
        }
        int count = criteria.list().size();

        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);

        List<DocumentTable> results = criteria.list();

        return new ResultPagination<>(firstResult, maxResults, results, count);
    }

    public FullTextQuery findAllProcessedDocumentsByFullTextQuery(boolean processed, String pattern) {
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
                .bool()
                .must(queryFullText)
                .must(queryProcessed)
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
    public ResultPagination<DocumentTable> findAllProcessedDocumentsByFullTextWithPagination(boolean processed, String pattern, int firstResult, int maxResults) {
        FullTextQuery query = findAllProcessedDocumentsByFullTextQuery(processed, pattern);
        List<DocumentTable> results = addPagination(query, firstResult, maxResults).list();
        int count = query.getResultSize();

        return new ResultPagination<>(firstResult, maxResults, results, count);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllSinceGlobalVersion(long version) {
        return findAllCriteria()
                .add(Restrictions.ge(DocumentTable.PROPERTY_NAME_GLOBAL_VERSION, version))
                .list();
                
    }
    
    private Query findAllDocumentsWithObjectQuery(long objectId) {
        Query hq = currentSession().createQuery(
                "select doc, count(occ) as num "
                + "from DocumentTable as doc "
                        + "inner join doc.aliasOccurrences as occ "
                        + "inner join occ.alias as alias "
                        + "inner join alias.object as obj "
                        + "inner join obj.rootObject as root "
                + "where root.id = :objectId "
                + "group by doc.id "
                + "order by num desc"
        );
        hq.setParameter("objectId", objectId);

        return hq;
    }
    
    private int getNumberOfObjectOccurrencesInDocument(long documentId, long objectId) {
        Query hq = currentSession().createQuery(
                "select count(*) from DocumentTable as doc "
                        + "inner join doc.aliasOccurrences as occ "
                        + "inner join occ.alias as alias "
                        + "inner join alias.object as obj "
                        + "inner join obj.rootObject as root "
                + "where doc.id = :documentId and root.id = :objectId "
        );
        hq.setParameter("documentId", documentId);
        hq.setParameter("objectId", objectId);

        return ((Long)hq.iterate().next()).intValue();
    }
    
    private int getNumberOfRelationOccurrencesInDocument(long documentId, long relationId) {
        Query hq = currentSession().createQuery(
                "select count(*) from DocumentTable as doc "
                        + "inner join doc.relationOccurrences as occ "
                        + "inner join occ.relation rel "
                        + "where rel.id = :relationId and doc.id = :documentId"
        );
        hq.setParameter("documentId", documentId);
        hq.setParameter("relationId", relationId);

        return ((Long)hq.iterate().next()).intValue();
    }

    private Query findAllDocumentsWithRelationQuery(long relationId) {
        Query hq = currentSession().createQuery(
                "select doc, count(occ) as num from DocumentTable as doc "
                        + "inner join doc.relationOccurrences as occ "
                        + "inner join occ.relation rel "
                        + "where rel.id = :relationId "
                        + "group by doc.id "
                        + "order by num desc"
        );
        hq.setParameter("relationId", relationId);

        return hq;
    }
    
}
