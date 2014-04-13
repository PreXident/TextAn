package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.EditingTicket;
import cz.cuni.mff.ufal.textan.commons.models.Entity;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import cz.cuni.mff.ufal.textan.server.services.NamedEntityRecognizerService;
import cz.cuni.mff.ufal.textan.server.services.ObjectAssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@javax.jws.WebService(
        serviceName = "DocumentProcessorService",
        portName = "DocumentProcessor",
        targetNamespace = "http://ws.commons.textan.ufal.mff.cuni.cz",
        wsdlLocation = "classpath:wsdl/DocumentProcessor.wsdl",
        endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor")
public class DocumentProcessor implements cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentProcessor.class);

    private final NamedEntityRecognizerService namedEntityService;
    private final ObjectAssignmentService objectAssignmentService;

    public DocumentProcessor(NamedEntityRecognizerService namedEntityService, ObjectAssignmentService objectAssignmentService) {
        this.namedEntityService = namedEntityService;
        this.objectAssignmentService = objectAssignmentService;
    }

    @Override
    public GetAssignmentsFromStringResponse getAssignmentsFromString(
            @WebParam(partName = "getAssignmentsFromString", name = "getAssignmentsFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetAssignmentsFromString getAssignmentsFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getObjectsFromString: {} {}", getAssignmentsFromString, editingTicket);

        //TODO: change assignments to send set of objects and list of assignment

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        List<cz.cuni.mff.ufal.textan.server.models.Entity> serverEntities = getAssignmentsFromString.getEntities().getEntities().stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Entity::fromCommonsEntity)
                .collect(Collectors.toList());

        List<Assignment> assignments = objectAssignmentService.getAssignments(getAssignmentsFromString.getText(), serverEntities, serverTicket).stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Assignment::toCommonsAssignment)
                .collect(Collectors.toList());

        GetAssignmentsFromStringResponse response = new GetAssignmentsFromStringResponse();
        response.getAssignments().addAll(assignments);

        return response;
    }

    @Override
    public SaveProcessedDocumentByIdResponse saveProcessedDocumentById(
            @WebParam(partName = "saveProcessedDocumentById", name = "saveProcessedDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            SaveProcessedDocumentById saveProcessedDocumentById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation saveProcessedDocumentById: {} {}", saveProcessedDocumentById, editingTicket);

        return new SaveProcessedDocumentByIdResponse();
    }

    @Override
    public GetProblemsResponse getProblems(
            @WebParam(partName = "getProblems", name = "getProblems", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetProblems getProblems,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getProblems: {} {}", getProblems, editingTicket);

        return new GetProblemsResponse();
    }

    @Override
    public GetAssignmentsByIdResponse getAssignmentsById(
            @WebParam(partName = "getAssignmentsById", name = "getAssignmentsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetAssignmentsById getAssignmentsById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) throws IdNotFoundException {

        LOG.debug("Executing operation getObjectsById: {} {}", getAssignmentsById, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        List<cz.cuni.mff.ufal.textan.server.models.Entity> serverEntities = getAssignmentsById.getEntities().getEntities().stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Entity::fromCommonsEntity)
                .collect(Collectors.toList());

        List<Assignment> assignments;
        try {

            assignments = objectAssignmentService.getAssignments(getAssignmentsById.getId(), serverEntities, serverTicket).stream()
                    .map(cz.cuni.mff.ufal.textan.server.models.Assignment::toCommonsAssignment)
                    .collect(Collectors.toList());

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(),exceptionBody);
        }

        GetAssignmentsByIdResponse response = new GetAssignmentsByIdResponse();
        response.getAssignments().addAll(assignments);

        return response;
    }

    @Override
    public GetEntitiesFromStringResponse getEntitiesFromString(
            @WebParam(partName = "getEntitiesFromString", name = "getEntitiesFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEntitiesFromString getEntitiesFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getEntitiesFromString: {} {}", getEntitiesFromString, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        List<Entity> entities = namedEntityService.getEntities(getEntitiesFromString.getText(), serverTicket).stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Entity::toCommonsEntity)
                .collect(Collectors.toList());

        GetEntitiesFromStringResponse response = new GetEntitiesFromStringResponse();
        response.getEntities().addAll(entities);

        return response;
    }

    @Override
    public GetEntitiesByIdResponse getEntitiesById(
            @WebParam(partName = "getEntitiesById", name = "getEntitiesById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEntitiesById getEntitiesById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) throws IdNotFoundException{

        LOG.debug("Executing operation getEntitiesById: {} {}", getEntitiesById, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        List<Entity> entities;
        try {
            entities = namedEntityService.getEntities(getEntitiesById.getDocumentId(), serverTicket).stream()
                    .map(cz.cuni.mff.ufal.textan.server.models.Entity::toCommonsEntity)
                    .collect(Collectors.toList());
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException();
        }

        GetEntitiesByIdResponse response = new GetEntitiesByIdResponse();
        response.getEntities().addAll(entities);

        return new GetEntitiesByIdResponse();
    }

    @Override
    public SaveProcessedDocumentFromStringResponse saveProcessedDocumentFromString(
            @WebParam(partName = "saveProcessedDocumentFromString", name = "saveProcessedDocumentFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            SaveProcessedDocumentFromString saveProcessedDocumentFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation saveProcessedDocumentFromString: {} {}", saveProcessedDocumentFromString, editingTicket);

        return new SaveProcessedDocumentFromStringResponse();
    }

    @Override
    public GetEditingTicketResponse getEditingTicket(
            @WebParam(partName = "getEditingTicket", name = "getEditingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEditingTicket getEditingTicket,
            @WebParam(partName = "ticket", name = "ticket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            Ticket ticket) {

        LOG.debug("Executing operation getEditingTicket: {} {}", getEditingTicket, ticket);

        final GetEditingTicketResponse response = new GetEditingTicketResponse();
        final EditingTicket t = new EditingTicket();
        t.setTimestamp(new Date());
        t.setUsername(ticket.getUsername());
        response.setEditingTicket(t);

        return response;
    }
}