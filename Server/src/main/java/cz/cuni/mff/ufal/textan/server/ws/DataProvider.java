package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.ObjectOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.RelationOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.*;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import cz.cuni.mff.ufal.textan.commons.ws.InvalidMergeException;
import cz.cuni.mff.ufal.textan.commons.ws.NonRootObjectException;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.server.services.DirectDataAccessService;
import cz.cuni.mff.ufal.textan.server.services.GraphService;
import cz.cuni.mff.ufal.textan.server.services.MergeService;
import cz.cuni.mff.ufal.textan.server.services.ProcessedFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A web service facade over DirectDataAccessService, GraphService and MergeService.
 * Implementation of the {@link cz.cuni.mff.ufal.textan.commons.ws.IDataProvider}.
 *
 * @author Petr Fanta
 * @see cz.cuni.mff.ufal.textan.server.services.DirectDataAccessService
 * @see cz.cuni.mff.ufal.textan.server.services.GraphService
 * @see cz.cuni.mff.ufal.textan.server.services.MergeService
 */
@javax.jws.WebService(
        serviceName = "DataProviderService",
        portName = "DataProviderPort",
        targetNamespace = "http://ws.commons.textan.ufal.mff.cuni.cz",
        wsdlLocation = "classpath:wsdl/DataProvider.wsdl",
        endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.IDataProvider")
public class DataProvider implements cz.cuni.mff.ufal.textan.commons.ws.IDataProvider {

    /** The logger for DataProvider class. */
    private static final Logger LOG = LoggerFactory.getLogger(DataProvider.class);

    /** The DirectDataAccessService bean (singleton) */
    private final DirectDataAccessService dbService;
    /** The GraphService bean (singleton)*/
    private final GraphService graphService;
    /** The MergeService bean (singleton) */
    private final MergeService mergeService;


    /**
     * Instantiates a new Data provider.
     *
     * @param dbService the DirectDataAccessService bean
     * @param graphService the
     * @param mergeService the merge service
     */
    public DataProvider(DirectDataAccessService dbService, GraphService graphService, MergeService mergeService) {
        this.dbService = dbService;
        this.graphService = graphService;
        this.mergeService = mergeService;
    }

    /**
     * Returns all objects in the database.
     * @param getObjects dummy parametr
     * @return all objects in the database
     */
    @Override
    public GetObjectsResponse getObjects(
            @WebParam(partName = "getObjects", name = "getObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getObjects) {

        LOG.info("Executing operation getObjects: {}", getObjects);

        GetObjectsResponse response = new GetObjectsResponse();
        List<Object> serverObjects = dbService.getObjects();

        for (Object object : serverObjects) {
            response.getObjects().add(object.toCommonsObject());
        }

        LOG.info("Executed operation getObjects: {}", response);
        return response;
    }

    /**
     * Updates the text of the document with given id.
     * @param updateDocumentRequest request containg id and new text
     * @return true if the document was updated, false otherwise
     * @throws IdNotFoundException if no document with the given id exists
     */
    @Override
    public UpdateDocumentResponse updateDocument(
            @WebParam(partName = "updateDocument", name = "updateDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            UpdateDocumentRequest updateDocumentRequest) throws IdNotFoundException {

        LOG.info("Executing operation updateDocument: {}", updateDocumentRequest);

        try {
            boolean result = dbService.updateDocument(updateDocumentRequest.getDocumentId(), updateDocumentRequest.getText());

            UpdateDocumentResponse response = new UpdateDocumentResponse();
            response.setResult(result);

            LOG.info("Executed operation updateDocument: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation updateDocument.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns all relations contained in the database.
     * @param getRelations dummy argument
     * @return all relations contained in the database
     */
    @Override
    public GetRelationsResponse getRelations(
            @WebParam(partName = "getRelations", name = "getRelations", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getRelations) {

        LOG.info("Executing operation getRelations: {}", getRelations);

        GetRelationsResponse response = new GetRelationsResponse();
        List<Relation> relations = dbService.getRelations();
        for (Relation relation : relations) {
            response.getRelations().add(relation.toCommonsRelation());
        }

        LOG.info("Executed operation getRelations: {}", response);
        return response;
    }

    /**
     * Returns all object types stored in the database.
     * @param getObjectTypes dummy argument
     * @return all object types stored in the database
     */
    @Override
    public GetObjectTypesResponse getObjectTypes(
            @WebParam(partName = "getObjectTypes", name = "getObjectTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getObjectTypes) {

        LOG.info("Executing operation getObjectTypes: {}", getObjectTypes);

        final GetObjectTypesResponse response = new GetObjectTypesResponse();
        List<ObjectType> objectTypes = dbService.getObjectTypes();
        for (ObjectType objectType : objectTypes) {
            response.getObjectTypes().add(objectType.toCommonsObjectType());
        }

        LOG.info("Executed operation getObjectTypes: {}", response);
        return response;
    }

    /**
     * Returns document with the given id.
     * @param getDocumentByIdRequest document id
     * @return document with the given id
     * @throws IdNotFoundException if no document with the given id exists
     */
    @Override
    public GetDocumentByIdResponse getDocumentById(
            @WebParam(partName = "getDocumentById", name = "getDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetDocumentByIdRequest getDocumentByIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getDocumentById: {}", getDocumentByIdRequest);

        try {
            Document document = dbService.getDocument(getDocumentByIdRequest.getDocumentId());
            GetDocumentByIdResponse response = new GetDocumentByIdResponse();
            response.setDocument(document.toCommonsDocument());

            LOG.info("Executed operation getDocumentById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getDocumentById.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns graph of the given relation.
     * @param getGraphByRelationIdRequest request containg relation id and maximal distance
     * @return graph of the given relation
     * @throws IdNotFoundException if no relation with the given id exists
     */
    @Override
    public GetGraphByRelationIdResponse getGraphByRelationId(
            @WebParam(partName = "getGraphByRelationIdRequest", name = "getGraphByRelationIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetGraphByRelationIdRequest getGraphByRelationIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getGraphByRelationId: {}", getGraphByRelationIdRequest);

        try {
            GetGraphByRelationIdResponse response = new GetGraphByRelationIdResponse();
            Graph graph = graphService.getGraphFromRelation(getGraphByRelationIdRequest.getRelationId(), getGraphByRelationIdRequest.getDistance());
            response.setGraph(graph.toCommonsGraph());

            LOG.info("Executed operation getGraphById: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getGraphById.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns filtered paginated list of relations.
     * @param getFilteredRelationsRequest request containing relation type id, anchor filter, index of the first record and maximal number of results
     * @return filtered paginated list of the relations
     * @throws IdNotFoundException if no relation type with the given id exists
     */
    @Override
    public GetFilteredRelationsResponse getFilteredRelations(
            @WebParam(partName = "getFilteredRelationsRequest", name = "getFilteredRelationsRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetFilteredRelationsRequest getFilteredRelationsRequest) throws IdNotFoundException {

        LOG.info("Executing operation getFilteredRelations: {}", getFilteredRelationsRequest);

        try {
            Pair<List<Relation>, Integer> results = dbService.getFilteredRelations(
                    getFilteredRelationsRequest.getRelationTypeId(),
                    getFilteredRelationsRequest.getAnchorFilter(),
                    getFilteredRelationsRequest.getFirstResult(),
                    getFilteredRelationsRequest.getMaxResults()
            );

            GetFilteredRelationsResponse response = new GetFilteredRelationsResponse();
            for (Relation relation : results.getFirst()) {
                response.getRelations().add(relation.toCommonsRelation());
            }
            response.setTotalNumberOfResults(results.getSecond());

            LOG.info("Executed operation getFilteredRelations: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getFilteredRelations.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns filtered paginated list of objects.
     * @param getFilteredObjectsRequest request containing object type id, anchor filter, index of the first record and maximal number of results
     * @return filtered paginated list of objects
     * @throws IdNotFoundException if no object type with the given id exists
     */
    @Override
    public GetFilteredObjectsResponse getFilteredObjects(
            @WebParam(partName = "getFilteredObjectsRequest", name = "getFilteredObjectsRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetFilteredObjectsRequest getFilteredObjectsRequest) throws IdNotFoundException {

        LOG.info("Executing operation getFilteredObjectsRequest: {}", getFilteredObjectsRequest);

        try {

            Pair<List<Object>, Integer> results = dbService.getFilteredObjects(
                    getFilteredObjectsRequest.getObjectTypeId(),
                    getFilteredObjectsRequest.getAliasFilter(),
                    getFilteredObjectsRequest.getFirstResult(),
                    getFilteredObjectsRequest.getMaxResults()
            );

            GetFilteredObjectsResponse response = new GetFilteredObjectsResponse();
            for (Object object : results.getFirst()) {
                response.getObjects().add(object.toCommonsObject());
            }
            response.setTotalNumberOfResults(results.getSecond());

            LOG.info("Executed operation getFilteredObjectsRequest: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getFilteredObjectsRequest.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns all relation types from the database.
     * @param getRelationTypes dummy parameter
     * @return all relation types from the database
     */
    @Override
    public GetRelationTypesResponse getRelationTypes(
            @WebParam(partName = "getRelationTypes", name = "getRelationTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getRelationTypes) {

        LOG.info("Executing operation getRelationTypes: {}", getRelationTypes);

        final GetRelationTypesResponse response = new GetRelationTypesResponse();
        List<RelationType> relationTypes = dbService.getRelationTypes();
        for (RelationType relationType : relationTypes) {
            response.getRelationTypes().add(relationType.toCommonsRelationType());
        }

        LOG.info("Executed operation getRelationTypes: {}", response);
        return response;
    }

    /**
     * Returns graph for the given object.
     * @param getGraphByObjectIdRequest request containing object id and maximal distace
     * @return graph for the given object
     * @throws IdNotFoundException if no object with the given id exists
     * @throws NonRootObjectException if the given object is not root
     */
    @Override
    public GetGraphByObjectIdResponse getGraphByObjectId(
            @WebParam(partName = "getGraphById", name = "getGraphById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetGraphByObjectIdRequest getGraphByObjectIdRequest) throws IdNotFoundException, NonRootObjectException {

        LOG.info("Executing operation getGraphByObjectId: {}", getGraphByObjectIdRequest);

        try {
            GetGraphByObjectIdResponse response = new GetGraphByObjectIdResponse();
            Graph graph = graphService.getGraphFromObject(getGraphByObjectIdRequest.getObjectId(), getGraphByObjectIdRequest.getDistance());
            response.setGraph(graph.toCommonsGraph());

            LOG.info("Executed operation getGraphById: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getGraphById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.NonRootObjectException e) {
            LOG.warn("Problem in operation getGraphById.", e);
            throw translateNonRootObjectException(e);
        }
    }

    /**
     * Returns filtered paginated list of documents containing given object.
     * @param getFilteredDocumentsContainingObjectByIdRequest request containing object id, search pattern, index of the first record and maximal number of results
     * @return filtered paginated list of documents containing given object
     * @throws IdNotFoundException if no object with the given id exists
     * @throws NonRootObjectException if the given object is not root
     */
    @Override
    public GetFilteredDocumentsContainingObjectByIdResponse getFilteredDocumentsContainingObjectById(
            @WebParam(partName = "getFilteredDocumentsContainingObjectByIdRequest", name = "getFilteredDocumentsContainingObjectByIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetFilteredDocumentsContainingObjectByIdRequest getFilteredDocumentsContainingObjectByIdRequest) throws IdNotFoundException, NonRootObjectException {
        LOG.info("Executing operation getFilteredDocumentsContainingObjectById: {}", getFilteredDocumentsContainingObjectByIdRequest);

        try {

            Pair<List<Pair<Document, Integer>>, Integer> documents = dbService.getFilteredDocumentsContainingObject(
                    getFilteredDocumentsContainingObjectByIdRequest.getObjectId(),
                    getFilteredDocumentsContainingObjectByIdRequest.getPattern(),
                    getFilteredDocumentsContainingObjectByIdRequest.getFirstResult(),
                    getFilteredDocumentsContainingObjectByIdRequest.getMaxResults()
            );

            GetFilteredDocumentsContainingObjectByIdResponse response = new GetFilteredDocumentsContainingObjectByIdResponse();
            for (Pair<Document, Integer> documentCountPair : documents.getFirst()) {
                GetFilteredDocumentsContainingObjectByIdResponse.DocumentCountPair pair = new GetFilteredDocumentsContainingObjectByIdResponse.DocumentCountPair();
                pair.setDocument(documentCountPair.getFirst().toCommonsDocument());
                pair.setCountOfOccurrences(documentCountPair.getSecond());
                response.getDocumentCountPairs().add(pair);
            }
            response.setTotalNumberOfResults(documents.getSecond());

            LOG.info("Executed operation getFilteredDocumentsContainingObjectById: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getFilteredDocumentsContainingObjectById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.NonRootObjectException e) {
            LOG.warn("Problem in operation getFilteredDocumentsContainingObjectById.", e);
            throw translateNonRootObjectException(e);
        }
    }

    /**
     * Returns graph of objects in relation with the given object.
     * @param getRelatedObjectsByIdRequest request containing object id
     * @return graph of objects in relation with the given object
     * @throws IdNotFoundException if no object with the given id exists
     * @throws NonRootObjectException if the given object is not root
     */
    @Override
    public GetRelatedObjectsByIdResponse getRelatedObjectsById(
            @WebParam(partName = "getRelatedObjectsById", name = "getRelatedObjectsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelatedObjectsByIdRequest getRelatedObjectsByIdRequest)
            throws IdNotFoundException, NonRootObjectException {

        LOG.info("Executing operation getRelatedObjectsById: {}", getRelatedObjectsByIdRequest);
        try {
            GetRelatedObjectsByIdResponse response = new GetRelatedObjectsByIdResponse();
            Graph graph = graphService.getRelatedObjects(getRelatedObjectsByIdRequest.getObjectId());
            response.setGraph(graph.toCommonsGraph());

            LOG.info("Executed operation getRelatedObjectsById: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getRelatedObjectsById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.NonRootObjectException e) {
            LOG.warn("Problem in operation getRelatedObjectsById.", e);
            throw translateNonRootObjectException(e);
        }
    }

    /**
     * Adds new unprocessed document.
     * @param addDocumentRequest text of the new report
     * @return id of the new document
     */
    @Override
    public AddDocumentResponse addDocument(
            @WebParam(partName = "addDocument", name = "addDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            AddDocumentRequest addDocumentRequest) {

        LOG.info("Executing operation addDocument: {}", addDocumentRequest);

        long documentId = dbService.addDocument(addDocumentRequest.getText());

        AddDocumentResponse response = new AddDocumentResponse();
        response.setDocumentId(documentId);

        LOG.info("Executed operation addDocument: {}", response);
        return response;
    }

    /**
     * Returns objects and relations that appear in the given document.
     * @param getObjectsAndRelationsOccurringInDocumentRequest request containing document id
     * @return objects and relations that appear in the given document
     * @throws IdNotFoundException if no document with the given id exists
     */
    @Override
    public GetObjectsAndRelationsOccurringInDocumentResponse getObjectsAndRelationsOccurringInDocument(
            @WebParam(partName = "getObjectsAndRelationsOccurringInDocumentRequest", name = "getObjectsAndRelationsOccurringInDocumentRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsAndRelationsOccurringInDocumentRequest getObjectsAndRelationsOccurringInDocumentRequest) throws IdNotFoundException {

        LOG.info("Executing operation getObjectsAndRelationsOccurringInDocument: {}", getObjectsAndRelationsOccurringInDocumentRequest);

        try {

            GetObjectsAndRelationsOccurringInDocumentResponse response = new GetObjectsAndRelationsOccurringInDocumentResponse();

            Pair<List<Object>, List<Pair<Long, Occurrence>>> objectWithOccurrences = dbService.getObjectsWithOccurrences(getObjectsAndRelationsOccurringInDocumentRequest.getDocumentId());
            Pair<List<Relation>, List<Pair<Long, Occurrence>>> relationWithOccurrences = dbService.getRelationsWithOccurrences(getObjectsAndRelationsOccurringInDocumentRequest.getDocumentId());

            for (Object object : objectWithOccurrences.getFirst()) {
                response.getObjects().add(object.toCommonsObject());
            }

            for (Pair<Long, Occurrence> objectOccurrence : objectWithOccurrences.getSecond()) {
                ObjectOccurrence commonsObjectOccurrence = new ObjectOccurrence();
                commonsObjectOccurrence.setObjectId(objectOccurrence.getFirst());
                commonsObjectOccurrence.setAlias(objectOccurrence.getSecond().toCommonsOccurrence());

                response.getObjectOccurrences().add(commonsObjectOccurrence);
            }

            for (Relation relation : relationWithOccurrences.getFirst()) {
                response.getRelations().add(relation.toCommonsRelation());
            }

            for (Pair<Long, Occurrence> relationOccurrence : relationWithOccurrences.getSecond()) {
                RelationOccurrence commonsRelationOccurrence = new RelationOccurrence();
                commonsRelationOccurrence.setRelationId(relationOccurrence.getFirst());
                commonsRelationOccurrence.setAnchor((relationOccurrence.getSecond() != null ? relationOccurrence.getSecond().toCommonsOccurrence() : null));

                response.getRelationOccurrences().add(commonsRelationOccurrence);
            }

            LOG.info("Executed operation getObjectsAndRelationsOccurringInDocument: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getObjectsAndRelationsOccurringInDocument.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns paginated list of documents that the given object appear in.
     * @param getDocumentsContainingObjectByIdRequest request containing object id, index of the first record and maximal number of results
     * @return paginated list of documents that the given object appear in
     * @throws IdNotFoundException if no object with the given id exists
     * @throws NonRootObjectException if the given object is not root
     */
    @Override
    public GetDocumentsContainingObjectByIdResponse getDocumentsContainingObjectById(
            @WebParam(partName = "getDocumentsContainsObjectByIdRequest", name = "getDocumentsContainsObjectByIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetDocumentsContainingObjectByIdRequest getDocumentsContainingObjectByIdRequest) throws IdNotFoundException, NonRootObjectException {

        LOG.info("Executing operation getDocumentsContainsObjectById: {}", getDocumentsContainingObjectByIdRequest);

        try {

            GetDocumentsContainingObjectByIdResponse response = new GetDocumentsContainingObjectByIdResponse();
            Pair<List<Pair<Document, Integer>>, Integer> documents = dbService.getDocumentsContainingObject(
                    getDocumentsContainingObjectByIdRequest.getObjectId(),
                    getDocumentsContainingObjectByIdRequest.getFirstResult(),
                    getDocumentsContainingObjectByIdRequest.getMaxResults()
            );
            for (Pair<Document, Integer> documentCountPair : documents.getFirst()) {
                GetDocumentsContainingObjectByIdResponse.DocumentCountPair pair = new GetDocumentsContainingObjectByIdResponse.DocumentCountPair();
                pair.setDocument(documentCountPair.getFirst().toCommonsDocument());
                pair.setCountOfOccurrences(documentCountPair.getSecond());
                response.getDocumentCountPairs().add(pair);
            }
            response.setTotalNumberOfResults(documents.getSecond());

            LOG.info("Executed operation getDocumentsContainsObjectById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getDocumentsContainsObjectById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.NonRootObjectException e) {
            LOG.warn("Problem in operation getDocumentsContainsObjectById.", e);
            throw translateNonRootObjectException(e);
        }
    }

    /**
     * Returns objects with given ids.
     * @param getObjectsByIdsRequest request containing a list of object ids
     * @return objects with given ids
     * @throws IdNotFoundException if no object with the given id exists
     */
    @Override
    public GetObjectsByIdsResponse getObjectsByIds(
            @WebParam(partName = "getObjectsByIdsRequest", name = "getObjectsByIdsRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsByIdsRequest getObjectsByIdsRequest) throws IdNotFoundException {

        LOG.info("Executing operation getObjectsByIds: {}", getObjectsByIdsRequest);

        try {
            Set<Object> objects = new HashSet<>(getObjectsByIdsRequest.getObjectIds().size());
            for (long id : getObjectsByIdsRequest.getObjectIds()) {
                objects.add(dbService.getObject(id));
            }

            GetObjectsByIdsResponse response = new GetObjectsByIdsResponse();
            for (Object object : objects) {
                response.getObjects().add(object.toCommonsObject());
            }

            LOG.info("Executed operation getObjectsByIds: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getObjectsByIds.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns paginated list of documents that the given relation appear in.
     * @param getDocumentsContainingRelationByIdRequest request containing relation id, index of the first record and maximal number of results
     * @return paginated list of documents that the given relation appear in
     * @throws IdNotFoundException if no relation with the given id exists
     */
    @Override
    public GetDocumentsContainingRelationByIdResponse getDocumentsContainingRelationById(
            @WebParam(partName = "getDocumentsContainingRelationByIdRequest", name = "getDocumentsContainingRelationByIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetDocumentsContainingRelationByIdRequest getDocumentsContainingRelationByIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getDocumentsContainingRelationById: {}", getDocumentsContainingRelationByIdRequest);

        try {

            GetDocumentsContainingRelationByIdResponse response = new GetDocumentsContainingRelationByIdResponse();
            Pair<List<Pair<Document, Integer>>, Integer> documents = dbService.getDocumentsContainingRelation(
                    getDocumentsContainingRelationByIdRequest.getRelationId(),
                    getDocumentsContainingRelationByIdRequest.getFirstResult(),
                    getDocumentsContainingRelationByIdRequest.getMaxResults()
            );
            for (Pair<Document, Integer> documentCountPair : documents.getFirst()) {
                GetDocumentsContainingRelationByIdResponse.DocumentCountPair pair = new GetDocumentsContainingRelationByIdResponse.DocumentCountPair();
                pair.setDocument(documentCountPair.getFirst().toCommonsDocument());
                pair.setCountOfOccurrences(documentCountPair.getSecond());
                response.getDocumentCountPairs().add(pair);
            }
            response.setTotalNumberOfResults(documents.getSecond());

            LOG.info("Executed operation getDocumentsContainingRelationById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getDocumentsContainingRelationById.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Splits object with the given id.
     * TODO splitting is not implemented
     * @param splitObjectRequest request containing object id
     * @return true if the object was split, false otherwise
     * @throws IdNotFoundException if no object with the given id exists
     * @throws NonRootObjectException if the given object is not root
     */
    @Override
    public SplitObjectResponse splitObject(
            @WebParam(partName = "splitObject", name = "splitObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            SplitObjectRequest splitObjectRequest) throws IdNotFoundException, NonRootObjectException {

        LOG.info("Executing operation splitObject: {}", splitObjectRequest);

        try {
            boolean result = mergeService.splitObject(splitObjectRequest.getObjectId());

            SplitObjectResponse response = new SplitObjectResponse();
            response.setResult(result);

            LOG.info("Executed operation splitObject: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation splitObject.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.NonRootObjectException e) {
            LOG.warn("Problem in operation splitObject.", e);
            throw translateNonRootObjectException(e);
        }
    }

    /**
     * Returns list of roles that appeared in relations with the given relation type.
     * @param getRolesForRelationTypeByIdRequest request containing relation type id
     * @return list of roles that appeared in relations with the given relation type
     * @throws IdNotFoundException if no relation type with the given id exists
     */
    @Override
    public GetRolesForRelationTypeByIdResponse getRolesForRelationTypeById(
            @WebParam(partName = "getRolesForRelationTypeByIdRequest", name = "getRolesForRelationTypeByIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRolesForRelationTypeByIdRequest getRolesForRelationTypeByIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getRolesForRelationTypeById: {}", getRolesForRelationTypeByIdRequest);

        try {

            List<String> roles = dbService.getRolesForRelationType(getRolesForRelationTypeByIdRequest.getRelationTypeId());
            GetRolesForRelationTypeByIdResponse response = new GetRolesForRelationTypeByIdResponse();
            response.getRoles().addAll(roles);

            LOG.info("Executed operation getRolesForRelationTypeById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getRolesForRelationTypeById.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns a graph representing a path between given objects.
     * If no such path exists, empty graph is returned.
     * @param getPathByIdRequest request containing start object id, target object id and maximal length of the path
     * @return graph representing a path between given objects
     * @throws IdNotFoundException if no object with the given id exists
     * @throws NonRootObjectException  if the given object is not root
     */
    @Override
    public GetPathByIdResponse getPathById(
            @WebParam(partName = "getPathById", name = "getPathById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetPathByIdRequest getPathByIdRequest) throws IdNotFoundException, NonRootObjectException {

        LOG.info("Executing operation getPathById: {}", getPathByIdRequest);

        try {
            GetPathByIdResponse response = new GetPathByIdResponse();
            Graph graph = graphService.getPath(getPathByIdRequest.getStartObjectId(), getPathByIdRequest.getTargetObjectId(), getPathByIdRequest.getMaxLength());
            response.setGraph(graph.toCommonsGraph());

            LOG.info("Executed operation getPathById: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getPathById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.NonRootObjectException e) {
            LOG.warn("Problem in operation getPathById.", e);
            throw translateNonRootObjectException(e);
        }
    }

    /**
     * Returns list of all documents stored in the database.
     * @param getDocuments dummy argument
     * @return list of all documents stored in the database
     */
    @Override
    public GetDocumentsResponse getDocuments(
            @WebParam(partName = "getDocuments", name = "getDocuments", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getDocuments) {

        LOG.info("Executing operation getDocuments: {}", getDocuments);

        GetDocumentsResponse response = new GetDocumentsResponse();
        List<Document> documents = dbService.getDocuments();
        for (Document document : documents) {
            response.getDocuments().add(document.toCommonsDocument());
        }

        LOG.info("Executed operation getDocuments: {}", response);
        return response;
    }

    /**
     * Returns list of all relations with the given type.
     * @param getRelationsByTypeIdRequest request containing relation type id
     * @return list of all relations with the given type
     * @throws IdNotFoundException if no relation type with the given id exists
     */
    @Override
    public GetRelationsByTypeIdResponse getRelationsByTypeId(
            @WebParam(partName = "getRelationsByTypeId", name = "getRelationsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelationsByTypeIdRequest getRelationsByTypeIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getRelationsByTypeId: {}", getRelationsByTypeIdRequest);

        try {
            List<Relation> relations = dbService.getRelations(getRelationsByTypeIdRequest.getRelationTypeId());

            GetRelationsByTypeIdResponse response = new GetRelationsByTypeIdResponse();
            for (Relation relation : relations) {
                response.getRelations().add(relation.toCommonsRelation());
            }

            LOG.info("Executed operation getRelationsByTypeId: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getObjectsByTypeId.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns filtered paginated list of documents.
     * @param getFilteredDocumentsRequest request containing search pattern, processed indicator, index of the first record and maximal number of results
     * @return filtered paginated list of documents
     */
    @Override
    public GetFilteredDocumentsResponse getFilteredDocuments(
            @WebParam(partName = "getFilteredDocumentsRequest", name = "getFilteredDocumentsRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetFilteredDocumentsRequest getFilteredDocumentsRequest) {

        LOG.info("Executing operation getFilteredDocuments: {}", getFilteredDocumentsRequest);

        GetFilteredDocumentsResponse response = new GetFilteredDocumentsResponse();

        Pair<List<Document>, Integer> documents = dbService.getFilteredDocuments(
                getFilteredDocumentsRequest.getPattern(),
                ProcessedFilter.parse(getFilteredDocumentsRequest.getProcessedFilter()),
                getFilteredDocumentsRequest.getFirstResult(),
                getFilteredDocumentsRequest.getMaxResults()
        );

        for (Document document : documents.getFirst()) {
            response.getDocuments().add(document.toCommonsDocument());
        }
        response.setTotalNumberOfResults(documents.getSecond());

        LOG.info("Executed operation getFilteredDocuments: {}", response);
        return response;
    }

    /**
     * Returns filtered paginated list of documents containing the given relation.
     * @param getFilteredDocumentsContainingRelationByIdRequest request containing relation id, search pattern, index of the first record and maximal number of results
     * @return filtered paginated list of documents containing the given relation
     * @throws IdNotFoundException if no relation with the given id exists
     */
    @Override
    public GetFilteredDocumentsContainingRelationByIdResponse getFilteredDocumentsContainingRelationById(
            @WebParam(partName = "getFilteredDocumentsContainingRelationByIdRequest", name = "getFilteredDocumentsContainingRelationByIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetFilteredDocumentsContainingRelationByIdRequest getFilteredDocumentsContainingRelationByIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getFilteredDocumentsContainingRelationById: {}", getFilteredDocumentsContainingRelationByIdRequest);

        try {

            Pair<List<Pair<Document, Integer>>, Integer> documents = dbService.getFilteredDocumentsContainingRelation(
                    getFilteredDocumentsContainingRelationByIdRequest.getRelationId(),
                    getFilteredDocumentsContainingRelationByIdRequest.getPattern(),
                    getFilteredDocumentsContainingRelationByIdRequest.getFirstResult(),
                    getFilteredDocumentsContainingRelationByIdRequest.getMaxResults()
            );

            GetFilteredDocumentsContainingRelationByIdResponse response = new GetFilteredDocumentsContainingRelationByIdResponse();
            for (Pair<Document, Integer> documentCountPair : documents.getFirst()) {
                GetFilteredDocumentsContainingRelationByIdResponse.DocumentCountPair pair = new GetFilteredDocumentsContainingRelationByIdResponse.DocumentCountPair();
                pair.setDocument(documentCountPair.getFirst().toCommonsDocument());
                pair.setCountOfOccurrences(documentCountPair.getSecond());
                response.getDocumentCountPairs().add(pair);
            }
            response.setTotalNumberOfResults(documents.getSecond());

            LOG.info("Executed operation getFilteredDocumentsContainingRelationById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getFilteredDocumentsContainingRelationById.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns list of all objects with the given object type.
     * @param getObjectsByTypeIdRequest request containing object type id
     * @return list of all objects with the given object type
     * @throws IdNotFoundException if no object type with the given id exists
     */
    @Override
    public GetObjectsByTypeIdResponse getObjectsByTypeId(
            @WebParam(partName = "getObjectsByTypeId", name = "getObjectsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsByTypeIdRequest getObjectsByTypeIdRequest)
            throws IdNotFoundException {

        LOG.info("Executing operation getObjectsByTypeId: {}", getObjectsByTypeIdRequest);

        try {
            List<Object> objects = dbService.getObjects(getObjectsByTypeIdRequest.getObjectTypeId());

            GetObjectsByTypeIdResponse response = new GetObjectsByTypeIdResponse();
            for (Object object : objects) {
                response.getObjects().add(object.toCommonsObject());
            }

            LOG.info("Executed operation getObjectsByTypeId: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getObjectsByTypeId.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Returns object with the given id.
     * @param getObjectRequest request containing object id
     * @return object with the given id
     * @throws IdNotFoundException if no object with the given id exists
     */
    @Override
    public GetObjectResponse getObject(
            @WebParam(partName = "getObject", name = "getObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectRequest getObjectRequest) throws IdNotFoundException {

        LOG.info("Executing operation getObject: {}", getObjectRequest);

        try {
            GetObjectResponse response = new GetObjectResponse();
            Object object = dbService.getObject(getObjectRequest.getObjectId());
            response.setObject(object.toCommonsObject());

            LOG.info("Executed operation getObject: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getObject.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Merges two object into a new one.
     * @param mergeObjectsRequest request containing two object ids
     * @return object id of the new object
     * @throws IdNotFoundException if no object with the given id exists
     * @throws InvalidMergeException if the given objects have different types
     * @throws NonRootObjectException if the given object is not root
     */
    @Override
    public MergeObjectsResponse mergeObjects(
            @WebParam(partName = "mergeObjects", name = "mergeObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            MergeObjectsRequest mergeObjectsRequest) throws IdNotFoundException, InvalidMergeException, NonRootObjectException {

        LOG.info("Executing operation mergeObjects: {}", mergeObjectsRequest);

        try {
            MergeObjectsResponse response = new MergeObjectsResponse();
            long objectId = mergeService.mergeObjects(mergeObjectsRequest.getObject1Id(), mergeObjectsRequest.getObject2Id());
            response.setObjectId(objectId);

            LOG.info("Executed operation mergeObjects: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation mergeObjects.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.InvalidMergeException e) {
            LOG.warn("Problem in operation mergeObjects.", e);
            throw translateInvalidMergeException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.NonRootObjectException e) {
            LOG.warn("Problem in operation mergeObjects.", e);
            throw translateNonRootObjectException(e);
        }
    }

    private static IdNotFoundException translateIdNotFoundException(cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
        cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
        exceptionBody.setFieldName(e.getFieldName());
        exceptionBody.setFieldValue(e.getFieldValue());

        return new IdNotFoundException(e.getMessage(), exceptionBody);
    }

    private static InvalidMergeException translateInvalidMergeException(cz.cuni.mff.ufal.textan.server.services.InvalidMergeException e) {
        cz.cuni.mff.ufal.textan.commons.models.dataprovider.InvalidMergeException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.dataprovider.InvalidMergeException();
        exceptionBody.setObjectId(e.getObjectId());

        return new InvalidMergeException(e.getMessage(), exceptionBody);
    }

    private static NonRootObjectException translateNonRootObjectException(cz.cuni.mff.ufal.textan.server.services.NonRootObjectException e) {
        cz.cuni.mff.ufal.textan.commons.models.NonRootObjectException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.NonRootObjectException();
        exceptionBody.setObjectId(e.getObjectId());
        exceptionBody.setRootObjectId(e.getRootObjectId());

        return new NonRootObjectException(e.getMessage(), exceptionBody);
    }
}