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
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Vaclav Pernicka
 * @author Petr Fanta
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

    private Query findAllByAliasFullTextQuery(String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query query = builder
                .phrase()
                .onField("aliases.alias")
                .sentence(pattern)
                .createQuery();

        return fullTextSession.createFullTextQuery(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasFullText(String pattern) {
        return findAllByAliasFullTextQuery(pattern).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasFullText(String pattern, int firstResult, int pageSize) {
        Query hq = findAllByAliasFullTextQuery(pattern);
        hq.setFirstResult(firstResult);
        hq.setMaxResults(pageSize);

        return hq.list();
    }

    private Query findAllByObjTypeAndAliasFullTextQuery(long objectTypeId, String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query fullTextQuery = builder
                .phrase()
                .onField("aliases.alias")
                .sentence(pattern)
                .createQuery();

        org.apache.lucene.search.Query typeQuery = builder
                .keyword()
                .onField("objectType.id")
                .matching(objectTypeId)
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
    public List<ObjectTable> findAllByObjTypeAndAliasFullText(long objectTypeId, String pattern) {
        return findAllByObjTypeAndAliasFullTextQuery(objectTypeId, pattern).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByObjTypeAndAliasFullText(long objectTypeId, String pattern, int firstResult, int pageSize) {
        Query hq = findAllByObjTypeAndAliasFullTextQuery(objectTypeId, pattern);
        hq.setFirstResult(firstResult);
        hq.setMaxResults(pageSize);

        return hq.list();
    }

    private Query findAllByObjectTypeAndAliasSubStrQuery(long objectTypeId, String aliasSubstring) {
        Query hq = currentSession().createQuery(
                "select distinct obj from ObjectTable as obj "
                        + "inner join obj.objectType as type "
                        + "inner join obj.aliases as al "
                        + "where lower(al.alias) like lower(:pattern) and type.id = :objectTypeId"
        );
        hq.setParameter("pattern", DAOUtils.getLikeSubstring(aliasSubstring));
        hq.setParameter("objectTypeId", objectTypeId);
        return hq;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByObjectTypeAndAliasSubStr(long objectTypeId, String aliasSubstring) {
        return findAllByObjectTypeAndAliasSubStrQuery(objectTypeId, aliasSubstring).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByObjectTypeAndAliasSubStr(long objectTypeId, String aliasSubstring, int firstResult, int pageSize) {
        Query hq = findAllByObjectTypeAndAliasSubStrQuery(objectTypeId, aliasSubstring);
        hq.setFirstResult(firstResult);
        hq.setMaxResults(pageSize);

        return hq.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByObjectType(long objectTypeId) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_OBJECT_TYPE_ID), "objType", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("objType", ObjectTypeTable.PROPERTY_NAME_ID), objectTypeId))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByObjectType(long objectTypeId, int firstResult, int pageSize) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_OBJECT_TYPE_ID), "objType", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("objType", ObjectTypeTable.PROPERTY_NAME_ID), objectTypeId))
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .list();
    }

    @Override
    public List<ObjectTable> findAllByObjectType(ObjectTypeTable objectType) {
        return findAllByObjectType(objectType.getId());
    }

    @Override
    public List<ObjectTable> findAllByObjectType(ObjectTypeTable type, int firstResult, int pageSize) {
        return findAllByObjectType(type.getId(), firstResult, pageSize);
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
    public List<ObjectTable> findAllByAliasEqualTo(String alias, int firstResult, int pageSize) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_ALIASES_ID), "alias", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("alias", AliasTable.PROPERTY_NAME_ALIAS), alias))
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .list();
    }

    private Query findAllByAliasSubstringQuery(String aliasSubstring) {

        Query hq = currentSession().createQuery(
                "select distinct obj from ObjectTable as obj "
                        + "inner join obj.aliases as al "
                        + "where lower(al.alias) like lower(:pattern)"
        );
        hq.setParameter("pattern", DAOUtils.getLikeSubstring(aliasSubstring));
        return hq;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasSubstring(String aliasSubstring) {
        return findAllByAliasSubstringQuery(aliasSubstring).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasSubstring(String aliasSubstring, int firstResult, int pageSize) {
        Query hq = findAllByAliasSubstringQuery(aliasSubstring);
        hq.setFirstResult(firstResult);
        hq.setMaxResults(pageSize);

        return hq.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByDocumentOccurrence(long documentId) {
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
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByDocumentOccurrence(long documentId, int firstResult, int pageSize) {
        return findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_ALIASES_ID), "alias", JoinType.INNER_JOIN)
                .createAlias(DAOUtils.getAliasPropertyName("alias", AliasTable.PROPERTY_NAME_OCCURRENCES),
                        "aliasOccurrence", JoinType.INNER_JOIN)
                .createAlias(DAOUtils.getAliasPropertyName("aliasOccurrence", AliasOccurrenceTable.PROPERTY_NAME_DOCUMENT),
                        "document", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("document", DocumentTable.PROPERTY_NAME_ID),
                        documentId))
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .list();
    }

    @Override
    public List<ObjectTable> findAllByDocumentOccurrence(DocumentTable document) {
        return findAllByDocumentOccurrence(document.getId());
    }

    @Override
    public List<ObjectTable> findAllByDocumentOccurrence(DocumentTable document, int firstResult, int pageSize) {
        return findAllByDocumentOccurrence(document.getId(), firstResult, pageSize);
    }
}
