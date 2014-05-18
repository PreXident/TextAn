package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.*;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.server.services.DirectDataAccessService;
import cz.cuni.mff.ufal.textan.server.services.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import java.util.List;

/**
 * For now only mocking database access.
 */
@javax.jws.WebService(
        serviceName = "DataProviderService",
        portName = "DataProviderWS",
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

    /*
    * TODO: Add operations which:
    *  - gets all documents for given object
    *
    * */

    @Override
    public GetObjectsResponse getObjects(
            @WebParam(partName = "getObjects", name = "getObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getObjects,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getObjects: {} {}", getObjects, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        GetObjectsResponse response = new GetObjectsResponse();
        List<Object> serverObjects = dbService.getObjects(serverTicket);

        for (Object object : serverObjects) {
            response.getObjects().add(object.toCommonsObject());
        }

        return response;
    }

    @Override
    public UpdateDocumentResponse updateDocument(
            @WebParam(partName = "updateDocument", name = "updateDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            UpdateDocumentRequest updateDocumentRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) throws IdNotFoundException {

        LOG.debug("Executing operation updateDocument: {} {}", updateDocumentRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        try {
            boolean result = dbService.updateDocument(updateDocumentRequest.getDocumentId(), updateDocumentRequest.getText(), serverTicket);

            UpdateDocumentResponse response = new UpdateDocumentResponse();
            response.setResult(result);

            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetRelationsResponse getRelations(
            @WebParam(partName = "getRelations", name = "getRelations", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getRelations,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getRelations: {} {}", getRelations, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        GetRelationsResponse response = new GetRelationsResponse();
        List<Relation> relations = dbService.getRelations(serverTicket);
        for (Relation relation : relations) {
            response.getRelations().add(relation.toCommonsRelation());
        }

        return response;
    }

    @Override
    public GetObjectTypesResponse getObjectTypes(
            @WebParam(partName = "getObjectTypes", name = "getObjectTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getObjectTypes,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getObjectTypes: {} {}", getObjectTypes, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        final GetObjectTypesResponse response = new GetObjectTypesResponse();
        List<ObjectType> objectTypes = dbService.getObjectTypes(serverTicket);
        for (ObjectType objectType : objectTypes) {
            response.getObjectTypes().add(objectType.toCommonsObjectType());
        }

        return response;
    }

    @Override
    public GetDocumentByIdResponse getDocumentById(
            @WebParam(partName = "getDocumentById", name = "getDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetDocumentByIdRequest getDocumentByIdRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getDocumentById: {} {}", getDocumentByIdRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        try {
            Document document = dbService.getDocument(getDocumentByIdRequest.getDocumentId(), serverTicket);
            GetDocumentByIdResponse getDocumentByIdResponse = new GetDocumentByIdResponse();
            getDocumentByIdResponse.setDocument(document.toCommonsDocument());

            return getDocumentByIdResponse;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetRelationTypesResponse getRelationTypes(
            @WebParam(partName = "getRelationTypes", name = "getRelationTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getRelationTypes,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getRelationTypes: {} {}", getRelationTypes, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        final GetRelationTypesResponse response = new GetRelationTypesResponse();
        List<RelationType> relationTypes = dbService.getRelationTypes(serverTicket);
        for (RelationType relationType : relationTypes) {
            response.getRelationTypes().add(relationType.toCommonsRelationType());
        }

        return response;
    }

    @Override
    public GetGraphByIdResponse getGraphById(
            @WebParam(partName = "getGraphById", name = "getGraphById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetGraphByIdRequest getGraphByIdRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getGraphById: {} {}", getGraphByIdRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        try {
            GetGraphByIdResponse getGraphByIdResponse = new GetGraphByIdResponse();
            Graph graph = graphService.getGraph(getGraphByIdRequest.getObjectId(), getGraphByIdRequest.getDistance(), serverTicket);
            getGraphByIdResponse.setGraph(graph.toCommonsGraph());

            return getGraphByIdResponse;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetRelatedObjectsByIdResponse getRelatedObjectsById(
            @WebParam(partName = "getRelatedObjectsById", name = "getRelatedObjectsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelatedObjectsByIdRequest getRelatedObjectsByIdRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getRelatedObjectsById: {} {}", getRelatedObjectsByIdRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);
        try {
            GetRelatedObjectsByIdResponse getRelatedObjectsByIdResponse = new GetRelatedObjectsByIdResponse();
            Graph graph = graphService.getRelatedObjects(getRelatedObjectsByIdRequest.getObjectId(), serverTicket);
            getRelatedObjectsByIdResponse.setGraph(graph.toCommonsGraph());

            return getRelatedObjectsByIdResponse;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public AddDocumentResponse addDocument(
            @WebParam(partName = "addDocument", name = "addDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            AddDocumentRequest addDocumentRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation addDocument: {} {}", addDocumentRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        long documentId = dbService.addDocument(addDocumentRequest.getText(), serverTicket);

        AddDocumentResponse addDocumentResponse = new AddDocumentResponse();
        addDocumentResponse.setDocumentId(documentId);

        return new AddDocumentResponse();
    }

    @Override
    public SplitObjectResponse splitObject(
            @WebParam(partName = "splitObject", name = "splitObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            SplitObjectRequest splitObjectRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) throws IdNotFoundException {

        LOG.debug("Executing operation splitObject: {} {}", splitObjectRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        try {
            boolean result = dbService.splitObject(splitObjectRequest.getObjectId(), serverTicket);

            SplitObjectResponse response = new SplitObjectResponse();
            response.setResult(result);

            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetPathByIdResponse getPathById(
            @WebParam(partName = "getPathById", name = "getPathById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetPathByIdRequest getPathByIdRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getPathById: {} {}", getPathByIdRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        try {
            GetPathByIdResponse getPathByIdResponse = new GetPathByIdResponse();
            Graph graph = graphService.getPath(getPathByIdRequest.getStartObjectId(), getPathByIdRequest.getTargetObjectId(), serverTicket);
            getPathByIdResponse.setGraph(graph.toCommonsGraph());

            return getPathByIdResponse;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public GetDocumentsResponse getDocuments(
            @WebParam(partName = "getDocuments", name = "getDocuments", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getDocuments,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getDocuments: {} {}", getDocuments, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        GetDocumentsResponse response = new GetDocumentsResponse();
        List<Document> documents = dbService.getDocuments(serverTicket);
        for (Document document : documents) {
            response.getDocuments().add(document.toCommonsDocument());
        }

        return new GetDocumentsResponse();
    }

    @Override
    public GetRelationsByTypeIdResponse getRelationsByTypeId(
            @WebParam(partName = "getRelationsByTypeId", name = "getRelationsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelationsByTypeIdRequest getRelationsByTypeIdRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getRelationsByTypeId: {} {}", getRelationsByTypeIdRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        GetRelationsByTypeIdResponse response = new GetRelationsByTypeIdResponse();
        List<Relation> relations = dbService.getRelations(getRelationsByTypeIdRequest.getRelationTypeId(), serverTicket);
        for (Relation relation : relations) {
            response.getRelations().add(relation.toCommonsRelation());
        }

        return response;
    }

    @Override
    public GetObjectsByTypeIdResponse getObjectsByTypeId(
            @WebParam(partName = "getObjectsByTypeId", name = "getObjectsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsByTypeIdRequest getObjectsByTypeIdRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getObjectsByTypeId: {} {}", getObjectsByTypeIdRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        GetObjectsByTypeIdResponse response = new GetObjectsByTypeIdResponse();
        List<Object> objects = dbService.getObjects(getObjectsByTypeIdRequest.getObjectTypeId(), serverTicket);
        for (Object object : objects) {
            response.getObjects().add(object.toCommonsObject());
        }

        return response;
    }

    @Override
    public GetObjectResponse getObject(
            @WebParam(partName = "getObject", name = "getObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectRequest getObjectRequest,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getObject: {} {}", getObjectRequest, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        try {
            GetObjectResponse response = new GetObjectResponse();
            Object object = dbService.getObject(getObjectRequest.getObjectId(), serverTicket);
            response.setObject(object.toCommonsObject());

            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }

    @Override
    public MergeObjectsResponse mergeObjects(
            @WebParam(partName = "mergeObjects", name = "mergeObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            MergeObjectsRequest mergeObjects,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) throws IdNotFoundException {

        LOG.debug("Executing operation mergeObjects: {} {}", mergeObjects, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        try {
            MergeObjectsResponse response = new MergeObjectsResponse();
            long objectId = dbService.mergeObjects(mergeObjects.getObject1Id(), mergeObjects.getObject2Id(), serverTicket);
            response.setObjectId(objectId);

            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }
    }
}