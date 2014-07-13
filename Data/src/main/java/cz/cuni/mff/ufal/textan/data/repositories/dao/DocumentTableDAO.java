package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.DAOUtils;
import cz.cuni.mff.ufal.textan.data.tables.*;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        return addPagination(findAllDocumentsWithObjectQuery(objectId), firstResult, maxResults).list();
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
        return new ArrayList<>(); //TODO
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTable> findAllProcessedDocuments(boolean processed, int firstResult, int maxResults) {
        return new ArrayList<>(); //TODO
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
