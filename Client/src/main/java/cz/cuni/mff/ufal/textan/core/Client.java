package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.Relation;
import cz.cuni.mff.ufal.textan.commons.models.UsernameToken;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.*;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsFromStringRequest.Entities;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.ws.IDataProvider;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPBinding;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class controlling core manipulations with reports.
 * Handles all communicatioin with the server.
 */
public class Client {

    private static final QName DOCUMENT_PROCESSOR_SERVICE = new QName("http://ws.commons.textan.ufal.mff.cuni.cz", "DocumentProcessorService");
    private static final QName DOCUMENT_PROCESSOR_PORT = new QName("http://ws.commons.textan.ufal.mff.cuni.cz/DocumentProcessorService", "DocumentProcessorPort");

    private static final QName DATA_PROVIDER_SERVICE = new QName("http://ws.commons.textan.ufal.mff.cuni.cz", "DataProviderService");
    private static final QName DATA_PROVIDER_PORT = new QName("http://server.textan.ufal.mff.cuni.cz/DataProviderService", "DataProviderPort");

    /** Settings of the application. Handle with care, they're shared. */
    final protected Properties settings;

    /** Instance of data provider. */
    protected IDataProvider dataProvider = null;

    /** Instance of document processor. */
    protected IDocumentProcessor documentProcessor = null;

    /**
     * Only constructor
     *
     * @param settings application settings
     */
    public Client(final Properties settings) {
        this.settings = settings;
    }

    /**
     * Adds JAX-WS Handler which adds UsernameToken header into SOAP message.
     *
     * @param binding JAW-WS bindings (from web service port)
     */
    private void addSOAPHandler(Binding binding) {

        UsernameToken token = new UsernameToken();
        token.setUsername(settings.getProperty("username"));

        List<Handler> handlers = new ArrayList<>(1);
        handlers.add(new SOAPHandler<SOAPMessageContext>() {

            @Override
            public boolean handleMessage(SOAPMessageContext context) {
                try {
                    Boolean outbound = (Boolean) context.get("javax.xml.ws.handler.message.outbound");
                    if (outbound != null && outbound) {

                        SOAPMessage message = context.getMessage();
                        SOAPHeader header = message.getSOAPHeader();
                        if (header == null) {
                            header = message.getSOAPPart().getEnvelope().addHeader();
                        }

                        Marshaller marshaller = JAXBContext.newInstance(UsernameToken.class).createMarshaller();
                        marshaller.marshal(token, header);
                    }
                } catch (JAXBException | SOAPException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return true;
            }

            @Override
            public boolean handleFault(SOAPMessageContext context) { return true; }

            @Override
            public void close(MessageContext context) { }

            @Override
            public Set<QName> getHeaders() { return null; }
        });

        binding.setHandlerChain(handlers);
    }

    /**
     * Returns {@link #documentProcessor}, it is created if needed.
     *
     * @return document processor
     */
    //TODO: configurable wsdl location!
    private IDocumentProcessor getDocumentProcessor() {
        if (documentProcessor == null) {
            try {
                Service service = Service.create(new URL("http://localhost:9100/soap/document?wsdl"), DOCUMENT_PROCESSOR_SERVICE);
                // Endpoint Address
                String endpointAddress = "http://localhost:9100/soap/document";
                // Add a port to the Service
                service.addPort(DOCUMENT_PROCESSOR_PORT, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
                documentProcessor = service.getPort(IDocumentProcessor.class);

                Binding documentProcessorBinding = ((BindingProvider) documentProcessor).getBinding();
                addSOAPHandler(documentProcessorBinding);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new WebServiceException("Malformed URL!", e);
            }
        }
        return documentProcessor;
    }

    /**
     * Returns {@link #dataProvider}, it is created if needed.
     *
     * @return data provider
     */
    //TODO: configurable wsdl location!
    private IDataProvider getDataProvider() {
        if (dataProvider == null) {
            try {
                Service service = Service.create(new URL("http://localhost:9100/soap/data?wsdl"), DATA_PROVIDER_SERVICE);
                // Endpoint Address
                String endpointAddress = "http://localhost:9100/soap/data";
                // Add a port to the Service
                service.addPort(DATA_PROVIDER_PORT, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
                dataProvider = service.getPort(IDataProvider.class);

                Binding dataProviderBinding = ((BindingProvider) dataProvider).getBinding();
                addSOAPHandler(dataProviderBinding);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new WebServiceException("Malformed URL!", e);
            }
        }
        return dataProvider;
    }

    /**
     * Returns entities identified in text.
     *
     * @param ticket editing ticket
     * @param text   text to process
     * @return entities identified in text
     * @see IDocumentProcessor#getEntitiesFromString(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesFromStringRequest, cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket)
     */
    public List<Entity> getEntities(final Ticket ticket, final String text) {
        final GetEntitiesFromStringRequest request = new GetEntitiesFromStringRequest();
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
     *
     * @param centerId center object id
     * @param distance maximal distance from center
     * @return centered graph with limited distance
     * @throws IdNotFoundException if object id is not found
     */
    public Graph getGraph(final long centerId, final int distance)
            throws IdNotFoundException {
        final GetGraphByIdRequest request = new GetGraphByIdRequest();
        request.setDistance(distance);
        request.setObjectId(centerId);
        try {
            final GetGraphByIdResponse response =
                    getDataProvider().getGraphById(request); //, createTicket()); fixme
            return new Graph(response.getGraph());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Fills candidates of entities.
     *
     * @param ticket   editing ticket
     * @param text     report to process
     * @param entities where to store candidates
     * @see cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor#getAssignmentsFromString(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsFromStringRequest, cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket)
     */
    public void getObjects(final Ticket ticket, final String text, final List<Entity> entities) {
        final Entities ents = new Entities();
        final Map<Integer, Entity> map = new HashMap<>();
        for (Entity entity : entities) {
            ents.getEntities().add(entity.toEntity());
            map.put(entity.getPosition(), entity);
        }

        final GetAssignmentsFromStringRequest request = new GetAssignmentsFromStringRequest();
        request.setText(text);
        request.setEntities(ents);

        final GetAssignmentsFromStringResponse response = getDocumentProcessor().getAssignmentsFromString(request, ticket.toTicket());

        for (Assignment assignment : response.getAssignments()) {
            final Entity ent = map.get(assignment.getEntity().getPosition());
            ent.getCandidates().clear();
            for (Assignment.RatedObject rating : assignment.getRatedObjects()) {
                final double r = rating.getScore();
                final Object obj = new Object(rating.getObject());
                ent.getCandidates().add(new Pair<>(r, obj));
            }
        }
    }

    /**
     * Returns list of all objects in the system with specified type.
     *
     * @param typeId type id to filter
     * @return list of all objects in the system with specified type
     * @throws IdNotFoundException if id was not found
     * @see IDataProvider#getObjectsByTypeId(cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsByTypeIdRequest)
     */
    public List<Object> getObjectsListByTypeId(final long typeId)
            throws IdNotFoundException {
        try {
            final GetObjectsByTypeIdRequest request = new GetObjectsByTypeIdRequest();
            request.setObjectTypeId(typeId);
            final GetObjectsByTypeIdResponse response =
                    getDataProvider().getObjectsByTypeId(request); //fixme, createTicket());
            return response.getObjects().stream()
                    .map(Object::new)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Returns list of all objects in the system.
     *
     * @return list of all objects in the system
     * @see IDataProvider#getObjects(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public List<Object> getObjectsList() {
        final GetObjectsResponse response =
                getDataProvider().getObjects(new Void()); //, createTicket()); fixme
        return response.getObjects().stream()
                .map(Object::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns set of all objects in the system.
     *
     * @return set of all objects in the system
     * @see IDataProvider#getObjects(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public Set<Object> getObjectsSet() {
        final GetObjectsResponse response =
                getDataProvider().getObjects(new Void()); //, createTicket()); fixme
        return response.getObjects().stream()
                .map(Object::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns list of all object types in the system.
     *
     * @return list of all object types in the system
     * @see IDataProvider#getObjectTypes(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public List<ObjectType> getObjectTypesList() {
        final GetObjectTypesResponse response =
                getDataProvider().getObjectTypes(new Void()); //, createTicket()); fixme
        return response.getObjectTypes().stream()
                .map(ObjectType::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns set of all object types in the system.
     *
     * @return set of all object types in the system
     * @see IDataProvider#getObjectTypes(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public Set<ObjectType> getObjectTypesSet() {
        final GetObjectTypesResponse response =
                getDataProvider().getObjectTypes(new Void()); //, createTicket()); fixme
        return response.getObjectTypes().stream()
                .map(ObjectType::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns set of all relation types in the system.
     *
     * @return set of all relation types in the system
     * @see IDataProvider#getRelationTypes(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public List<RelationType> getRelationTypesList() {
        final GetRelationTypesResponse response =
                getDataProvider().getRelationTypes(new Void()); //, createTicket()); fixme
        return response.getRelationTypes().stream()
                .map(RelationType::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns settings of the application. Handle with care, their shared.
     *
     * @return settings of the application
     */
    public Properties getSettings() {
        return settings;
    }

    /**
     * Returns ticket for document processing.
     *
     * @param username user login
     * @return ticket for document processing
     * @see IDocumentProcessor#getEditingTicket(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEditingTicketRequest)
     */
    public Ticket getTicket(final String username) {
        final GetEditingTicketRequest request = new GetEditingTicketRequest();
//        final cz.cuni.mff.ufal.textan.commons.models.Ticket ticket = fixme
//                new cz.cuni.mff.ufal.textan.commons.models.Ticket();
//        ticket.setUsername(username);
        final IDocumentProcessor docProc = getDocumentProcessor();
        final GetEditingTicketResponse response =
                docProc.getEditingTicket(request);//, ticket); fixme
        return new Ticket(response.getEditingTicket());
    }

    /**
     * Creates new grapher for providing graph information.
     *
     * @return new grapher for providing graph information
     */
    public Grapher createGrapher() {
        return new Grapher(this);
    }

    /**
     * Creates new pipeline for processing new report.
     *
     * @return new pipeline for processing new report
     */
    public ProcessReportPipeline createNewReportPipeline() {
        return new ProcessReportPipeline(this);
    }

    /**
     * Saves processed documents.
     *
     * @param ticket          editing ticket
     * @param text            report text
     * @param reportEntities  report entities
     * @param reportRelations report relations
     */
    public boolean saveProcessedDocument(final Ticket ticket,
                                         final String text, final List<Entity> reportEntities,
                                         final List<RelationBuilder> reportRelations) throws IdNotFoundException {
        final SaveProcessedDocumentFromStringRequest request =
                new SaveProcessedDocumentFromStringRequest();

        final List<cz.cuni.mff.ufal.textan.commons.models.Object> objects =
                request.getObjects();
        final List<ObjectOccurrence> objectOccurrences =
                request.getObjectOccurrences();
        for (Entity ent : reportEntities) {
            if (ent.getCandidate() != null) {
                objects.add(ent.getCandidate().toObject());
                objectOccurrences.add(ent.toObjectOccurrence());
            }
        }

        final List<Relation> relations = request.getRelations();
        final List<RelationOccurrence> relationOccurrences =
                request.getRelationOccurrences();
        for (RelationBuilder relation : reportRelations) {
            relations.add(relation.toRelation());
            final RelationOccurrence occ = relation.toRelationOccurrence();
            relationOccurrences.add(occ);
        }

        request.setText(text);
        request.setForce(false);

        try {
            final SaveProcessedDocumentFromStringResponse response =
                    getDocumentProcessor().saveProcessedDocumentFromString(
                            request, //TODO handle save document error
                            ticket.toTicket());
            return response.isResult();
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }
}
