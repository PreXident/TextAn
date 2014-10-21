/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;


import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.repositories.common.DAOUtils;
import cz.cuni.mff.ufal.textan.data.repositories.common.ResultPagination;
import cz.cuni.mff.ufal.textan.data.tables.*;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.ResultTransformer;

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

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasFullText(String pattern) {
        return findAllByAliasFullTextQuery(pattern).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<ObjectTable> findAllByAliasFullTextWithPagination(String pattern, int firstResult, int pageSize) {
        FullTextQuery query = findAllByAliasFullTextQuery(pattern);
        List<ObjectTable> results = addPagination(query, firstResult, pageSize).list();
        int count = query.getResultSize();

        return new ResultPagination<>(firstResult, pageSize, results, count);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByObjTypeAndAliasFullText(long objectTypeId, String pattern) {
        return findAllByObjTypeAndAliasFullTextQuery(objectTypeId, pattern).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<ObjectTable> findAllByObjTypeAndAliasFullTextWithPagination(long objectTypeId, String pattern, int firstResult, int pageSize) {
        FullTextQuery query = findAllByObjTypeAndAliasFullTextQuery(objectTypeId, pattern);
        List<ObjectTable> results = addPagination(query, firstResult, pageSize).list();
        int count = query.getResultSize();

        return new ResultPagination<>(firstResult, pageSize, results, count);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByObjectTypeAndAliasSubStr(long objectTypeId, String aliasSubstring) {
        return findAllByObjectTypeAndAliasSubStrQuery(objectTypeId, aliasSubstring).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultPagination<ObjectTable> findAllByObjectTypeAndAliasSubStrWithPagination(long objectTypeId, String aliasSubstring, int firstResult, int pageSize) {
        Query query = findAllByObjectTypeAndAliasSubStrQuery(objectTypeId, aliasSubstring);
        int count = query.list().size();
        List<ObjectTable> results = addPagination(query, firstResult, pageSize).list();

        return new ResultPagination<>(firstResult, pageSize, results, count);
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
    public ResultPagination<ObjectTable> findAllByObjectTypeWithPagination(long objectTypeId, int firstResult, int pageSize) {
        Criteria criteria = findAllCriteria()
                .createAlias(getAliasPropertyName(ObjectTable.PROPERTY_NAME_OBJECT_TYPE_ID), "objType", JoinType.INNER_JOIN)
                .add(Restrictions.eq(DAOUtils.getAliasPropertyName("objType", ObjectTypeTable.PROPERTY_NAME_ID), objectTypeId));
        int count = criteria.list().size();

        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(pageSize);

        List<ObjectTable> results = criteria.list();

        return new ResultPagination<>(firstResult, pageSize, results, count);
    }

    @Override
    public List<ObjectTable> findAllByObjectType(ObjectTypeTable objectType) {
        return findAllByObjectType(objectType.getId());
    }

    @Override
    public ResultPagination<ObjectTable> findAllByObjectTypeWithPagination(ObjectTypeTable type, int firstResult, int pageSize) {
        return findAllByObjectTypeWithPagination(type.getId(), firstResult, pageSize);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasEqualTo(String alias) {
        return findAllByAliasQuery(alias)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasEqualTo(String alias, int firstResult, int pageSize) {
        return findAllByAliasQuery(alias)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasSubstring(String aliasSubstring) {
        return findAllByAliasSubstringQuery(aliasSubstring).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByAliasSubstring(String aliasSubstring, int firstResult, int pageSize) {
        return addPagination(findAllByAliasSubstringQuery(aliasSubstring), firstResult, pageSize).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByDocumentOccurrence(long documentId) {
        return findAllByDocumentQuery(documentId)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllByDocumentOccurrence(long documentId, int firstResult, int pageSize) {
        return findAllByDocumentQuery(documentId)
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

    @Override
    @SuppressWarnings("unchecked")
    public List<ObjectTable> findAllSinceGlobalVersion(long version) {
        return findAllCriteria()
                .add(Restrictions.ge(ObjectTable.PROPERTY_NAME_GLOBAL_VERSION, version))
                .list();
                
    }
    
    @Override
    public List<Pair<ObjectTable, RelationTable>> getNeighbors(ObjectTable object) {
        return getNeighbors(object.getId());
    }

    @Override
    @SuppressWarnings({"serial", "rawtypes", "unchecked"})
    public List<Pair<ObjectTable, RelationTable>> getNeighbors(long objectId) {
        return currentSession().createQuery(
                "select obj2, rel"
                + " from ObjectTable obj"
                + "     inner join obj.relations inRel"
                + "     inner join inRel.relation rel"
                + "     inner join rel.objectsInRelation inRel2"
                + "     inner join inRel2.object obj2"
                + " where obj.id = :pId"
        )
                .setParameter("pId", objectId)
                .setResultTransformer(new ResultTransformer() {

                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        return new Pair<> 
                                ((ObjectTable)tuple[0], 
                                 (RelationTable)tuple[1]);
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                })
                .list();
    }

    @Override
    protected Criteria findAllCriteria() {
        return super.findAllCriteria()
                .add(Restrictions.eqProperty(ObjectTable.PROPERTY_NAME_ID,
                                             ObjectTable.PROPERTY_NAME_ROOT_OBJECT_ID));
    }

    private FullTextQuery findAllByAliasFullTextQuery(String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query query = builder
                .phrase()
                .onField("rootOfObjects.aliases.alias")
                .sentence(pattern)
                .createQuery();

        return fullTextSession.createFullTextQuery(query);
    }
    
    private FullTextQuery findAllByObjTypeAndAliasFullTextQuery(long objectTypeId, String pattern) {
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        QueryBuilder builder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(type).get();
        org.apache.lucene.search.Query fullTextQuery = builder
                .phrase()
                .onField("rootOfObjects.aliases.alias")
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
     
    private Query findAllByObjectTypeAndAliasSubStrQuery(long objectTypeId, String aliasSubstring) {
        Query hq = currentSession().createQuery(
                "select distinct obj "
              + "from ObjectTable as obj "
                        + "inner join obj.rootOfObjects as rootOf "
                        + "inner join obj.objectType as type "
                        + "inner join rootOf.aliases as al "
              + "where lower(al.alias) like lower(:pattern) and type.id = :objectTypeId "
                        + "and obj.rootObject = obj.id"
        );
        hq.setParameter("pattern", DAOUtils.getLikeSubstring(aliasSubstring));
        hq.setParameter("objectTypeId", objectTypeId);
        return hq;
    }

    private Query findAllByAliasSubstringQuery(String aliasSubstring) {

        Query hq = currentSession().createQuery(
                "select distinct obj "
                        + "from ObjectTable as obj "
                            + "inner join obj.rootOfObjects as rootOf "
                            + "inner join rootOf.aliases as al "
                        + "where lower(al.alias) like lower(:pattern)"
                            + "and obj.rootObject = obj.id"           // this is root
        );
        hq.setParameter("pattern", DAOUtils.getLikeSubstring(aliasSubstring));
        return hq;
    }
    
    private Query findAllByAliasQuery(String aliasSubstring) {
        Query hq = currentSession().createQuery(
                "select distinct obj "
              + "from ObjectTable as obj "
                        + "inner join obj.rootOfObjects as rootOf "
                        + "inner join rootOf.aliases as al "
              + "where lower(al.alias) = lower(:pattern) "
                        + "and obj.rootObject = obj.id"           // this is root
        );
        
        hq.setParameter("pattern", aliasSubstring);
        return hq;
        
    }
    
    private Query findAllByDocumentQuery(Long documentId) {
        return currentSession().createQuery(
                "select distinct obj "
              + "from ObjectTable as obj "
                        + "inner join obj.rootOfObjects as rootOf "
                        + "inner join rootOf.aliases as al "
                        + "inner join al.occurrences as occ "
                        + "inner join occ.document as doc "
              + "where doc.id = :docId "
                        + "and obj.rootObject = obj.id"           // this is root
        )
        .setParameter("docId", documentId);
        
    }


}
