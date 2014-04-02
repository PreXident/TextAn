package cz.cuni.mff.ufal.textan.server.ws;


import cz.cuni.mff.ufal.textan.commons.models.Graph;
import cz.cuni.mff.ufal.textan.commons.models.Graph.Edges;
import cz.cuni.mff.ufal.textan.commons.models.Graph.Nodes;
import cz.cuni.mff.ufal.textan.commons.models.Relation;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.*;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
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

    @Override
    public GetObjectsResponse getObjects(
            @WebParam(partName = "getObjects", name = "getObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getObjects,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getObjects: {} {}", getObjects, ticket);

        final GetObjectsResponse response = new GetObjectsResponse();
        response.getObjects().addAll(MockDB.objects);

        return response;
    }

    @Override
    public UpdateDocumentResponse updateDocument(
            @WebParam(partName = "updateDocument", name = "updateDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            UpdateDocument updateDocument,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation updateDocument: {} {}", updateDocument, ticket);

        return new UpdateDocumentResponse();
    }

    @Override
    public GetObjectTypesResponse getObjectTypes(
            @WebParam(partName = "getObjectTypes", name = "getObjectTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getObjectTypes,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getObjectTypes: {} {}", getObjectTypes, ticket);

        final GetObjectTypesResponse response = new GetObjectTypesResponse();
        response.getObjectTypes().addAll(MockDB.objectTypes);

        return response;
    }

    @Override
    public GetDocumentByIdResponse getDocumentById(
            @WebParam(partName = "getDocumentById", name = "getDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetDocumentById getDocumentById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getDocumentById: {} {}", getDocumentById, ticket);

        return new GetDocumentByIdResponse();
    }

    @Override
    public GetRelationTypesResponse getRelationTypes(
            @WebParam(partName = "getRelationTypes", name = "getRelationTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getRelationTypes,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getRelationTypes: {} {}", getRelationTypes, ticket);

        final GetRelationTypesResponse response = new GetRelationTypesResponse();
        response.getRelationTypes().addAll(MockDB.relationTypes);

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

        final GetGraphByIdResponse response = new GetGraphByIdResponse();
        final Graph graph = new Graph();
        final Nodes nodes = new Nodes();
        nodes.getObjects().addAll(MockDB.objects);
        graph.setNodes(nodes);
        final Edges edges = new Edges();
        edges.getRelations().addAll(MockDB.relations);
        graph.setEdges(edges);
        response.setGraph(graph);

        return response;
    }

    @Override
    public GetRelationsByTypeIdResponse getRelatedObjectsById(
            @WebParam(partName = "getRelatedObjectsById", name = "getRelatedObjectsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelatedObjectsById getRelatedObjectsById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getRelatedObjectsById: {} {}", getRelatedObjectsById, ticket);

        final GetRelationsByTypeIdResponse response = new GetRelationsByTypeIdResponse();
        final List<Relation> rels =
                MockDB.relations.stream().filter(
                        rel -> rel.getObjectInRelationIds().getInRelations().stream().anyMatch(
                                inrel -> inrel.getObjectId() == getRelatedObjectsById.getObjectId()
                        )
                ).collect(Collectors.toList());
        response.getRelations().addAll(rels);

        return response;
    }

    @Override
    public GetObjectsByTypeResponse getObjectsByType(
            @WebParam(partName = "getObjectsByType", name = "getObjectsByType", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsByType getObjectsByType,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getObjectsByType: {} {}", getObjectsByType, ticket);

        final GetObjectsByTypeResponse response = new GetObjectsByTypeResponse();
        response.getObjects().addAll(
                MockDB.objects.stream().filter(
                        obj -> obj.getObjectType().getId() == getObjectsByType.getObjectType().getId()
                ).collect(Collectors.toList())
        );

        return response;
    }

    @Override
    public AddDocumentResponse addDocument(
            @WebParam(partName = "addDocument", name = "addDocument", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            AddDocument addDocument,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation addDocument: {} {}", addDocument, ticket);

        return new AddDocumentResponse();
    }

    @Override
    public SplitObjectResponse splitObject(
            @WebParam(partName = "splitObject", name = "splitObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            SplitObject splitObject,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation splitObject: {} {}", splitObject, ticket);

        return new SplitObjectResponse();
    }

    @Override
    public GetPathByIdResponse getPathById(
            @WebParam(partName = "getPathById", name = "getPathById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetPathById getPathById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getPathById: {} {}", getPathById, ticket);

        return new GetPathByIdResponse();
    }

    @Override
    public GetDocumentsResponse getDocuments(
            @WebParam(partName = "getDocuments", name = "getDocuments", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            Void getDocuments,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getDocuments: {} {}", getDocuments, ticket);

        return new GetDocumentsResponse();
    }

    @Override
    public GetRelationsByTypeResponse getRelationsByType(
            @WebParam(partName = "getRelationsByType", name = "getRelationsByType", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelationsByType getRelationsByType,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getRelationsByType: {} {}", getRelationsByType, ticket);

        final GetRelationsByTypeResponse response = new GetRelationsByTypeResponse();
        response.getRelations().addAll(
                MockDB.relations.stream().filter(
                        rel -> rel.getRelationType().getId() == getRelationsByType.getRelationType().getId()
                ).collect(Collectors.toList())
        );

        return response;
    }

    @Override
    public GetRelationsByTypeIdResponse getRelationsByTypeId(
            @WebParam(partName = "getRelationsByTypeId", name = "getRelationsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetRelationsByTypeId getRelationsByTypeId,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getRelationsByTypeId: {} {}", getRelationsByTypeId, ticket);

        final GetRelationsByTypeIdResponse response = new GetRelationsByTypeIdResponse();
        response.getRelations().addAll(
                MockDB.relations.stream().filter(
                        rel -> rel.getRelationType().getId() == getRelationsByTypeId.getRelationTypeId()
                ).collect(Collectors.toList())
        );

        return response;
    }

    @Override
    public GetObjectsByTypeIdResponse getObjectsByTypeId(
            @WebParam(partName = "getObjectsByTypeId", name = "getObjectsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObjectsByTypeId getObjectsByTypeId,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getObjectsByTypeId: {} {}", getObjectsByTypeId, ticket);

        final GetObjectsByTypeIdResponse response = new GetObjectsByTypeIdResponse();
        response.getObjects().addAll(
                MockDB.objects.stream().filter(
                        obj -> obj.getObjectType().getId() == getObjectsByTypeId.getObjectTypeId()
                ).collect(Collectors.toList())
        );

        return response;
    }

    @Override
    public GetObjectResponse getObject(
            @WebParam(partName = "getObject", name = "getObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            GetObject getObject,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getObject: {} {}", getObject, ticket);

        return new GetObjectResponse();
    }

    @Override
    public MergeObjectsResponse mergeObjects(
            @WebParam(partName = "mergeObjects", name = "mergeObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/dataProvider")
            MergeObjects mergeObjects,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation mergeObjects: {} {}", mergeObjects, ticket);

        return new MergeObjectsResponse();
    }
}