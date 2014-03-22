package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.EditingTicket;
import cz.cuni.mff.ufal.textan.commons.models.Entity;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import java.util.Date;
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
        LOG.debug("Executing operation getEntitiesFromString");
        final GetEntitiesFromStringResponse response = new GetEntitiesFromStringResponse();
        final Entity entity = new Entity();
        entity.setPosition(0);
        entity.setLength(getEntitiesFromString.getText().length());
        entity.setType(0);
        entity.setValue(getEntitiesFromString.getText());
        response.getEntities().add(entity);
        return response;
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
        final GetEditingTicketResponse response = new GetEditingTicketResponse();
        final EditingTicket t = new EditingTicket();
        t.setTimestamp(new Date());
        t.setUsername(ticket.getUsername());
        response.setEditingTicket(t);
        return response;
    }
}