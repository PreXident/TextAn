package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.EditingTicket;
import cz.cuni.mff.ufal.textan.commons.models.Entity;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import cz.cuni.mff.ufal.textan.server.services.NamedEntityRecognizerService;
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

    public DocumentProcessor(NamedEntityRecognizerService namedEntityService) {
        this.namedEntityService = namedEntityService;
    }

    @Override
    public GetObjectsFromStringResponse getObjectsFromString(
            @WebParam(partName = "getObjectsFromString", name = "getObjectsFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetObjectsFromString getObjectsFromString,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getObjectsFromString: {} {}", getObjectsFromString, editingTicket);

        final GetObjectsFromStringResponse response = new GetObjectsFromStringResponse();
        for (Entity entity : getObjectsFromString.getEntities().getEntities()) {
            final GetObjectsFromStringResponse.Assignment assignment = new GetObjectsFromStringResponse.Assignment();
            assignment.setEntity(entity);
            final GetObjectsFromStringResponse.Assignment.Objects objects =
                    new GetObjectsFromStringResponse.Assignment.Objects();
            final GetObjectsFromStringResponse.Assignment.Objects.ObjectWithRating rating =
                    new GetObjectsFromStringResponse.Assignment.Objects.ObjectWithRating();
            rating.setObject(MockDB.objects.get(0));
            rating.setRating(1.0f);
            objects.getObjectWithRatings().add(rating);
            assignment.setObjects(objects);
            response.getAssignments().add(assignment);
        }

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
    public GetObjectsByIdResponse getObjectsById(
            @WebParam(partName = "getObjectsById", name = "getObjectsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetObjectsById getObjectsById,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz", header = true)
            EditingTicket editingTicket) {

        LOG.debug("Executing operation getObjectsById: {} {}", getObjectsById, editingTicket);

        final GetObjectsByIdResponse response = new GetObjectsByIdResponse();
        for (Entity entity : getObjectsById.getEntities().getEntities()) {
            final GetObjectsByIdResponse.Assignment assignment = new GetObjectsByIdResponse.Assignment();
            assignment.setEntity(entity);
            final GetObjectsByIdResponse.Assignment.Objects objects =
                    new GetObjectsByIdResponse.Assignment.Objects();
            final GetObjectsByIdResponse.Assignment.Objects.ObjectWithRating rating =
                    new GetObjectsByIdResponse.Assignment.Objects.ObjectWithRating();
            rating.setObject(MockDB.objects.get(0));
            rating.setRating(1.0f);
            objects.getObjectWithRatings().add(rating);
            assignment.setObjects(objects);
            response.getAssignments().add(assignment);
        }

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
            @WebParam(partName = "saveProcessedDocuemntFromString", name = "saveProcessedDocumentFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
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