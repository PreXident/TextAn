package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.EditingTicket;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetGraphById;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetGraphByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectTypesResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsByTypeId;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsByTypeIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetObjectsFromString.Entities;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetObjectsFromStringResponse.Assignment;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetObjectsFromStringResponse.Assignment.Objects.ObjectWithRating;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.SaveProcessedDocumentFromString.Objects;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.SaveProcessedDocumentFromString.Relations;
import cz.cuni.mff.ufal.textan.commons.ws.IDataProvider;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Main class controlling core manipulations with reports.
 * Handles all communicatioin with the server.
 */
public class Client {

    /** Settings of the application. Handle with care, they're shared. */
    final protected Properties settings;

    /** Instance of data provider. */
    protected IDataProvider dataProvider = null;

    /** Instance of document processor. */
    protected IDocumentProcessor documentProcessor = null;

    /**
     * Only constructor
     * @param settings application settings
     */
    public Client(final Properties settings) {
        this.settings = settings;
    }

    /**
     * Creates ticket with username specified in settings.
     * @return ticket with username specified in settings
     */
    private cz.cuni.mff.ufal.textan.commons.models.Ticket createTicket() {
        final cz.cuni.mff.ufal.textan.commons.models.Ticket ticket =
                new cz.cuni.mff.ufal.textan.commons.models.Ticket();
        ticket.setUsername(settings.getProperty("username"));
        return ticket;
    }

    /**
     * Returns {@link #documentProcessor}, it is created if needed.
     * @return document processor
     */
    private IDocumentProcessor getDocumentProcessor() {
        if (documentProcessor == null) {
            try {
                Service service = Service.create(
                        new URL("http://localhost:9100/soap/document?wsdl"),
                        new QName("http://ws.commons.textan.ufal.mff.cuni.cz",
                                "DocumentProcessorService"));
                // Endpoint Address
                String endpointAddress = "http://localhost:9100/soap/document";
                // Add a port to the Service
                service.addPort(
                        new QName("http://ws.commons.textan.ufal.mff.cuni.cz/DocumentProcessorService",
                                "DocumentProcessorPort"),
                        SOAPBinding.SOAP11HTTP_BINDING,
                        endpointAddress);
                documentProcessor = service.getPort(IDocumentProcessor.class);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new WebServiceException("Malformed URL!", e);
            }
        }
        return documentProcessor;
    }

    /**
     * Returns {@link #dataProvider}, it is created if needed.
     * @return data provider
     */
    private IDataProvider getDataProvider() throws WebServiceException {
        if (dataProvider == null) {
            try {
                Service service = Service.create(
                        new URL("http://localhost:9100/soap/data?wsdl"),
                        new QName("http://ws.commons.textan.ufal.mff.cuni.cz",
                                "DataProviderService"));
                // Endpoint Address
                String endpointAddress = "http://localhost:9100/soap/data";
                // Add a port to the Service
                service.addPort(
                        new QName("http://server.textan.ufal.mff.cuni.cz/DataProviderService",
                                "DataProviderPort"),
                        SOAPBinding.SOAP11HTTP_BINDING,
                        endpointAddress);
                dataProvider = service.getPort(IDataProvider.class);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new WebServiceException("Malformed URL!", e);
            }
        }
        return dataProvider;
    }

    /**
     * Returns entities identified in text.
     * @param ticket editing ticket
     * @param text text to process
     * @return entities identified in text
     * @see IDocumentProcessor#getEntitiesFromString(GetEntitiesFromString, EditingTicket)
     */
    public List<Entity> getEntities(final Ticket ticket, final String text) {
        final GetEntitiesFromString request = new GetEntitiesFromString();
        request.setText(text);
        final IDocumentProcessor docProc = getDocumentProcessor();
        final GetEntitiesFromStringResponse response =
                docProc.getEntitiesFromString(request, ticket.toTicket());
        return response.getEntities().stream()
                .map(Entity::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns centered graph with limited distance.
     * @param centerId center object id
     * @param distance maximal distance from center
     * @return centered graph with limited distance
     * @throws IdNotFoundException if object id is not found
     */
    public Graph getGraph(final int centerId, final int distance)
            throws IdNotFoundException {
        final GetGraphById request = new GetGraphById();
        request.setDistance(distance);
        request.setObjectId(centerId);
        try {
            final GetGraphByIdResponse response =
                    getDataProvider().getGraphById(request, createTicket());
            return new Graph(response.getGraph());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Fills candidates of entities.
     * @param ticket editing ticket
     * @param text report to process
     * @param entities where to store candidates
     * @see cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor#getObjectsFromString(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetObjectsFromString, cz.cuni.mff.ufal.textan.commons.models.EditingTicket)
     */
    public void getObjects(final Ticket ticket, final String text, final List<Entity> entities) {
        final Entities ents = new Entities();
        final Map<Integer, Entity> map = new HashMap<>();
        for (Entity entity : entities) {
            ents.getEntities().add(entity.toEntity());
            map.put(entity.getPosition(), entity);
        }

        final GetObjectsFromString request = new GetObjectsFromString();
        request.setText(text);
        request.setEntities(ents);

        final GetObjectsFromStringResponse response = getDocumentProcessor().getObjectsFromString(request, ticket.toTicket());

        for (Assignment assignment : response.getAssignments()) {
            final Entity ent = map.get(assignment.getEntity().getPosition());
            ent.getCandidates().clear();
            for (ObjectWithRating rating : assignment.getObjects().getObjectWithRatings()) {
                final double r = rating.getRating();
                final Object obj = new Object(rating.getObject());
                ent.getCandidates().put(r, obj);
            }
        }
    }

    /**
     * Returns list of all objects in the system with specified type.
     * @param typeId type id to filter
     * @return list of all objects in the system with specified type
     * @throws IdNotFoundException if id was not found
     * @see IDataProvider#getObjectsByTypeId(int)
     */
    public List<Object> getObjectsListByTypeId(final int typeId)
            throws IdNotFoundException {
        try {
            final GetObjectsByTypeId request = new GetObjectsByTypeId();
            request.setObjectTypeId(typeId);
            final GetObjectsByTypeIdResponse response =
                    getDataProvider().getObjectsByTypeId(request, createTicket());
            return response.getObjects().stream()
                    .map(Object::new)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Returns list of all objects in the system.
     * @return list of all objects in the system
     * @see IDataProvider#getObjects()
     */
    public List<Object> getObjectsList() {
        final GetObjectsResponse response =
                getDataProvider().getObjects(new Void(), createTicket());
        return response.getObjects().stream()
                .map(Object::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns set of all objects in the system.
     * @return set of all objects in the system
     * @see IDataProvider#getObjects()
     */
    public Set<Object> getObjectsSet() {
        final GetObjectsResponse response =
                getDataProvider().getObjects(new Void(), createTicket());
        return response.getObjects().stream()
                .map(Object::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns list of all object types in the system.
     * @return list of all object types in the system
     * @see IDataProvider#getObjectTypes()
     */
    public List<ObjectType> getObjectTypesList() {
        final GetObjectTypesResponse response =
                getDataProvider().getObjectTypes(new Void(), createTicket());
        return response.getObjectTypes().stream()
                .map(ObjectType::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns set of all object types in the system.
     * @return set of all object types in the system
     * @see IDataProvider#getObjectTypes()
     */
    public Set<ObjectType> getObjectTypesSet() {
        final GetObjectTypesResponse response =
                getDataProvider().getObjectTypes(new Void(), createTicket());
        return response.getObjectTypes().stream()
                .map(ObjectType::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns settings of the application. Handle with care, their shared.
     * @return settings of the application
     */
    public Properties getSettings() {
        return settings;
    }

    /**
     * Returns ticket for document processing.
     * @param username user login
     * @return ticket for document processing
     * @see IDocumentProcessor#getTicket(java.lang.String)
     */
    public Ticket getTicket(final String username) {
        final GetEditingTicket request = new GetEditingTicket();
        final cz.cuni.mff.ufal.textan.commons.models.Ticket ticket =
                new cz.cuni.mff.ufal.textan.commons.models.Ticket();
        ticket.setUsername(username);
        final IDocumentProcessor docProc = getDocumentProcessor();
        final GetEditingTicketResponse response = docProc.getEditingTicket(request, ticket);
        return new Ticket(response.getEditingTicket());
    }

    /**
     * Creates new grapher for providing graph information.
     * @return new grapher for providing graph information
     */
    public Grapher createGrapher() {
        return new Grapher(this);
    }

    /**
     * Creates new pipeline for processing new report.
     * @return new pipeline for processing new report
     */
    public ProcessReportPipeline createNewReportPipeline() {
        return new ProcessReportPipeline(this);
    }

    /**
     * Saves processed documents.
     * @param ticket editing ticket
     * @param text report text
     * @param reportEntities report entities
     * @param reportRelations report relations
     */
    public void saveProcessedDocument(final Ticket ticket,
            final String text, final List<Entity> reportEntities,
            final Set<Relation> reportRelations) {
        final Objects objs = new Objects();
        objs.getObjects().addAll(
                reportEntities.stream()
                        .sequential()
                        .filter(ent -> ent.getCandidate() != null)
                        .map(ent -> ent.getCandidate().toObject())
                        .collect(Collectors.toList())
        );
        final Relations relations = new Relations();
        relations.getRelations().addAll(
                reportRelations.stream()
                        .map(Relation::toRelation)
                        .collect(Collectors.toList())
        );

        final SaveProcessedDocumentFromString request = new SaveProcessedDocumentFromString();
        request.setText(text);
        request.setObjects(objs);
        request.setRelations(relations);
        request.setForce(false);

        getDocumentProcessor().saveProcessedDocumentFromString(
                request, //TODO handle save document error
                ticket.toTicket()
        );
    }
}
