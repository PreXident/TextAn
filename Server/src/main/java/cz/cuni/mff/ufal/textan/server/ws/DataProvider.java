package cz.cuni.mff.ufal.textan.server.ws;


import cz.cuni.mff.ufal.textan.commons.models.*;
import cz.cuni.mff.ufal.textan.commons.models.Object;
import cz.cuni.mff.ufal.textan.commons.models.Void;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;

/**
 * For now only mocking database access.
 */
@javax.jws.WebService(
        serviceName = "DataProviderService",
        portName = "DataProviderWS",
        targetNamespace = "http://ws.commons.textan.ufal.mff.cuni.cz",
        //wsdlLocation = "DataProvider.wsdl", //TODO: what is right path to wsdl?!
        endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.DataProvider")
public class DataProvider implements cz.cuni.mff.ufal.textan.commons.ws.DataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DataProvider.class);

    @Override
    public GetObjectsResponse getObjects(
            @WebParam(partName = "getObjects", name = "getObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") Void getObjects,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation getObjects");

        return null;
    }

    @Override
    public boolean updateDocument(
            @WebParam(name = "text", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") String text,
            @WebParam(name = "documentId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") int documentId,
            @WebParam(name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation updateDocument");

        return false;
    }

    @Override
    public Object getObjectTypes(
            @WebParam(partName = "getObjectTypes", name = "getObjectTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") Void getObjectTypes,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation getObjectTypes");
        return null;
    }

    @Override
    public GetDocumentByIdResponse getDocumentById(
            @WebParam(partName = "getDocumentById", name = "getDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetDocumentById getDocumentById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getDocumentById");

        return null;
    }

    @Override
    public GetRelationTypesResponse getRelationTypes(
            @WebParam(partName = "getRelationTypes", name = "getRelationTypes", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") Void getRelationTypes,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation getRelationTypes");

        return null;
    }

    @Override
    public GetGraphByIdResponse getGraphById(
            @WebParam(partName = "getGraphById", name = "getGraphById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetGraphById getGraphById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getGraphById");

        return null;
    }

    @Override
    public GetRelationsByTypeIdResponse getRelatedObjectsById(
            @WebParam(partName = "getRelatedObjectsById", name = "getRelatedObjectsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetRelatedObjectsById getRelatedObjectsById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getRelatedObjectsById");

        return null;
    }

    @Override
    public GetObjectsByTypeResponse getObjectsByType(
            @WebParam(partName = "getObjectsByType", name = "getObjectsByType", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetObjectsByType getObjectsByType,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation getObjectsById");

        return null;
    }

    @Override
    public int addDocument(
            @WebParam(name = "text", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") String text,
            @WebParam(name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation addDocument");

        return 0;
    }

    @Override
    public boolean splitObject(
            @WebParam(name = "objectId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") int objectId,
            @WebParam(name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation splitObject");

        return false;
    }

    @Override
    public GetPathByIdResponse getPathById(
            @WebParam(partName = "getPathById", name = "getPathById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetPathById getPathById,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getPathById");

        return null;
    }

    @Override
    public GetDocumentsResponse getDocuments(
            @WebParam(partName = "getDocuments", name = "getDocuments", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") Void getDocuments,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation getDocuments");

        return null;
    }

    @Override
    public GetRelationsByTypeResponse getRelationsByType(
            @WebParam(partName = "getRelationsByType", name = "getRelationsByType", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetRelationsByType getRelationsByType,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation getRelationsByType");

        return null;
    }

    @Override
    public GetRelationsByTypeIdResponse getRelationsByTypeId(
            @WebParam(partName = "getRelationsByTypeId", name = "getRelationsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetRelationsByTypeId getRelationsByTypeId,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getRelationsByTypeId");

        return null;
    }

    @Override
    public GetObjectsByTypeIdResponse getObjectsByTypeId(
            @WebParam(partName = "getObjectsByTypeId", name = "getObjectsByTypeId", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetObjectsByTypeId getObjectsByTypeId,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getObjectsByTypeId");

        return null;
    }

    @Override
    public GetObjectResponse getObject(
            @WebParam(partName = "getObject", name = "getObject", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") GetObject getObject,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket)
            throws IdNotFoundException {

        LOG.debug("Executing operation getObject");

        return null;
    }

    @Override
    public MergeObjectsResponse mergeObjects(
            @WebParam(partName = "mergeObjects", name = "mergeObjects", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz") MergeObjects mergeObjects,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true) Ticket ticket) {

        LOG.debug("Executing operation mergeObjects");

        return null;
    }
}
