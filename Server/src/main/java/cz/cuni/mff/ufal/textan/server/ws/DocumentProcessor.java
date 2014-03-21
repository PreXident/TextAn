package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.EditingTicket;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;

@javax.jws.WebService(
        serviceName = "DocumentProcessorService",
        portName = "DocumentProcessor",
        targetNamespace = "http://ws.commons.textan.ufal.mff.cuni.cz",
        wsdlLocation = "classpath:wsdl/DocumentProcessor.wsdl",
        endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor")
public class DocumentProcessor implements cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentProcessor.class);

    @Override
    public GetObjectsFromStringResponse getObjectsFromString(
            @WebParam(partName = "getObjectsFromString", name = "getObjectsFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetObjectsFromString getObjectsFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getObjectsFromString");

        return new GetObjectsFromStringResponse();
    }

    @Override
    public SaveProcessedDocumentByIdResponse saveProcessedDocumentById(
            @WebParam(partName = "saveProcessedDocumentById", name = "saveProcessedDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            SaveProcessedDocumentById saveProcessedDocumentById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation saveProcessedDocumentById");

        return new SaveProcessedDocumentByIdResponse();
    }

    @Override
    public GetProblemsResponse getProblems(
            @WebParam(partName = "getProblems", name = "getProblems", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetProblems getProblems,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getProblems");

        return new GetProblemsResponse();
    }

    @Override
    public GetObjectsByIdResponse getObjectsById(
            @WebParam(partName = "getObjectsById", name = "getObjectsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetObjectsById getObjectsById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getObjectsById");

        return new GetObjectsByIdResponse();
    }

    @Override
    public GetEntitiesFromStringResponse getEntitiesFromString(
            @WebParam(partName = "getEntitiesFromString", name = "getEntitiesFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEntitiesFromString getEntitiesFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getEntitiesFromString");

        return new GetEntitiesFromStringResponse();
    }

    @Override
    public GetEntitiesByIdResponse getEntitiesById(
            @WebParam(partName = "getEntitiesById", name = "getEntitiesById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEntitiesById getEntitiesById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getEntitiesById");

        return new GetEntitiesByIdResponse();
    }

    @Override
    public SaveProcessedDocumentFromStringResponse saveProcessedDocumentFromString(
            @WebParam(partName = "saveProcessedDocuemntFromString", name = "saveProcessedDocumentFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            SaveProcessedDocumentFromString saveProcessedDocumentFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation saveProcessedDocumentFromString");

        return new SaveProcessedDocumentFromStringResponse();
    }

    @Override
    public GetEditingTicketResponse getEditingTicket(
            @WebParam(partName = "getEditingTicket", name = "getEditingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEditingTicket getEditingTicket,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getEditingTicket");

        return new GetEditingTicketResponse();
    }
}