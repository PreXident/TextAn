package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.EditingTicket;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import cz.cuni.mff.ufal.textan.server.models.Assignment;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.server.models.Occurrence;
import cz.cuni.mff.ufal.textan.server.services.NamedEntityRecognizerService;
import cz.cuni.mff.ufal.textan.server.services.ObjectAssignmentService;
import cz.cuni.mff.ufal.textan.server.services.SaveService;
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
    private final SaveService saveService;

    public DocumentProcessor(
            NamedEntityRecognizerService namedEntityService,
            ObjectAssignmentService objectAssignmentService,
            SaveService saveService) {

        this.namedEntityService = namedEntityService;
        this.objectAssignmentService = objectAssignmentService;
        this.saveService = saveService;
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

        List<Entity> serverEntities = getAssignmentsFromString.getEntities().getEntities().stream()
                .map(Entity::fromCommonsEntity)
                .collect(Collectors.toList());

        GetAssignmentsFromStringResponse response = new GetAssignmentsFromStringResponse();
        List<Assignment> assignments = objectAssignmentService.getAssignments(getAssignmentsFromString.getText(), serverEntities, serverTicket);
        for (Assignment assignment : assignments) {
            response.getAssignments().add(assignment.toCommonsAssignment());
        }

        return response;
    }

    @Override
    public SaveProcessedDocumentByIdResponse saveProcessedDocumentById(
            @WebParam(partName = "saveProcessedDocumentById", name = "saveProcessedDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            SaveProcessedDocumentById saveProcessedDocumentById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) throws IdNotFoundException {

        LOG.debug("Executing operation saveProcessedDocumentById: {} {}", saveProcessedDocumentById, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        List<Object> objects = saveProcessedDocumentById.getObjects().stream()
                .map(Object::fromCommonsObject)
                .collect(Collectors.toList());

        List<Pair<Long, Occurrence>> objectOccurrences = saveProcessedDocumentById.getObjectOccurrences().stream()
                .map(o -> new Pair<>(o.getObjectId(), Occurrence.fromCommonsOccurrence(o.getAlias())))
                .collect(Collectors.toList());

        List<Relation> relations = saveProcessedDocumentById.getRelations().stream()
                .map(Relation::fromCommonsRelation)
                .collect(Collectors.toList());

        List<Pair<Long, Occurrence>> relationOccurrences = saveProcessedDocumentById.getRelationOccurrences().stream()
                .map(o -> new Pair<>(o.getRelationId(), Occurrence.fromCommonsOccurrence(o.getAnchor())))
                .collect(Collectors.toList());

        try {

            boolean result = saveService.save(
                    saveProcessedDocumentById.getDocumentId(),
                    objects,
                    objectOccurrences,
                    relations,
                    relationOccurrences,
                    saveProcessedDocumentById.isForce(),
                    serverTicket
            );

            SaveProcessedDocumentByIdResponse response = new SaveProcessedDocumentByIdResponse();
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
    public GetProblemsByIdResponse getProblemsById(
            @WebParam(partName = "getProblemsById", name = "getProblemsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetProblemsById getProblemsById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) throws IdNotFoundException {

        LOG.debug("Executing operation getProblems: {} {}", getProblemsById, editingTicket);

        return new GetProblemsByIdResponse();
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

        try {
            GetAssignmentsByIdResponse response = new GetAssignmentsByIdResponse();
            List<Assignment> assignments = objectAssignmentService.getAssignments(getAssignmentsById.getId(), serverEntities, serverTicket);
            for (Assignment assignment : assignments) {
                response.getAssignments().add(assignment.toCommonsAssignment());
            }

            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {

            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException(e.getMessage(), exceptionBody);
        }

    }

    @Override
    public GetEntitiesFromStringResponse getEntitiesFromString(
            @WebParam(partName = "getEntitiesFromString", name = "getEntitiesFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEntitiesFromString getEntitiesFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getEntitiesFromString: {} {}", getEntitiesFromString, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        GetEntitiesFromStringResponse response = new GetEntitiesFromStringResponse();
        List<Entity> entities = namedEntityService.getEntities(getEntitiesFromString.getText(), serverTicket);
        for (Entity entity : entities) {
            response.getEntities().add(entity.toCommonsEntity());
        }

        return response;
    }

    @Override
    public GetEntitiesByIdResponse getEntitiesById(
            @WebParam(partName = "getEntitiesById", name = "getEntitiesById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEntitiesById getEntitiesById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) throws IdNotFoundException {

        LOG.debug("Executing operation getEntitiesById: {} {}", getEntitiesById, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        try {
            GetEntitiesByIdResponse response = new GetEntitiesByIdResponse();
            List<Entity> entities = namedEntityService.getEntities(getEntitiesById.getDocumentId(), serverTicket);
            for (Entity entity : entities) {
                response.getEntities().add(entity.toCommonsEntity());
            }

            return new GetEntitiesByIdResponse();

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException();
        }
    }

    @Override
    public SaveProcessedDocumentFromStringResponse saveProcessedDocumentFromString(
            @WebParam(partName = "saveProcessedDocumentFromString", name = "saveProcessedDocumentFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            SaveProcessedDocumentFromString saveProcessedDocumentFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) throws IdNotFoundException {

        LOG.debug("Executing operation saveProcessedDocumentFromString: {} {}", saveProcessedDocumentFromString, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        List<Object> objects = saveProcessedDocumentFromString.getObjects().stream()
                .map(Object::fromCommonsObject)
                .collect(Collectors.toList());

        List<Pair<Long, Occurrence>> objectOccurrences = saveProcessedDocumentFromString.getObjectOccurrences().stream()
                .map(o -> new Pair<>(o.getObjectId(), Occurrence.fromCommonsOccurrence(o.getAlias())))
                .collect(Collectors.toList());

        List<Relation> relations = saveProcessedDocumentFromString.getRelations().stream()
                .map(Relation::fromCommonsRelation)
                .collect(Collectors.toList());

        List<Pair<Long, Occurrence>> relationOccurrences = saveProcessedDocumentFromString.getRelationOccurrences().stream()
                .map(o -> new Pair<>(o.getRelationId(), Occurrence.fromCommonsOccurrence(o.getAnchor())))
                .collect(Collectors.toList());

        try {

            boolean result = saveService.save(
                    saveProcessedDocumentFromString.getText(),
                    objects,
                    objectOccurrences,
                    relations,
                    relationOccurrences,
                    saveProcessedDocumentFromString.isForce(),
                    serverTicket
            );

            SaveProcessedDocumentFromStringResponse response = new SaveProcessedDocumentFromStringResponse();
            response.setResult(result);

            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
            exceptionBody.setFieldName(e.getFieldName());
            exceptionBody.setFieldValue(e.getFieldValue());

            throw new IdNotFoundException("", exceptionBody);
        }
    }

    @Override
    public GetProblemsFromStringResponse getProblemsFromString(
            @WebParam(partName = "getProblemsFromString", name = "getProblemsFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetProblemsFromString getProblemsFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getEditingTicket: {} {}", getProblemsFromString, editingTicket);

        return new GetProblemsFromStringResponse();
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