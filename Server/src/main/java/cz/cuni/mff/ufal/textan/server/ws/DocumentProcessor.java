package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.commons.ws.DocumentAlreadyProcessedException;
import cz.cuni.mff.ufal.textan.commons.ws.DocumentChangedException;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;
import cz.cuni.mff.ufal.textan.server.models.Assignment;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Entity;
import cz.cuni.mff.ufal.textan.server.models.JoinedObject;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.server.models.Occurrence;
import cz.cuni.mff.ufal.textan.server.models.Relation;
import cz.cuni.mff.ufal.textan.server.services.NamedEntityRecognizerService;
import cz.cuni.mff.ufal.textan.server.services.ObjectAssignmentService;
import cz.cuni.mff.ufal.textan.server.services.SaveService;
import cz.cuni.mff.ufal.textan.server.services.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor}.
 */
@javax.jws.WebService(
        serviceName = "DocumentProcessorService",
        portName = "DocumentProcessorPort",
        targetNamespace = "http://ws.commons.textan.ufal.mff.cuni.cz",
        wsdlLocation = "classpath:wsdl/DocumentProcessor.wsdl",
        endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor")
public class DocumentProcessor implements cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentProcessor.class);

    private final NamedEntityRecognizerService namedEntityService;
    private final ObjectAssignmentService objectAssignmentService;
    private final SaveService saveService;
    private final TicketService ticketService;

    public DocumentProcessor(
            NamedEntityRecognizerService namedEntityService,
            ObjectAssignmentService objectAssignmentService,
            SaveService saveService,
            TicketService ticketService) {

        this.namedEntityService = namedEntityService;
        this.objectAssignmentService = objectAssignmentService;
        this.saveService = saveService;
        this.ticketService = ticketService;
    }

    /**
     * Returns editing ticket needed for report processing.
     * @param getEditingTicketRequest request
     * @return editing ticket needed for report processing
     */
    @Override
    public GetEditingTicketResponse getEditingTicket(
            @WebParam(partName = "getEditingTicket", name = "getEditingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEditingTicketRequest getEditingTicketRequest) {

        LOG.info("Executing operation getEditingTicket: {}", getEditingTicketRequest);

        EditingTicket editingTicket = ticketService.createTicket().toCommonsEditingTicket();

        final GetEditingTicketResponse response = new GetEditingTicketResponse();
        response.setEditingTicket(editingTicket);

        LOG.info("Executed operation getEditingTicket: {}", response);
        return response;
    }

    /**
     * Recognizes named entities in the given report string.
     * @param getEntitiesFromStringRequest request containing report text
     * @param editingTicket editing ticket
     * @return list of recognized entities
     */
    @Override
    public GetEntitiesFromStringResponse getEntitiesFromString(
            @WebParam(partName = "getEntitiesFromString", name = "getEntitiesFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEntitiesFromStringRequest getEntitiesFromStringRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) {

        LOG.info("Executing operation getEntitiesFromString: {} {}", getEntitiesFromStringRequest, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        GetEntitiesFromStringResponse response = new GetEntitiesFromStringResponse();
        List<Entity> entities = namedEntityService.getEntities(getEntitiesFromStringRequest.getText(), serverTicket);
        for (Entity entity : entities) {
            response.getEntities().add(entity.toCommonsEntity());
        }

        LOG.info("Executed operation getEntitiesFromString: {}", response);
        return response;
    }

    /**
     * Recognizes named entities in the given document stored in the database.
     * @param getEntitiesByIdRequest request containing document id
     * @param editingTicket editing ticket
     * @return list of recognized entities
     * @throws DocumentChangedException if the document has been altered since processing started
     * @throws DocumentAlreadyProcessedException if the document has been already processed
     * @throws IdNotFoundException if no document with the given id exists
     */
    @Override
    public GetEntitiesByIdResponse getEntitiesById(
            @WebParam(partName = "getEntitiesById", name = "getEntitiesById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetEntitiesByIdRequest getEntitiesByIdRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) throws DocumentChangedException, DocumentAlreadyProcessedException, IdNotFoundException {

        LOG.info("Executing operation getEntitiesById: {} {}", getEntitiesByIdRequest, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        try {
            GetEntitiesByIdResponse response = new GetEntitiesByIdResponse();
            List<Entity> entities = namedEntityService.getEntities(getEntitiesByIdRequest.getDocumentId(), serverTicket);
            for (Entity entity : entities) {
                response.getEntities().add(entity.toCommonsEntity());
            }

            LOG.info("Executed operation getEntitiesById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation getEntitiesById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.DocumentAlreadyProcessedException e) {
            LOG.warn("Problem in operation getEntitiesById.", e);
            throw translateDocumentAlreadyProcessedException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.DocumentChangedException e) {
            LOG.warn("Problem in operation getEntitiesById.", e);
            throw translateDocumentChangedException(e);
        }
    }

    /**
     * Assigns objects from the database to recognized entities in given report string.
     * @param getAssignmentsFromStringRequest request containing report text and recognized entities
     * @param editingTicket editing ticket
     * @return list of possible object assignments with scores for each entity
     */
    @Override
    public GetAssignmentsFromStringResponse getAssignmentsFromString(
            @WebParam(partName = "getAssignmentsFromString", name = "getAssignmentsFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetAssignmentsFromStringRequest getAssignmentsFromStringRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) {

        LOG.info("Executing operation getObjectsFromString: {} {}", getAssignmentsFromStringRequest, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        List<Entity> serverEntities = getAssignmentsFromStringRequest.getEntities().stream()
                .map(Entity::fromCommonsEntity)
                .collect(Collectors.toList());

        GetAssignmentsFromStringResponse response = new GetAssignmentsFromStringResponse();
        List<Assignment> assignments = objectAssignmentService.getAssignments(getAssignmentsFromStringRequest.getText(), serverEntities, serverTicket);
        for (Assignment assignment : assignments) {
            response.getAssignments().add(assignment.toCommonsAssignment());
        }

        LOG.info("Executed operation getObjectsFromString: {}", response);
        return response;
    }

    /**
     * Assigns objects from the database to recognized entities in report with given id.
     * @param getAssignmentsByIdRequest request containing report id and recognized entities
     * @param editingTicket editing ticket
     * @return list of possible object assignments with scores for each entity
     * @throws DocumentChangedException if the document has been altered since processing started
     * @throws DocumentAlreadyProcessedException if the document has been already processed
     * @throws IdNotFoundException if no document with the given id exists
     */
    @Override
    public GetAssignmentsByIdResponse getAssignmentsById(
            @WebParam(partName = "getAssignmentsById", name = "getAssignmentsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetAssignmentsByIdRequest getAssignmentsByIdRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) throws DocumentChangedException, DocumentAlreadyProcessedException, IdNotFoundException {

        LOG.info("Executing operation getObjectsById: {} {}", getAssignmentsByIdRequest, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        List<cz.cuni.mff.ufal.textan.server.models.Entity> serverEntities = getAssignmentsByIdRequest.getEntities().stream()
                .map(cz.cuni.mff.ufal.textan.server.models.Entity::fromCommonsEntity)
                .collect(Collectors.toList());

        try {
            GetAssignmentsByIdResponse response = new GetAssignmentsByIdResponse();
            List<Assignment> assignments = objectAssignmentService.getAssignments(getAssignmentsByIdRequest.getId(), serverEntities, serverTicket);
            for (Assignment assignment : assignments) {
                response.getAssignments().add(assignment.toCommonsAssignment());
            }

            LOG.info("Executed operation getObjectsById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation saveProcessedDocumentById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.DocumentAlreadyProcessedException e) {
            LOG.warn("Problem in operation saveProcessedDocumentById.", e);
            throw translateDocumentAlreadyProcessedException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.DocumentChangedException e) {
            LOG.warn("Problem in operation saveProcessedDocumentById.", e);
            throw translateDocumentChangedException(e);
        }
    }

    /**
     * Recognizes relations in the given report string.
     * TODO recognizing relations not implemented.
     * @param getRelationsFromStringRequest request containing report text, recognized objects and their occurrences
     * @param editingTicket editing ticket
     * @return list of recognized relations
     */
    @Override
    public GetRelationsFromStringResponse getRelationsFromString(
            @WebParam(partName = "getRelationsFromStringRequest", name = "getRelationsFromStringRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetRelationsFromStringRequest getRelationsFromStringRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) {

        LOG.info("Executing operation getRelationsFromString: {} {}", getRelationsFromStringRequest, editingTicket);
        GetRelationsFromStringResponse response = new GetRelationsFromStringResponse();
        LOG.info("Executed operation getRelationsFromString: {}", response);
        return response;
    }

    /**
     * Recognizes relations in the report with the given id.
     * TODO recognizing relations not implemented.
     * @param getRelationsByIdRequest request containing report id, recognized objects and their occurrences
     * @param editingTicket editing ticket
     * @return list of recognized relations
     * @throws DocumentChangedException if the document has been altered since processing started
     * @throws DocumentAlreadyProcessedException if the document has been already processed
     * @throws IdNotFoundException if no document with the given id exists
     */
    @Override
    public GetRelationsByIdResponse getRelationsById(
            @WebParam(partName = "getRelationsByIdRequest", name = "getRelationsByIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetRelationsByIdRequest getRelationsByIdRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) throws DocumentChangedException, DocumentAlreadyProcessedException, IdNotFoundException {

        LOG.info("Executing operation getRelationsById: {} {}", getRelationsByIdRequest, editingTicket);
        GetRelationsByIdResponse response = new GetRelationsByIdResponse();
        LOG.info("Executed operation getRelationsById: {}", response);
        return response;
    }

    /**
     * Saves new report to the database processed from string.
     * @param saveProcessedDocumentFromStringRequest request containing report text, recognized objects, recognized relations, their occurrences and force indicator
     * @param editingTicket editing ticket
     * @return true if the report was saved, false otherwise
     * @throws IdNotFoundException if any id error occurs
     */
    @Override
    public SaveProcessedDocumentFromStringResponse saveProcessedDocumentFromString(
            @WebParam(partName = "saveProcessedDocumentFromString", name = "saveProcessedDocumentFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            SaveProcessedDocumentFromStringRequest saveProcessedDocumentFromStringRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) throws IdNotFoundException {

        LOG.info("Executing operation saveProcessedDocumentFromString: {} {}", saveProcessedDocumentFromStringRequest, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        try {
            List<Object> objects = saveProcessedDocumentFromStringRequest.getObjects().stream()
                    .map(Object::fromCommonsObject)
                    .collect(Collectors.toList());

            Map<Long, Object> objectsMap = new HashMap<>();
            for (Object object : objects) {
                objectsMap.put(object.getId(), object);
            }

            List<Pair<Long, Occurrence>> objectOccurrences = saveProcessedDocumentFromStringRequest.getObjectOccurrences().stream()
                    .map(o -> new Pair<>(o.getObjectId(), Occurrence.fromCommonsOccurrence(o.getAlias())))
                    .collect(Collectors.toList());

            List<Relation> relations = new ArrayList<>();
            for (cz.cuni.mff.ufal.textan.commons.models.Relation relation : saveProcessedDocumentFromStringRequest.getRelations()) {
                relations.add(Relation.fromCommonsRelation(relation, objectsMap));
            }

            List<Pair<Long, Occurrence>> relationOccurrences = saveProcessedDocumentFromStringRequest.getRelationOccurrences().stream()
                    .map(o -> new Pair<>(o.getRelationId(), Occurrence.fromCommonsOccurrence(o.getAnchor())))
                    .collect(Collectors.toList());

            boolean result = saveService.save(
                    saveProcessedDocumentFromStringRequest.getText(),
                    objects,
                    objectOccurrences,
                    relations,
                    relationOccurrences,
                    saveProcessedDocumentFromStringRequest.isForce(),
                    serverTicket
            );

            SaveProcessedDocumentFromStringResponse response = new SaveProcessedDocumentFromStringResponse();
            response.setResult(result);

            LOG.info("Executed operation saveProcessedDocumentFromString: {}", response);
            return response;
        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation saveProcessedDocumentFromString.", e);
            throw translateIdNotFoundException(e);
        }
    }

    /**
     * Saves processed report with the given id.
     * @param saveProcessedDocumentByIdRequest request containing document id, recognized objects, recognized relations, their occurrences and force indicator
     * @param editingTicket editing ticket
     * @return true if the report was saved, false otherwise
     * @throws DocumentChangedException if the document has been altered since processing started
     * @throws DocumentAlreadyProcessedException if the document has been already processed
     * @throws IdNotFoundException if any id error occurs
     */
    @Override
    public SaveProcessedDocumentByIdResponse saveProcessedDocumentById(
            @WebParam(partName = "saveProcessedDocumentById", name = "saveProcessedDocumentById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            SaveProcessedDocumentByIdRequest saveProcessedDocumentByIdRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) throws DocumentChangedException, DocumentAlreadyProcessedException, IdNotFoundException {

        LOG.info("Executing operation saveProcessedDocumentById: {} {}", saveProcessedDocumentByIdRequest, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        try {
            List<Object> objects = saveProcessedDocumentByIdRequest.getObjects().stream()
                    .map(Object::fromCommonsObject)
                    .collect(Collectors.toList());

            Map<Long, Object> objectsMap = new HashMap<>();
            for (Object object : objects) {
                objectsMap.put(object.getId(), object);
            }

            List<Pair<Long, Occurrence>> objectOccurrences = saveProcessedDocumentByIdRequest.getObjectOccurrences().stream()
                    .map(o -> new Pair<>(o.getObjectId(), Occurrence.fromCommonsOccurrence(o.getAlias())))
                    .collect(Collectors.toList());

            List<Relation> relations = new ArrayList<>();
            for (cz.cuni.mff.ufal.textan.commons.models.Relation relation : saveProcessedDocumentByIdRequest.getRelations()) {
                relations.add(Relation.fromCommonsRelation(relation, objectsMap));
            }

            List<Pair<Long, Occurrence>> relationOccurrences = saveProcessedDocumentByIdRequest.getRelationOccurrences().stream()
                    .map(o -> new Pair<>(o.getRelationId(), Occurrence.fromCommonsOccurrence(o.getAnchor())))
                    .collect(Collectors.toList());

            boolean result = saveService.save(
                    saveProcessedDocumentByIdRequest.getDocumentId(),
                    objects,
                    objectOccurrences,
                    relations,
                    relationOccurrences,
                    saveProcessedDocumentByIdRequest.isForce(),
                    serverTicket
            );

            SaveProcessedDocumentByIdResponse response = new SaveProcessedDocumentByIdResponse();
            response.setResult(result);

            LOG.info("Executed operation saveProcessedDocumentById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation saveProcessedDocumentById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.DocumentAlreadyProcessedException e) {
            LOG.warn("Problem in operation saveProcessedDocumentById.", e);
            throw translateDocumentAlreadyProcessedException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.DocumentChangedException e) {
            LOG.warn("Problem in operation saveProcessedDocumentById.", e);
            throw translateDocumentChangedException(e);
        }
    }

    /**
     *
     * @param rewriteAndSaveProcessedDocumentByIdRequest
     * @param editingTicket
     * @return
     * @throws DocumentAlreadyProcessedException
     * @throws IdNotFoundException
     */
    /**
     * Saves processed report with the given id while overwriting its text.
     * @param rewriteAndSaveProcessedDocumentByIdRequest request containing document id, report text, recognized objects, recognized relations, their occurrences and force indicator
     * @param editingTicket editing ticket
     * @return true if the report was saved, false otherwise
     * @throws DocumentAlreadyProcessedException if the document has been already processed
     * @throws IdNotFoundException if any id error occurs
     */
    @Override
    public RewriteAndSaveProcessedDocumentByIdResponse rewriteAndSaveProcessedDocumentById(
            @WebParam(partName = "rewriteAndSaveProcessedDocumentByIdRequest", name = "rewriteAndSaveProcessedDocumentByIdRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            RewriteAndSaveProcessedDocumentByIdRequest rewriteAndSaveProcessedDocumentByIdRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) throws DocumentAlreadyProcessedException, IdNotFoundException {

        LOG.info("Executing operation rewriteAndSaveProcessedDocumentById: {} {}", rewriteAndSaveProcessedDocumentByIdRequest, editingTicket);
        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        try {
            List<Object> objects = rewriteAndSaveProcessedDocumentByIdRequest.getObjects().stream()
                    .map(Object::fromCommonsObject)
                    .collect(Collectors.toList());

            Map<Long, Object> objectsMap = new HashMap<>();
            for (Object object : objects) {
                objectsMap.put(object.getId(), object);
            }

            List<Pair<Long, Occurrence>> objectOccurrences = rewriteAndSaveProcessedDocumentByIdRequest.getObjectOccurrences().stream()
                    .map(o -> new Pair<>(o.getObjectId(), Occurrence.fromCommonsOccurrence(o.getAlias())))
                    .collect(Collectors.toList());

            List<Relation> relations = new ArrayList<>();
            for (cz.cuni.mff.ufal.textan.commons.models.Relation relation : rewriteAndSaveProcessedDocumentByIdRequest.getRelations()) {
                relations.add(Relation.fromCommonsRelation(relation, objectsMap));
            }

            List<Pair<Long, Occurrence>> relationOccurrences = rewriteAndSaveProcessedDocumentByIdRequest.getRelationOccurrences().stream()
                    .map(o -> new Pair<>(o.getRelationId(), Occurrence.fromCommonsOccurrence(o.getAnchor())))
                    .collect(Collectors.toList());

            boolean result = saveService.save(
                    rewriteAndSaveProcessedDocumentByIdRequest.getDocumentId(),
                    rewriteAndSaveProcessedDocumentByIdRequest.getText(),
                    objects,
                    objectOccurrences,
                    relations,
                    relationOccurrences,
                    rewriteAndSaveProcessedDocumentByIdRequest.isForce(),
                    serverTicket
            );

            RewriteAndSaveProcessedDocumentByIdResponse response = new RewriteAndSaveProcessedDocumentByIdResponse();
            response.setResult(result);

            LOG.info("Executed operation saveProcessedDocumentById: {}", response);
            return response;

        } catch (cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
            LOG.warn("Problem in operation saveProcessedDocumentById.", e);
            throw translateIdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.server.services.DocumentAlreadyProcessedException e) {
            LOG.warn("Problem in operation saveProcessedDocumentById.", e);
            throw translateDocumentAlreadyProcessedException(e);
        }
    }

    //    @Override
//    public GetProblemsByIdResponse getProblemsById(
//            @WebParam(partName = "getProblemsById", name = "getProblemsById", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
//            GetProblemsByIdRequest getProblemsByIdRequest,
//            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
//            EditingTicket editingTicket) throws IdNotFoundException {
//
//        LOG.info("Executing operation getProblems: {} {}", getProblemsByIdRequest, editingTicket);
//
//        return new GetProblemsByIdResponse();
//    }


//    @Override
//    public GetProblemsFromStringResponse getProblemsFromString(
//            @WebParam(partName = "getProblemsFromString", name = "getProblemsFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
//            GetProblemsFromStringRequest getProblemsFromStringRequest,
//            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
//            EditingTicket editingTicket) {
//
//        LOG.info("Executing operation getEditingTicket: {} {}", getProblemsFromStringRequest, editingTicket);
//
//        return new GetProblemsFromStringResponse();
//    }

    /**
     * Returns all possible problems or conflicts that occurred during report processing.
     * Ie. new objects, new relations and newly joined objects.
     * @param getProblemsRequest request
     * @param editingTicket editing ticket
     * @return all possible problems or conflicts that occurred during report processing
     */
    @Override
    public GetProblemsResponse getProblems(
            @WebParam(partName = "getProblemsFromString", name = "getProblemsFromString", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor")
            GetProblemsRequest getProblemsRequest,
            @WebParam(partName = "editingTicket", name = "editingTicket", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/documentProcessor", header = true)
            EditingTicket editingTicket) {

        LOG.info("Executing operation getProblems: {} {}", getProblemsRequest, editingTicket);

        cz.cuni.mff.ufal.textan.server.models.EditingTicket serverTicket = cz.cuni.mff.ufal.textan.server.models.EditingTicket.fromCommonsEditingTicket(editingTicket);

        Problems problems = saveService.getProblems(serverTicket);
        GetProblemsResponse response = new GetProblemsResponse();

        for (Object object : problems.getNewObjects()) {
            response.getNewObjects().add(object.toCommonsObject());
        }

        Set<cz.cuni.mff.ufal.textan.commons.models.Object> objects = new HashSet<>();
        for (Relation relation : problems.getNewRelations()) {
            response.getNewRelations().add(relation.toCommonsRelation());

            for (Triple<Object, String, Integer> inRelation : relation.getObjectsInRelation()) {
                objects.add(inRelation.getFirst().toCommonsObject());
            }
        }
        response.getRelationObjects().addAll(objects);

        for (JoinedObject joinedObject : problems.getNewJoinedObjects()) {
            response.getNewJoinedObjects().add(joinedObject.toCommonsJoinedObject());
        }

        LOG.info("Executed operation getProblems: {}", response);
        return response;
    }

    private static IdNotFoundException translateIdNotFoundException(cz.cuni.mff.ufal.textan.server.services.IdNotFoundException e) {
        cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.IdNotFoundException();
        exceptionBody.setFieldName(e.getFieldName());
        exceptionBody.setFieldValue(e.getFieldValue());

        return new IdNotFoundException(e.getMessage(), exceptionBody);
    }

    private static DocumentAlreadyProcessedException translateDocumentAlreadyProcessedException(cz.cuni.mff.ufal.textan.server.services.DocumentAlreadyProcessedException e) {
        cz.cuni.mff.ufal.textan.commons.models.documentprocessor.DocumentAlreadyProcessedException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.documentprocessor.DocumentAlreadyProcessedException();
        exceptionBody.setDocumentId(e.getDocumentId());
        exceptionBody.setProcessedDate(e.getProcessedDate());

        return new DocumentAlreadyProcessedException(e.getMessage(), exceptionBody);
    }

    private static DocumentChangedException translateDocumentChangedException(cz.cuni.mff.ufal.textan.server.services.DocumentChangedException e) {
        cz.cuni.mff.ufal.textan.commons.models.documentprocessor.DocumentChangedException exceptionBody = new cz.cuni.mff.ufal.textan.commons.models.documentprocessor.DocumentChangedException();
        exceptionBody.setDocumentId(e.getDocumentId());
        exceptionBody.setDocumentVersion(e.getDocumentVersion());
        exceptionBody.setTicketVersion(e.getTicketVersion());

        return new DocumentChangedException(e.getMessage(), exceptionBody);
    }
}