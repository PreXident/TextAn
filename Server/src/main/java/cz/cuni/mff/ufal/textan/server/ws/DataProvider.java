package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.ObjectOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.RelationOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.*;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.server.services.DirectDataAccessService;
import cz.cuni.mff.ufal.textan.server.services.GraphService;
import cz.cuni.mff.ufal.textan.server.services.ProcessedFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * For now only mocking database access.
 */
@javax.jws.WebService(
        serviceName = "DataProviderService",
        portName = "DataProviderPort",
        targetNamespace = "http://ws.commons.textan.ufal.mff.cuni.cz",
        wsdlLocation = "classpath:wsdl/DataProvider.wsdl",
        endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.IDataProvider")
public class DataProvider implements cz.cuni.mff.ufal.textan.commons.ws.IDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DataProvider.class);

    private final DirectDataAccessService dbService;
    private final GraphService graphService;


    public DataProvider(DirectDataAccessService dbService, GraphService graphService) {
        this.dbService = dbService;
        this.graphService = graphService;
    }

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

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

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

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

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

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

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

    @Override
    public GetGraphByIdResponse getGraphById(
            @WebParam(partName = "getGraphById", name = "getGraphById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetGraphByIdRequest getGraphByIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getGraphById: {}", getGraphByIdRequest);

        try {
            GetGraphByIdResponse response = new GetGraphByIdResponse();
            Graph graph = graphService.getGraph(getGraphByIdRequest.getObjectId(), getGraphByIdRequest.getDistance());
            response.setGraph(graph.toCommonsGraph());

            LOG.info("Executed operation getGraphById: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getGraphById.", e);

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetRelatedObjectsByIdResponse getRelatedObjectsById(
            @WebParam(partName = "getRelatedObjectsById", name = "getRelatedObjectsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelatedObjectsByIdRequest getRelatedObjectsByIdRequest)
            throws IdNotFoundException {

        LOG.info("Executing operation getRelatedObjectsById: {}", getRelatedObjectsByIdRequest);
        try {
            GetRelatedObjectsByIdResponse response = new GetRelatedObjectsByIdResponse();
            Graph graph = graphService.getRelatedObjects(getRelatedObjectsByIdRequest.getObjectId());
            response.setGraph(graph.toCommonsGraph());

            LOG.info("Executed operation getRelatedObjectsById: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getRelatedObjectsById.", e);

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public AddDocumentResponse addDocument(
            @WebParam(partName = "addDocument", name = "addDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            AddDocumentRequest addDocumentRequest) {

        LOG.info("Executing operation addDocument: {}", addDocumentRequest);

        long documentId = dbService.addDocument(addDocumentRequest.getText());

        AddDocumentResponse response = new AddDocumentResponse();
        response.setDocumentId(documentId);

        LOG.info("Executed operation addDocument: {}", response);
        return new AddDocumentResponse();
    }

    @Override
    public GetObjectsAndRelationsOccurringInDocumentResponse getObjectsAndRelationsOccurringInDocument(
            @WebParam(partName = "getObjectsAndRelationsOccurringInDocumentRequest", name = "getObjectsAndRelationsOccurringInDocumentRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsAndRelationsOccurringInDocumentRequest getObjectsAndRelationsOccurringInDocumentRequest) throws IdNotFoundException {

        LOG.info("Executing operation getObjectsAndRelationsOccurringInDocument: {}", getObjectsAndRelationsOccurringInDocumentRequest);

        try {

            GetObjectsAndRelationsOccurringInDocumentResponse response = new GetObjectsAndRelationsOccurringInDocumentResponse();

            //TODO: object or object id?
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

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetDocumentsContainingObjectByIdResponse getDocumentsContainingObjectById(
            @WebParam(partName = "getDocumentsContainsObjectByIdRequest", name = "getDocumentsContainsObjectByIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetDocumentsContainingObjectByIdRequest getDocumentsContainingObjectByIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getDocumentsContainsObjectById: {}", getDocumentsContainingObjectByIdRequest);

        try {

            GetDocumentsContainingObjectByIdResponse response = new GetDocumentsContainingObjectByIdResponse();
            Pair<List<Document>, Integer> documents = dbService.getDocumentsContainingObject(
                    getDocumentsContainingObjectByIdRequest.getObjectId(),
                    getDocumentsContainingObjectByIdRequest.getFirstResult(),
                    getDocumentsContainingObjectByIdRequest.getMaxResults()
            );
            for (Document document : documents.getFirst()) {
                response.getDocuments().add(document.toCommonsDocument());
            }
            response.setTotalNumberOfResults(documents.getSecond());

            LOG.info("Executed operation getDocumentsContainsObjectById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getDocumentsContainsObjectById.", e);

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetObjectsByIdsResponse getObjectsByIds(
            @WebParam(partName = "getObjectsByIdsRequest", name = "getObjectsByIdsRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsByIdsRequest getObjectsByIdsRequest) throws IdNotFoundException {

        LOG.info("Executing operation getDocumentsContainsObjectById: {}", getObjectsByIdsRequest);

        try {
            Set<Object> objects = new HashSet<>(getObjectsByIdsRequest.getObjectIds().size());
            for (long id : getObjectsByIdsRequest.getObjectIds()) {
                objects.add(dbService.getObject(id));
            }

            GetObjectsByIdsResponse response = new GetObjectsByIdsResponse();
            for (Object object : objects) {
                response.getObjects().add(object.toCommonsObject());
            }

            LOG.info("Executed operation getDocumentsContainsObjectById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getDocumentsContainsObjectById.", e);

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public SplitObjectResponse splitObject(
            @WebParam(partName = "splitObject", name = "splitObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            SplitObjectRequest splitObjectRequest) throws IdNotFoundException {

        LOG.info("Executing operation splitObject: {}", splitObjectRequest);

        try {
            boolean result = dbService.splitObject(splitObjectRequest.getObjectId());

            SplitObjectResponse response = new SplitObjectResponse();
            response.setResult(result);

            LOG.info("Executed operation splitObject: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation splitObject.", e);

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

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

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetPathByIdResponse getPathById(
            @WebParam(partName = "getPathById", name = "getPathById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetPathByIdRequest getPathByIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getPathById: {}", getPathByIdRequest);

        try {
            GetPathByIdResponse response = new GetPathByIdResponse();
            Graph graph = graphService.getPath(getPathByIdRequest.getStartObjectId(), getPathByIdRequest.getTargetObjectId());
            response.setGraph(graph.toCommonsGraph());

            LOG.info("Executed operation getPathById: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getPathById.", e);

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

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

    @Override
    public GetRelationsByTypeIdResponse getRelationsByTypeId(
            @WebParam(partName = "getRelationsByTypeId", name = "getRelationsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelationsByTypeIdRequest getRelationsByTypeIdRequest) throws IdNotFoundException {

        LOG.info("Executing operation getRelationsByTypeId: {}", getRelationsByTypeIdRequest);

        GetRelationsByTypeIdResponse response = new GetRelationsByTypeIdResponse();
        List<Relation> relations = dbService.getRelations(getRelationsByTypeIdRequest.getRelationTypeId());
        for (Relation relation : relations) {
            response.getRelations().add(relation.toCommonsRelation());
        }

        LOG.info("Executed operation getRelationsByTypeId: {}", response);
        return response;
    }

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

    @Override
    public GetObjectsByTypeIdResponse getObjectsByTypeId(
            @WebParam(partName = "getObjectsByTypeId", name = "getObjectsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsByTypeIdRequest getObjectsByTypeIdRequest)
            throws IdNotFoundException {

        LOG.info("Executing operation getObjectsByTypeId: {}", getObjectsByTypeIdRequest);

        GetObjectsByTypeIdResponse response = new GetObjectsByTypeIdResponse();
        List<Object> objects = dbService.getObjects(getObjectsByTypeIdRequest.getObjectTypeId());
        for (Object object : objects) {
            response.getObjects().add(object.toCommonsObject());
        }

        LOG.info("Executed operation getObjectsByTypeId: {}", response);
        return response;
    }

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

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public MergeObjectsResponse mergeObjects(
            @WebParam(partName = "mergeObjects", name = "mergeObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            MergeObjectsRequest mergeObjectsRequest) throws IdNotFoundException {

        LOG.info("Executing operation mergeObjects: {}", mergeObjectsRequest);

        try {
            MergeObjectsResponse response = new MergeObjectsResponse();
            long objectId = dbService.mergeObjects(mergeObjectsRequest.getObject1Id(), mergeObjectsRequest.getObject2Id());
            response.setObjectId(objectId);

            LOG.info("Executed operation mergeObjects: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation mergeObjects.", e);

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }
}