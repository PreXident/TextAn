package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.common.ResultPagination;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.*;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A service which provides direct access into the database.
 *
 * @author Petr Fanta
 */
@Service
@Transactional
public class DirectDataAccessService {

    private final IDocumentTableDAO documentTableDAO;

    private final IObjectTypeTableDAO objectTypeTableDAO;
    private final IObjectTableDAO objectTableDAO;
    private final IAliasTableDAO aliasTableDAO;

    private final IRelationTypeTableDAO relationTypeTableDAO;
    private final IRelationTableDAO relationTableDAO;

    private final IInRelationTableDAO inRelationTableDAO;

    /**
     * Instantiates a new Direct data access service.
     * @param documentTableDAO the document table dAO
     * @param objectTypeTableDAO the object type table dAO
     * @param objectTableDAO the object table dAO
     * @param aliasTableDAO the alias table dAO
     * @param relationTypeTableDAO the relation type table dAO
     * @param relationTableDAO the relation table dAO
     * @param inRelationTableDAO the inRelationTableDAO
     */
    @Autowired
    public DirectDataAccessService(
            IDocumentTableDAO documentTableDAO,
            IObjectTypeTableDAO objectTypeTableDAO,
            IObjectTableDAO objectTableDAO,
            IAliasTableDAO aliasTableDAO, IRelationTypeTableDAO relationTypeTableDAO,
            IRelationTableDAO relationTableDAO, IInRelationTableDAO inRelationTableDAO) {

        this.documentTableDAO = documentTableDAO;
        this.objectTypeTableDAO = objectTypeTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.relationTypeTableDAO = relationTypeTableDAO;
        this.relationTableDAO = relationTableDAO;
        this.inRelationTableDAO = inRelationTableDAO;
    }

    /**
     * Adds a new document document into the system.
     *
     * @param text the text of the new document
     * @return the identifier of the new document
     */
    public long addDocument(String text) {

        DocumentTable documentTable = new DocumentTable(text);

        return documentTableDAO.add(documentTable);
    }

    /**
     * Gets the document with the given id from the system.
     *
     * @param documentId the document id
     * @return the document
     * @throws IdNotFoundException if no document with the given id exists
     */
    public Document getDocument(long documentId) throws IdNotFoundException{

        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        }

        return Document.fromDocumentTable(documentTable);
    }

    /**
     * Gets all documents from the system.
     *
     * @return the documents
     */
    public List<Document> getDocuments() {
        return documentTableDAO.findAll().stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Document::fromDocumentTable)
                .collect(Collectors.toList());
    }

    public Pair<List<Document>,Integer> getFilteredDocuments(String pattern, ProcessedFilter processedFilter, int firstResult, int maxResults) {

        ResultPagination<DocumentTable> documentTables;

        if ((pattern != null && !pattern.isEmpty()) && (processedFilter != null && processedFilter != ProcessedFilter.ALL)) {
            documentTables = documentTableDAO.findAllProcessedDocumentsByFullTextWithPagination(processedFilter == ProcessedFilter.PROCESSED, pattern, firstResult, maxResults);
        } else if (pattern != null && !pattern.isEmpty()) {
            documentTables = documentTableDAO.findAllDocumentsByFullTextWithPagination(pattern, firstResult, maxResults);
        } else if (processedFilter != null && processedFilter != ProcessedFilter.ALL) {
            documentTables = documentTableDAO.findAllProcessedDocumentsWithPagination(processedFilter == ProcessedFilter.PROCESSED, firstResult, maxResults);
        } else {
            documentTables = documentTableDAO.findAllWithPagination(firstResult, maxResults);
        }

        return new Pair<>(
                documentTables.getResults().stream().map(Document::fromDocumentTable).collect(Collectors.toList()),
                documentTables.getTotalNumberOfResults()
        );
    }

    /**
     * Gets documents which contain object with given id.
     * @param objectId the object id
     * @param firstResult index of the first document
     * @param maxResults maximal number of documents to return
     * @return a list of documents
     * @throws IdNotFoundException thrown when object with given id not exists
     * @throws NonRootObjectException if given object is not root
     */
    public Pair<List<Pair<Document, Integer>>, Integer> getDocumentsContainingObject(long objectId, int firstResult, int maxResults) throws IdNotFoundException, NonRootObjectException {

        ObjectTable objectTable = objectTableDAO.find(objectId);
        if (objectTable == null) {
            throw new IdNotFoundException("objectId", objectId);
        } if (!objectTable.isRoot()) {
            throw new NonRootObjectException(objectId, objectTable.getRootObject().getId());
        }

        ResultPagination<Pair<DocumentTable, Integer>> documents = documentTableDAO.findAllDocumentsWithObjectWithPagination(objectId, firstResult, maxResults);
        return new Pair<>(
                documents.getResults().stream()
                    .map(x -> new Pair<>(Document.fromDocumentTable(x.getFirst()), x.getSecond()))
                    .collect(Collectors.toList()),
                documents.getTotalNumberOfResults()
        );
    }



    public Pair<List<Pair<Document, Integer>>, Integer> getFilteredDocumentsContainingObject(long objectId, String pattern, int firstResult, int maxResults) throws IdNotFoundException, NonRootObjectException {

        ObjectTable objectTable = objectTableDAO.find(objectId);
        if (objectTable == null) {
            throw new IdNotFoundException("objectId", objectId);
        } if (!objectTable.isRoot()) {
            throw new NonRootObjectException(objectId, objectTable.getRootObject().getId());
        }

        ResultPagination<Pair<DocumentTable, Integer>> documents;

        if (pattern != null && !pattern.isEmpty()) {
            documents = documentTableDAO.findAllDocumentsWithObjectByFullTextWithPagination(objectId, pattern, firstResult, maxResults);
        } else {
            documents = documentTableDAO.findAllDocumentsWithObjectWithPagination(objectId, firstResult, maxResults);
        }

        return new Pair<>(documents.getResults().stream()
                .map(x -> new Pair<>(Document.fromDocumentTable(x.getFirst()), x.getSecond()))
                .collect(Collectors.toList()),
                documents.getTotalNumberOfResults()
        );
    }

    public Pair<List<Pair<Document, Integer>>, Integer> getDocumentsContainingRelation(long relationId, int firstResult, int maxResults) throws IdNotFoundException {
        RelationTable relationTable = relationTableDAO.find(relationId);
        if (relationTable == null) {
            throw new IdNotFoundException("relationId", relationId);
        }

        ResultPagination<Pair<DocumentTable, Integer>> documents = documentTableDAO.findAllDocumentsWithRelationWithPagination(relationId, firstResult, maxResults);

        return new Pair<>(
                documents.getResults().stream()
                        .map(x -> new Pair<>(Document.fromDocumentTable(x.getFirst()), x.getSecond()))
                        .collect(Collectors.toList()),
                documents.getTotalNumberOfResults()
        );
    }

    public Pair<List<Pair<Document,Integer>>,Integer> getFilteredDocumentsContainingRelation(long relationId, String pattern, int firstResult, int maxResults) throws IdNotFoundException {
        RelationTable relationTable = relationTableDAO.find(relationId);
        if (relationTable == null) {
            throw new IdNotFoundException("relationId", relationId);
        }

        ResultPagination<Pair<DocumentTable, Integer>> documents;

        if (pattern != null && !pattern.isEmpty()) {
            documents = documentTableDAO.findAllDocumentsWithRelationByFullTextWithPagination(relationId, pattern, firstResult, maxResults);
        } else {
            documents = documentTableDAO.findAllDocumentsWithRelationWithPagination(relationId, firstResult, maxResults);
        }

        return new Pair<>(
                documents.getResults().stream()
                    .map(x -> new Pair<>(Document.fromDocumentTable(x.getFirst()), x.getSecond()))
                    .collect(Collectors.toList()),
                documents.getTotalNumberOfResults()
        );
    }

    /**
     * Update the document with the given.
     *
     * @param documentId the document id
     * @param text the new text of the document
     * @return the indicates if document was updated
     * @throws IdNotFoundException if no document with the given id exists
     */
    public boolean updateDocument(long documentId, String text) throws IdNotFoundException {

        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        }

        documentTable.setText(text);
        documentTableDAO.update(documentTable);

        return true;
    }

    /**
     * Gets all object types.
     *
     * @return the object types
     */
    public List<ObjectType> getObjectTypes() {

        return objectTypeTableDAO.findAll().stream()
                .map(ObjectType::fromObjectTypeTable)
                .collect(Collectors.toList());
    }


    /**
     * Gets the object with given id.
     *
     * @param objectId the object id
     * @return the object
     * @throws IdNotFoundException if no object with the given id exists
     */
    public Object getObject(long objectId) throws IdNotFoundException {

        ObjectTable objectTable = objectTableDAO.find(objectId);
        if (objectTable == null) {
            throw new IdNotFoundException("objectId", objectId);
        }

        return Object.fromObjectTable(objectTable, aliasTableDAO.findAllAliasesOfObject(objectId));
    }

    /**
     * Gets all objects.
     *
     * @return the objects
     */
    public List<Object> getObjects() {

        return objectTableDAO.findAll().stream()
                .map(x -> Object.fromObjectTable(x, aliasTableDAO.findAllAliasesOfObject(x)))
                .collect(Collectors.toList());

    }

    /**
     * Gets all objects of the given type id.
     *
     * @param objectTypeId the object type id
     * @return the objects
     * @throws IdNotFoundException if no object type of the given id exists
     */
    public List<Object> getObjects(long objectTypeId) throws IdNotFoundException {
        ObjectTypeTable objectTypeTable = objectTypeTableDAO.find(objectTypeId);
        if (objectTypeTable == null) {
            throw new IdNotFoundException("objectTypeId", objectTypeId);
        }

        return objectTableDAO.findAllByObjectType(objectTypeId).stream()
                .map(x -> Object.fromObjectTable(x, aliasTableDAO.findAllAliasesOfObject(x)))
                .collect(Collectors.toList());
    }


    public Pair<List<Object>,Integer> getFilteredObjects(Long objectTypeId, String aliasFilter, int firstResult, int maxResults) throws IdNotFoundException {

        if (objectTypeId != null) {
            ObjectTypeTable objectType = objectTypeTableDAO.find(objectTypeId);
            if (objectType == null) {
                throw new IdNotFoundException("objectTypeId", objectTypeId);
            }
        }
        ResultPagination<ObjectTable> objects;

        if (objectTypeId != null && (aliasFilter != null && !aliasFilter.isEmpty())) {
            objects = objectTableDAO.findAllByObjTypeAndAliasFullTextWithPagination(objectTypeId, aliasFilter, firstResult, maxResults);

        } else if (objectTypeId != null) {
            objects = objectTableDAO.findAllByObjectTypeWithPagination(objectTypeId, firstResult, maxResults);

        } else if (aliasFilter != null && !aliasFilter.isEmpty()) {
            objects = objectTableDAO.findAllByAliasFullTextWithPagination(aliasFilter, firstResult, maxResults);

        } else {
            objects = objectTableDAO.findAllWithPagination(firstResult, maxResults);

        }

        return new Pair<>(
                objects.getResults().stream()
                    .map(x -> Object.fromObjectTable(x, aliasTableDAO.findAllAliasesOfObject(x)))
                    .collect(Collectors.toList()),
                objects.getTotalNumberOfResults()
        );
    }

    /**
     * Finds all objects and their occurrences for a given document.
     * @param documentId the document id
     * @return the pair with the list of object and the list of their occurrences in the document
     * @throws IdNotFoundException thrown when the document identifier not exists
     */
    public Pair<List<Object>, List<Pair<Long, Occurrence>>> getObjectsWithOccurrences(long documentId) throws IdNotFoundException {
        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        }

        Set<Object> objects = new HashSet<>();
        List<Pair<Long, Occurrence>> objectOccurrences = new ArrayList<>();

        //TODO: add duplicity checking
        for (AliasOccurrenceTable aliasOccurrence : documentTable.getAliasOccurrences()) {

            AliasTable alias = aliasOccurrence.getAlias();
            ObjectTable object = alias.getObject().getRootObject();

            objects.add(Object.fromObjectTable(object, aliasTableDAO.findAllAliasesOfObject(object)));
            objectOccurrences.add(new Pair<>(object.getId(), new Occurrence(alias.getAlias(), aliasOccurrence.getPosition())));
        }

        return new Pair<>(new ArrayList<>(objects), objectOccurrences);
    }

    /**
     * Gets all relation types.
     *
     * @return the relation types
     */
    public List<RelationType> getRelationTypes() {

        return relationTypeTableDAO.findAll().stream()
                .map(RelationType::fromRelationTypeTable)
                .collect(Collectors.toList());

    }

    /**
     * Gets all relations.
     *
     * @return the relations
     */
    public List<Relation> getRelations() {

        return relationTableDAO.findAll().stream()
                .map(x -> Relation.fromRelationTable(x, aliasTableDAO))
                .collect(Collectors.toList());
    }

    /**
     * Gets all relations of the given type.
     *
     * @param relationTypeId the relation type id
     * @return the relations
     * @throws IdNotFoundException if no type with the given id exists
     */
    public List<Relation> getRelations(long relationTypeId) throws IdNotFoundException {
        RelationTypeTable relationTypeTable = relationTypeTableDAO.find(relationTypeId);
        if (relationTypeTable == null) {
            throw new IdNotFoundException("relationTypeId", relationTypeId);
        }

        return relationTableDAO.findAllByRelationType(relationTypeId).stream()
                .map(x -> Relation.fromRelationTable(x, aliasTableDAO))
                .collect(Collectors.toList());
    }

    public Pair<List<Relation>,Integer> getFilteredRelations(Long relationTypeId, String anchorFilter, int firstResult, int maxResults) throws IdNotFoundException {

        if (relationTypeId != null) {
            RelationTypeTable relationType = relationTypeTableDAO.find(relationTypeId);
            if (relationType == null) {
                throw new IdNotFoundException("relationTypeId", relationTypeId);
            }
        }

        ResultPagination<RelationTable> relations;

        if (relationTypeId != null && (anchorFilter != null && !anchorFilter.isEmpty())) {
            relations = relationTableDAO.findAllByRelTypeAndAnchorFullTextWithPagination(relationTypeId, anchorFilter, firstResult, maxResults);
        } else if (relationTypeId != null) {
            relations = relationTableDAO.findAllByRelationTypeWithPagination(relationTypeId, firstResult, maxResults);
        } else if (anchorFilter != null && !anchorFilter.isEmpty()) {
            relations = relationTableDAO.findAllByAnchorFullTextWithPagination(anchorFilter, firstResult, maxResults);
        } else {
            relations = relationTableDAO.findAllWithPagination(firstResult, maxResults);
        }

        return new Pair<>(
                relations.getResults().stream()
                    .map(x -> Relation.fromRelationTable(x, aliasTableDAO))
                    .collect(Collectors.toList()),
                relations.getTotalNumberOfResults()
        );
    }

    /**
     * Finds all relations and their occurrences for a given document.
     * @param documentId the document id
     * @return the pair with the list of relations and the list of their occurrences in the document
     * @throws IdNotFoundException thrown when the document identifier not exists
     */
    public Pair<List<Relation>, List<Pair<Long, Occurrence>>> getRelationsWithOccurrences(long documentId) throws IdNotFoundException{
        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        }

        Set<Relation> relations = new HashSet<>();
        List<Pair<Long, Occurrence>> relationOccurrences = new ArrayList<>();

        for (RelationOccurrenceTable relationOccurrence : documentTable.getRelationOccurrences()) {

            RelationTable relation = relationOccurrence.getRelation();

            relations.add(Relation.fromRelationTable(relation, aliasTableDAO));
            relationOccurrences.add(new Pair<>(relation.getId(), new Occurrence(relationOccurrence.getAnchor(), relationOccurrence.getPosition())));
        }

        return new Pair<>(new ArrayList<>(relations), relationOccurrences);
    }

    public List<String> getRolesForRelationType(long relationTypeId) throws IdNotFoundException {

        RelationTypeTable relationType = relationTypeTableDAO.find(relationTypeId);
        if (relationType == null) {
            throw new IdNotFoundException("relationTypeId", relationTypeId);
        }

        return inRelationTableDAO.getRolesForRelationType(relationTypeId);
    }
}
