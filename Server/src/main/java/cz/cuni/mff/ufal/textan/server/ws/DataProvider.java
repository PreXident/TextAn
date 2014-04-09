package cz.cuni.mff.ufal.textan.server.ws;


import cz.cuni.mff.ufal.textan.commons.models.*;
import cz.cuni.mff.ufal.textan.commons.models.Object;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.*;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import cz.cuni.mff.ufal.textan.server.services.DirectDataAccessService;
import cz.cuni.mff.ufal.textan.server.services.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import java.util.List;
import java.util.stream.Collectors;

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

        List<Object> objects = dbService.getObjects(serverTicket).stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Object::toCommonsObject)
                .collect(Collectors.toList());

        GetObjectsResponse getObjectsResponse = new GetObjectsResponse();
        getObjectsResponse.getObjects().addAll(objects);

        return getObjectsResponse;
    }

    @Override
    public UpdateDocumentResponse updateDocument(
            @WebParam(partName = "updateDocument", name = "updateDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            UpdateDocument updateDocument,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation updateDocument: {} {}", updateDocument, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        boolean result = dbService.updateDocument(updateDocument.getDocumentId(), updateDocument.getText(), serverTicket);

        UpdateDocumentResponse updateDocumentResponse = new UpdateDocumentResponse();
        updateDocumentResponse.setResult(result);

        return updateDocumentResponse;
    }

    @Override
    public GetRelationsResponse getRelations(
            @WebParam(partName = "getRelations", name = "getRelations", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getRelations,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getRelations: {} {}", getRelations, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        List<Relation> relations = dbService.getRelations(serverTicket).stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Relation::toCommonsRelation)
                .collect(Collectors.toList());

        GetRelationsResponse getRelationsResponse = new GetRelationsResponse();
        getRelationsResponse.getRelations().addAll(relations);

        return getRelationsResponse;
    }

    @Override
    public GetObjectTypesResponse getObjectTypes(
            @WebParam(partName = "getObjectTypes", name = "getObjectTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getObjectTypes,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getObjectTypes: {} {}", getObjectTypes, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        List<ObjectType> objectTypes = dbService.getObjectTypes(serverTicket).stream().map(cz.cuni.mff.ufal.textan.server.models.ObjectType::toCommonsObjectType)
                .collect(Collectors.toList());

        final GetObjectTypesResponse response = new GetObjectTypesResponse();
        response.getObjectTypes().addAll(objectTypes);

        return response;
    }

    @Override
    public GetDocumentByIdResponse getDocumentById(
            @WebParam(partName = "getDocumentById", name = "getDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetDocumentById getDocumentById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getDocumentById: {} {}", getDocumentById, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        Document document = dbService.getDocument(getDocumentById.getDocumentId(), serverTicket).toCommonsDocument();

        //TODO: add exception throwing

        GetDocumentByIdResponse getDocumentByIdResponse = new GetDocumentByIdResponse();
        getDocumentByIdResponse.setDocument(document);

        return getDocumentByIdResponse;
    }

    @Override
    public GetRelationTypesResponse getRelationTypes(
            @WebParam(partName = "getRelationTypes", name = "getRelationTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getRelationTypes,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getRelationTypes: {} {}", getRelationTypes, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        List<RelationType> relationTypes = dbService.getRelationTypes(serverTicket).stream()
                .map(cz.cuni.mff.ufal.textan.server.models.RelationType::toCommonsRelationType)
                .collect(Collectors.toList());

        final GetRelationTypesResponse response = new GetRelationTypesResponse();
        response.getRelationTypes().addAll(relationTypes);

        return response;
    }

    @Override
    public GetGraphByIdResponse getGraphById(
            @WebParam(partName = "getGraphById", name = "getGraphById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetGraphById getGraphById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getGraphById: {} {}", getGraphById, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        Graph graph = graphService.getGraph(getGraphById.getObjectId(), getGraphById.getDistance(), serverTicket).toCommonsGraph();

        GetGraphByIdResponse getGraphByIdResponse = new GetGraphByIdResponse();
        getGraphByIdResponse.setGraph(graph);

        return getGraphByIdResponse;
    }

    @Override
    public GetRelatedObjectsByIdResponse getRelatedObjectsById(
            @WebParam(partName = "getRelatedObjectsById", name = "getRelatedObjectsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelatedObjectsById getRelatedObjectsById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getRelatedObjectsById: {} {}", getRelatedObjectsById, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        Graph graph = graphService.getRelatedObjects(getRelatedObjectsById.getObjectId(), serverTicket).toCommonsGraph();

        GetRelatedObjectsByIdResponse getRelatedObjectsByIdResponse = new GetRelatedObjectsByIdResponse();
        getRelatedObjectsByIdResponse.setGraph(graph);

        return getRelatedObjectsByIdResponse;
    }

    @Override
    public AddDocumentResponse addDocument(
            @WebParam(partName = "addDocument", name = "addDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            AddDocument addDocument,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation addDocument: {} {}", addDocument, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        long documentId = dbService.addDocument(addDocument.getText(), serverTicket);

        AddDocumentResponse addDocumentResponse = new AddDocumentResponse();
        addDocumentResponse.setDocumentId(documentId);

        return new AddDocumentResponse();
    }

    @Override
    public SplitObjectResponse splitObject(
            @WebParam(partName = "splitObject", name = "splitObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            SplitObject splitObject,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation splitObject: {} {}", splitObject, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        //TODO: add some exceptions
        boolean result = dbService.splitObject(splitObject.getObjectId(), serverTicket);

        SplitObjectResponse splitObjectResponse = new SplitObjectResponse();
        splitObjectResponse.setResult(result);

        return splitObjectResponse;
    }

    @Override
    public GetPathByIdResponse getPathById(
            @WebParam(partName = "getPathById", name = "getPathById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetPathById getPathById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getPathById: {} {}", getPathById, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        Graph graph = graphService.getPath(getPathById.getStartObjectId(), getPathById.getTargetObjectId(), serverTicket).toCommonsGraph();

        GetPathByIdResponse getPathByIdResponse = new GetPathByIdResponse();
        getPathByIdResponse.setGraph(graph);

        return getPathByIdResponse;
    }

    @Override
    public GetDocumentsResponse getDocuments(
            @WebParam(partName = "getDocuments", name = "getDocuments", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getDocuments,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getDocuments: {} {}", getDocuments, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        List<Document> documents = dbService.getDocuments(serverTicket).stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Document::toCommonsDocument)
                .collect(Collectors.toList());

        GetDocumentsResponse getDocumentsResponse = new GetDocumentsResponse();
        getDocumentsResponse.getDocuments().addAll(documents);

        return new GetDocumentsResponse();
    }

    @Override
    public GetRelationsByTypeIdResponse getRelationsByTypeId(
            @WebParam(partName = "getRelationsByTypeId", name = "getRelationsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelationsByTypeId getRelationsByTypeId,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getRelationsByTypeId: {} {}", getRelationsByTypeId, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        List<Relation> relations = dbService.getRelations(getRelationsByTypeId.getRelationTypeId(), serverTicket).stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Relation::toCommonsRelation)
                .collect(Collectors.toList());

        GetRelationsByTypeIdResponse getRelationsByTypeIdResponse = new GetRelationsByTypeIdResponse();
        getRelationsByTypeIdResponse.getRelations().addAll(relations);

        return getRelationsByTypeIdResponse;
    }

    @Override
    public GetObjectsByTypeIdResponse getObjectsByTypeId(
            @WebParam(partName = "getObjectsByTypeId", name = "getObjectsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsByTypeId getObjectsByTypeId,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getObjectsByTypeId: {} {}", getObjectsByTypeId, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        List<Object> objects = dbService.getObjects(getObjectsByTypeId.getObjectTypeId(), serverTicket).stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Object::toCommonsObject)
                .collect(Collectors.toList());

        GetObjectsByTypeIdResponse getObjectsByTypeIdResponse = new GetObjectsByTypeIdResponse();
        getObjectsByTypeIdResponse.getObjects().addAll(objects);

        return getObjectsByTypeIdResponse;
    }

    @Override
    public GetObjectResponse getObject(
            @WebParam(partName = "getObject", name = "getObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObject getObject,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getObject: {} {}", getObject, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        //TODO add exceptions
        Object object = dbService.getObject(getObject.getObjectId(), serverTicket).toCommonsObject();

        GetObjectResponse getObjectResponse = new GetObjectResponse();
        getObjectResponse.setObject(object);

        return getObjectResponse;
    }

    @Override
    public MergeObjectsResponse mergeObjects(
            @WebParam(partName = "mergeObjects", name = "mergeObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            MergeObjects mergeObjects,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation mergeObjects: {} {}", mergeObjects, ticket);

        cz.cuni.mff.ufal.textan.server.models.Ticket serverTicket = cz.cuni.mff.ufal.textan.server.models.Ticket.fromCommonsTicket(ticket);

        long objectId = dbService.mergeObjects(mergeObjects.getObject1Id(), mergeObjects.getObject2Id(), serverTicket);

        MergeObjectsResponse mergeObjectsResponse = new MergeObjectsResponse();
        mergeObjectsResponse.setObjectId(objectId);

        return mergeObjectsResponse;
    }
}