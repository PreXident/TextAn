package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.ObjectOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.RelationOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.UsernameToken;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.*;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsFromStringRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsFromStringResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEditingTicketRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEditingTicketResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesFromStringRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesFromStringResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetProblemsRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetProblemsResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetRelationsByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetRelationsByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetRelationsFromStringRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetRelationsFromStringResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.RewriteAndSaveProcessedDocumentByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.RewriteAndSaveProcessedDocumentByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.SaveProcessedDocumentByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.SaveProcessedDocumentByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.SaveProcessedDocumentFromStringRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.SaveProcessedDocumentFromStringResponse;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.ws.IDataProvider;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import cz.cuni.mff.ufal.textan.commons.ws.InvalidMergeException;
import cz.cuni.mff.ufal.textan.core.DocumentData.Occurrence;
import cz.cuni.mff.ufal.textan.core.graph.ObjectGrapher;
import cz.cuni.mff.ufal.textan.core.graph.RelationGrapher;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentAlreadyProcessedException;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentChangedException;
import cz.cuni.mff.ufal.textan.core.processreport.Problems;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
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

/**
 * Main class controlling core manipulations with reports.
 * Handles all communicatioin with the server. Serves as a factory for
 * ProcessReportPipeline.
 */
public class Client {

    /** Document processor QName. */
    private static final QName DOCUMENT_PROCESSOR_SERVICE = new QName("http://ws.commons.textan.ufal.mff.cuni.cz", "DocumentProcessorService");

    /** Document processor Port. */
    private static final QName DOCUMENT_PROCESSOR_PORT = new QName("http://ws.commons.textan.ufal.mff.cuni.cz/DocumentProcessorService", "DocumentProcessorPort");

    /** Data provider QName. */
    private static final QName DATA_PROVIDER_SERVICE = new QName("http://ws.commons.textan.ufal.mff.cuni.cz", "DataProviderService");

    /** Data provider Port. */
    private static final QName DATA_PROVIDER_PORT = new QName("http://server.textan.ufal.mff.cuni.cz/DataProviderService", "DataProviderPort");

    /**
     * Parses string to integer; if string is invalid, def is returned.
     * @param string string to parse
     * @param def default value
     * @return string converted to integer, or default if invalid
     */
    private static int parseInt(final String string, final int def) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /** Values for document processed filtering. */
    public enum Processed {
        YES {
            @Override
            public boolean filter(final Document document) {
                return document.isProcessed();
            }

            @Override
            public String toFilter() {
                return "processed";
            }
        },
        NO {
            @Override
            public boolean filter(final Document document) {
                return !document.isProcessed();
            }

            @Override
            public String toFilter() {
                return "unprocessed";
            }
        },
        BOTH {
            @Override
            public boolean filter(final Document document) {
                return true;
            }

            @Override
            public String toFilter() {
                return "all";
            }
        };

        /**
         * Filters document by it's processed field.
         * @param document document to filter
         * @return true if document passes the filter, false otherwise
         */
        public abstract boolean filter(final Document document);

        /**
         * Returns filter value for field processed filter of DocumentProvider request.
         * @return filter value for field processed filter of DocumentProvider request
         */
        public abstract String toFilter();
    }

    /**
     * Process assignments to entities.
     * @param assignments assignments to process
     * @param entities entities to assign candidates to
     */
    static protected void processAssignments(final List<Assignment> assignments,
            final Map<Integer, Entity> entities) {
        final Map<Long, Object> objects = new HashMap<>();
        for (Assignment assignment : assignments) {
            final Entity ent = entities.get(assignment.getEntity().getPosition());
            ent.getCandidates().clear();
            for (Assignment.RatedObject rating : assignment.getRatedObjects()) {
                final double r = rating.getScore();
                final Long objId = rating.getObject().getId();
                Object obj;
                if (objects.containsKey(objId)) {
                    obj = objects.get(objId);
                } else {
                    obj = new Object(rating.getObject());
                    objects.put(objId, obj);
                }
                ent.getCandidates().add(new Pair<>(r, obj));
            }
        }
    }

    /** Settings of the application. Handle with care, they're shared. */
    final protected Properties settings;

    /** Username for communication with the server. It is not change until restart. */
    protected String username;

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
        username = settings.getProperty("username");
    }

    /**
     * Sets the username.
     * Make sure this method is called BEFORE creating {@link #dataProvider} or
     * {@link #documentProcessor}, so they use new username.
     * @param username new username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Adds JAX-WS Handler which adds UsernameToken header into SOAP message.
     * @param binding JAW-WS bindings (from web service port)
     */
    private void addSOAPHandler(final Binding binding) {
        final UsernameToken token = new UsernameToken();
        token.setUsername(username);

        @SuppressWarnings("rawtypes")
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
     * Inits ssl communication.
     */
    private void initSSL() {
        if (settings.getProperty("ssl", "").equals("true")) {
            final String ts = settings.getProperty("ssl.trustStore", "");
            if (!ts.isEmpty()) {
                System.setProperty("javax.net.ssl.trustStore", ts);
            }
            final String tsPass = settings.getProperty("ssl.trustStore.password", "");
            if (!tsPass.isEmpty()) {
                System.setProperty("javax.net.ssl.trustStorePassword", tsPass);
            }
            final String tsType = settings.getProperty("ssl.trustStore.type", "");
            if (!tsType.isEmpty()) {
                System.setProperty("javax.net.ssl.trustStoreType", tsType);
            }
            if (settings.getProperty("ssl.clientAuth", "").equals("true")) {
                final String ks = settings.getProperty("ssl.keyStore", "");
                if (!ks.isEmpty()) {
                    System.setProperty("javax.net.ssl.keyStore", ks);
                }
                final String ksPass = settings.getProperty("ssl.keyStore.password", "");
                if (!ksPass.isEmpty()) {
                    System.setProperty("javax.net.ssl.keyStorePassword", ksPass);
                }
                final String ksType = settings.getProperty("ssl.keyStore.type", "");
                if (!ksType.isEmpty()) {
                    System.setProperty("javax.net.ssl.keyStoreType", ksType);
                }
            }
        }
    }

    /**
     * Returns {@link #documentProcessor}, it is created if needed.
     * @return document processor
     */
    private IDocumentProcessor getDocumentProcessor() {
        if (documentProcessor == null) {
            initSSL();
            try {
                Service service = Service.create(
                        new URL(settings.getProperty("url.document.wsdl", "http://textan.ms.mff.cuni.cz:9500/soap/document?wsdl")),
                        DOCUMENT_PROCESSOR_SERVICE);
                // Endpoint Address
                String endpointAddress = settings.getProperty("url.document", "http://textan.ms.mff.cuni.cz:9500/soap/document");
                // Add a port to the Service
                service.addPort(DOCUMENT_PROCESSOR_PORT, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
                final IDocumentProcessor processor =
                        service.getPort(IDocumentProcessor.class);

                final BindingProvider provider = ((BindingProvider) processor);
                final int connectTimeout =
                        parseInt(settings.getProperty("connect.timeout", "60000"), 60000);
                provider.getRequestContext().put(
                        "com.sun.xml.ws.connect.timeout",
                        connectTimeout);
                provider.getRequestContext().put(
                        "com.sun.xml.internal.ws.connect.timeout",
                        connectTimeout);
                final int requestTimeout =
                        parseInt(settings.getProperty("request.timeout", "60000"), 60000);
                provider.getRequestContext().put(
                        "com.sun.xml.ws.request.timeout",
                        requestTimeout);
                provider.getRequestContext().put(
                        "com.sun.xml.internal.ws.request.timeout",
                        requestTimeout);
                Binding documentProcessorBinding = provider.getBinding();
                addSOAPHandler(documentProcessorBinding);
                documentProcessor = new SynchronizedDocumentProcessor(processor);
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
    private IDataProvider getDataProvider() {
        if (dataProvider == null) {
            initSSL();
            try {
                Service service = Service.create(
                        new URL(settings.getProperty("url.data.wsdl", "http://textan.ms.mff.cuni.cz:9500/soap/data?wsdl")),
                        DATA_PROVIDER_SERVICE);
                // Endpoint Address
                String endpointAddress = settings.getProperty("url.data", "http://textan.ms.mff.cuni.cz:9500/soap/data");
                // Add a port to the Service
                service.addPort(DATA_PROVIDER_PORT, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
                final IDataProvider data = service.getPort(IDataProvider.class);

                final BindingProvider provider = ((BindingProvider) data);
                final int connectTimeout =
                        parseInt(settings.getProperty("connect.timeout", "60000"), 60000);
                provider.getRequestContext().put(
                        "com.sun.xml.ws.connect.timeout",
                        connectTimeout);
                provider.getRequestContext().put(
                        "com.sun.xml.internal.ws.connect.timeout",
                        connectTimeout);
                final int requestTimeout =
                        parseInt(settings.getProperty("request.timeout", "60000"), 60000);
                provider.getRequestContext().put(
                        "com.sun.xml.ws.request.timeout",
                        requestTimeout);
                provider.getRequestContext().put(
                        "com.sun.xml.internal.ws.request.timeout",
                        requestTimeout);
                Binding dataProviderBinding = provider.getBinding();
                addSOAPHandler(dataProviderBinding);
                dataProvider = new SynchronizedDataProvider(data);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new WebServiceException("Malformed URL!", e);
            }
        }
        return dataProvider;
    }

    /**
     * Returns document with given id.
     * @param docId document id
     * @return document with given id
     * @throws IdNotFoundException on id error
     */
    public synchronized DocumentData getDocumentData(final long docId)
            throws IdNotFoundException {
        try {
            final GetObjectsAndRelationsOccurringInDocumentRequest request =
                    new GetObjectsAndRelationsOccurringInDocumentRequest();
            request.setDocumentId(docId);
            final GetObjectsAndRelationsOccurringInDocumentResponse response =
                    getDataProvider().getObjectsAndRelationsOccurringInDocument(request);
            return new DocumentData(response);
        } catch(cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Returns list of documents.
     * @param processed only processed documents?
     * @param filter document text filter
     * @param first index of the first object
     * @param size maximal number of objects
     * @return list of documents
     */
    public synchronized Pair<List<Document>, Integer> getDocumentsList(
            final Processed processed, final String filter,
            final int first, final int size) {
        final GetFilteredDocumentsRequest request =
                new GetFilteredDocumentsRequest();
        request.setFirstResult(first);
        request.setMaxResults(size);
        request.setPattern(filter);
        request.setProcessedFilter(processed.toFilter());
        final GetFilteredDocumentsResponse response =
                getDataProvider().getFilteredDocuments(request);
        final List<Document> list = response.getDocuments().stream()
                .map(Document::new)
                .collect(Collectors.toCollection(ArrayList::new));
        return new Pair<>(list, response.getDocuments().size());
    }

    /**
     * Returns documents containing given object.
     * @param objectId object whose document should be returned
     * @param filter document text filter
     * @param first index of the first object
     * @param size maximal number of objects
     * @return list of documents containing object with given id
     * @throws IdNotFoundException if id error occurs
     * @throws NonRootObjectException if object is no longer root
     */
    public synchronized Pair<List<Document>, Integer> getDocumentsList(
            final long objectId, final String filter,
            final int first, final int size) throws IdNotFoundException, NonRootObjectException {
        try {
            final GetFilteredDocumentsContainingObjectByIdRequest request =
                    new GetFilteredDocumentsContainingObjectByIdRequest();
            request.setObjectId(objectId);
            request.setFirstResult(first);
            request.setMaxResults(size);
            request.setPattern(filter);
            final GetFilteredDocumentsContainingObjectByIdResponse response =
                    getDataProvider().getFilteredDocumentsContainingObjectById(request);
            final List<Document> list = response.getDocumentCountPairs().stream()
                    .map(x -> new Document(x.getDocument(), x.getCountOfOccurrences()))
                    .collect(Collectors.toCollection(ArrayList::new));
            return new Pair<>(list, response.getTotalNumberOfResults());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.NonRootObjectException e) {
            throw new NonRootObjectException(e);
        }
    }

    /**
     * Returns documents containing given relation.
     * @param relation relation whose document should be returned
     * @param filter document text filter
     * @param first index of the first object
     * @param size maximal number of objects
     * @return list of documents containing object with given id
     * @throws IdNotFoundException if id error occurs
     */
    public synchronized Pair<List<Document>, Integer> getDocumentsList(
            final Relation relation, final String filter,
            final int first, final int size) throws IdNotFoundException {
        try {
            final GetFilteredDocumentsContainingRelationByIdRequest request =
                    new GetFilteredDocumentsContainingRelationByIdRequest();
            request.setRelationId(relation.getId());
            request.setFirstResult(first);
            request.setMaxResults(size);
            request.setPattern(filter);
            final GetFilteredDocumentsContainingRelationByIdResponse response =
                    getDataProvider().getFilteredDocumentsContainingRelationById(request);
            final List<Document> list = response.getDocumentCountPairs().stream()
                    .map(x -> new Document(x.getDocument(), x.getCountOfOccurrences()))
                    .collect(Collectors.toCollection(ArrayList::new));
            return new Pair<>(list, response.getTotalNumberOfResults());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Returns entities identified in text.
     * @param ticket editing ticket
     * @param id report id
     * @return entities identified in text
     * @throws DocumentChangedException if document has been changed under our hands
     * @throws IdNotFoundException if document was not found
     * @throws DocumentAlreadyProcessedException if document has been processed under our hands
     * @see IDocumentProcessor#getEntitiesById(GetEntitiesByIdRequest, EditingTicket)
     */
    public synchronized List<Entity> getEntities(final Ticket ticket, final long id)
            throws DocumentChangedException, IdNotFoundException,
            DocumentAlreadyProcessedException {
        final GetEntitiesByIdRequest request = new GetEntitiesByIdRequest();
        request.setDocumentId(id);
        final IDocumentProcessor docProc = getDocumentProcessor();
        try {
            final GetEntitiesByIdResponse response =
                    docProc.getEntitiesById(request, ticket.toTicket());
            return response.getEntities().stream()
                    .map(Entity::new)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentChangedException e) {
            throw new DocumentChangedException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentAlreadyProcessedException e) {
            throw new DocumentAlreadyProcessedException(e);
        }
    }

    /**
     * Returns entities identified in text.
     * @param ticket editing ticket
     * @param text   text to process
     * @return entities identified in text
     * @see IDocumentProcessor#getEntitiesFromString(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesFromStringRequest, cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket)
     */
    public synchronized List<Entity> getEntities(final Ticket ticket, final String text) {
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
     * Returns centered object graph with limited distance.
     * @param centerId center object id
     * @param distance maximal distance from center
     * @return centered graph with limited distance
     * @throws IdNotFoundException if object id is not found
     * @throws NonRootObjectException if object is no longer root
     */
    public synchronized Graph getObjectGraph(final long centerId, final int distance)
            throws IdNotFoundException, NonRootObjectException {
        final GetGraphByObjectIdRequest request = new GetGraphByObjectIdRequest();
        request.setDistance(distance);
        request.setObjectId(centerId);
        try {
            final GetGraphByObjectIdResponse response =
                    getDataProvider().getGraphByObjectId(request);
            return new Graph(response.getGraph());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.NonRootObjectException e) {
            throw new NonRootObjectException(e);
        }
    }

    /**
     * Converts client fromEntities to commons toEntities while filling entMap.
     * @param fromEntities client entities
     * @param toEntities commons entities
     * @param entMap entity id -> entity mapping
     */
    private void convertEntities(final List<Entity> fromEntities,
            final List<cz.cuni.mff.ufal.textan.commons.models.Entity> toEntities,
            final Map<Integer, Entity> entMap) {
        for (Entity entity : fromEntities) {
            toEntities.add(entity.toEntity());
            entMap.put(entity.getPosition(), entity);
        }
    }

    /**
     * Fills candidates of entities.
     * @param ticket   editing ticket
     * @param id document id
     * @param entities where to store candidates
     * @throws DocumentChangedException if document has been changed under our hands
     * @throws IdNotFoundException if id was not found
     * @throws DocumentAlreadyProcessedException if document has been processed under our hands
     * @see IDocumentProcessor#getAssignmentsById(GetAssignmentsByIdRequest, EditingTicket)
     */
    public synchronized void getObjects(final Ticket ticket, final long id,
            final List<Entity> entities)
            throws DocumentChangedException, IdNotFoundException,
            DocumentAlreadyProcessedException {
        final Map<Integer, Entity> entMap = new HashMap<>();
        final GetAssignmentsByIdRequest request = new GetAssignmentsByIdRequest();
        convertEntities(entities, request.getEntities(), entMap);
        request.setId(id);
        try {
            final GetAssignmentsByIdResponse response =
                    getDocumentProcessor().getAssignmentsById(request, ticket.toTicket());
            processAssignments(response.getAssignments(), entMap);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentChangedException e) {
            throw new DocumentChangedException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentAlreadyProcessedException e) {
            throw new DocumentAlreadyProcessedException(e);
        }
    }

    /**
     * Returns object with the given id.
     * @param objectId object id to find
     * @return object with the given id
     * @throws IdNotFoundException if object with given id was not found
     */
    public synchronized Object getObject(final long objectId)
            throws IdNotFoundException {
        final GetObjectRequest request = new GetObjectRequest();
        request.setObjectId(objectId);
        try {
            final GetObjectResponse response = getDataProvider().getObject(request);
            return new Object(response.getObject());
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
    public synchronized void getObjects(final Ticket ticket, final String text, final List<Entity> entities) {
        final Map<Integer, Entity> entMap = new HashMap<>();
        final GetAssignmentsFromStringRequest request = new GetAssignmentsFromStringRequest();
        convertEntities(entities, request.getEntities(), entMap);
        request.setText(text);
        //
        final GetAssignmentsFromStringResponse response = getDocumentProcessor().getAssignmentsFromString(request, ticket.toTicket());
        processAssignments(response.getAssignments(), entMap);
    }

    /**
     * Returns list of all objects in the system with specified type.
     * @param typeId type id to filter
     * @return list of all objects in the system with specified type
     * @throws IdNotFoundException if id was not found
     * @see IDataProvider#getObjectsByTypeId(cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsByTypeIdRequest)
     */
    public synchronized List<Object> getObjectsListByTypeId(final long typeId)
            throws IdNotFoundException {
        try {
            final GetObjectsByTypeIdRequest request = new GetObjectsByTypeIdRequest();
            request.setObjectTypeId(typeId);
            final GetObjectsByTypeIdResponse response =
                    getDataProvider().getObjectsByTypeId(request);
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
     * @see IDataProvider#getObjects(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public synchronized List<Object> getObjectsList() {
        final GetObjectsResponse response =
                getDataProvider().getObjects(new Void());
        return response.getObjects().stream()
                .map(Object::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns filtered list of objects in the system.
     * @param type filter object type
     * @param filter filter aliases
     * @param first index of the first object
     * @param size maximal number of objects
     * @return list of filtered objects in the system
     * @throws IdNotFoundException if id error occurs
     */
    public synchronized Pair<List<Object>, Integer> getObjectsList(final ObjectType type,
            final String filter, final int first, final int size) throws IdNotFoundException {
        final GetFilteredObjectsRequest request =
                new GetFilteredObjectsRequest();
        request.setObjectTypeId(type != null ? type.getId() : null);
        request.setAliasFilter(filter);
        request.setFirstResult(first);
        request.setMaxResults(size);
        try {
            final GetFilteredObjectsResponse response =
                    getDataProvider().getFilteredObjects(request);
            final List<Object> objects = response.getObjects().stream()
                    .map(Object::new)
                    .collect(Collectors.toList());
            return new Pair<>(objects, response.getTotalNumberOfResults());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Returns map of all objects in the system.
     * @return map of all objects in the system
     * @see IDataProvider#getObjects(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public synchronized Map<Long, Object> getObjectsMap() {
        final GetObjectsResponse response =
                getDataProvider().getObjects(new Void());
        return response.getObjects().stream()
                .collect(Collectors.toMap(
                        cz.cuni.mff.ufal.textan.commons.models.Object::getId,
                        Object::new));
    }

    /**
     * Returns set of all objects in the system.
     * @return set of all objects in the system
     * @see IDataProvider#getObjects(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public synchronized Set<Object> getObjectsSet() {
        final GetObjectsResponse response =
                getDataProvider().getObjects(new Void());
        return response.getObjects().stream()
                .map(Object::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns list of all object types in the system.
     * @return list of all object types in the system
     * @see IDataProvider#getObjectTypes(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public synchronized List<ObjectType> getObjectTypesList() {
        final GetObjectTypesResponse response =
                getDataProvider().getObjectTypes(new Void());
        return response.getObjectTypes().stream()
                .map(ObjectType::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns set of all object types in the system.
     * @return set of all object types in the system
     * @see IDataProvider#getObjectTypes(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public synchronized Set<ObjectType> getObjectTypesSet() {
        final GetObjectTypesResponse response =
                getDataProvider().getObjectTypes(new Void());
        return response.getObjectTypes().stream()
                .map(ObjectType::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns graph connecting the given two objects.
     * The returned graph is empty if no path is found.
     * @param start starting object
     * @param target target object
     * @param maxLength maximum length of the path
     * @return graph connecting the given two objects, empty if no path is found
     * @throws IdNotFoundException on id error
     * @throws NonRootObjectException if given objects are no longer roots
     */
    public synchronized Graph getPathGraph(final long start,
            final long target, final int maxLength)
            throws IdNotFoundException, NonRootObjectException {
        //TODO call proper methods when ready
        final GetPathByIdRequest request = new GetPathByIdRequest();
        request.setStartObjectId(start);
        request.setTargetObjectId(target);
        request.setMaxLength(maxLength);
        try {
            final GetPathByIdResponse response =
                    getDataProvider().getPathById(request);
            return new Graph(response.getGraph());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.NonRootObjectException e) {
            throw new NonRootObjectException(e);
        }
    }

    /**
     * Returns problems with report saving.
     * @param ticket editing ticket
     * @return problems with report saving
     */
    public synchronized Problems getProblems(final Ticket ticket) {
        final GetProblemsRequest request =  new GetProblemsRequest();
        final GetProblemsResponse response = getDocumentProcessor().getProblems(request, ticket.toTicket());
        return new Problems(response);
    }

    /**
     * Recognizes relations into relations and occurrences lists.
     * @param ticket editing ticket
     * @param text report to process
     * @param entities entities with assigned objects
     * @param relations where to store recognized entities
     * @param occurrences where to store relation occurrences
     * @see IDocumentProcessor#getAssignmentsFromString(GetAssignmentsFromStringRequest, EditingTicket)
     */
    public synchronized void getRelations(final Ticket ticket,
            final String text, final List<Entity> entities,
            final List<Relation> relations, final List<Occurrence> occurrences) {
        final GetRelationsFromStringRequest request =
                new GetRelationsFromStringRequest();
        request.setText(text);
        final List<cz.cuni.mff.ufal.textan.commons.models.Object> reqObjects =
                request.getObjects();
        final List<ObjectOccurrence> objectOccurrences =
                request.getObjectOccurrences();
        final Map<Long, Object> objectMap = new HashMap<>();
        for (Entity entity : entities) {
            final Object object = entity.getCandidate();
            reqObjects.add(object.toObject());
            objectMap.put(object.getId(), object);
            objectOccurrences.add(entity.toObjectOccurrence());
        }
        final GetRelationsFromStringResponse response =
                getDocumentProcessor().getRelationsFromString(request, ticket.toTicket());
        response.getRelations().stream()
                .map(rel -> new Relation(rel, objectMap))
                .forEach(relations::add);
        response.getRelationOccurrences().stream()
                .map(Occurrence::new)
                .forEach(occurrences::add);
    }

    /**
     * Recognizes relations into relations and occurrences lists.
     * @param ticket editing ticket
     * @param id document id
     * @param entities entities with assigned objects
     * @param relations where to store recognized entities
     * @param occurrences where to store relation occurrences
     * @throws DocumentChangedException if document has been changed under our hands
     * @throws IdNotFoundException if id was not found
     * @throws DocumentAlreadyProcessedException if document has been processed under our hands
     * @see IDocumentProcessor#getAssignmentsFromString(GetAssignmentsFromStringRequest, EditingTicket)
     */
    public synchronized void getRelations(final Ticket ticket,
            final long id, final List<Entity> entities,
            final List<Relation> relations, final List<Occurrence> occurrences)
            throws IdNotFoundException, DocumentAlreadyProcessedException,
            DocumentChangedException {
        final GetRelationsByIdRequest request =
                new GetRelationsByIdRequest();
        request.setId(id);
        final List<cz.cuni.mff.ufal.textan.commons.models.Object> reqObjects =
                request.getObjects();
        final List<ObjectOccurrence> objectOccurrences =
                request.getObjectOccurrences();
        final Map<Long, Object> objectMap = new HashMap<>();
        for (Entity entity : entities) {
            final Object object = entity.getCandidate();
            reqObjects.add(object.toObject());
            objectMap.put(object.getId(), object);
            objectOccurrences.add(entity.toObjectOccurrence());
        }
        try {
            final GetRelationsByIdResponse response =
                    getDocumentProcessor().getRelationsById(request, ticket.toTicket());
            response.getRelations().stream()
                    .map(rel -> new Relation(rel, objectMap))
                    .forEach(relations::add);
            response.getRelationOccurrences().stream()
                    .map(Occurrence::new)
                    .forEach(occurrences::add);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentAlreadyProcessedException e) {
            throw new DocumentAlreadyProcessedException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentChangedException e) {
            throw new DocumentChangedException(e);
        }
    }

    /**
     * Returns centered relation graph with limited distance.
     * @param rootId center relation id
     * @param distance maximal distance from center
     * @return centered graph with limited distance
     * @throws IdNotFoundException if relation id is not found
     */
    public synchronized Graph getRelationGraph(final long rootId,
            final int distance) throws IdNotFoundException {
        try {
            final GetGraphByRelationIdRequest request =
                    new GetGraphByRelationIdRequest();
            request.setRelationId(rootId);
            request.setDistance(distance);
            final GetGraphByRelationIdResponse response =
                    getDataProvider().getGraphByRelationId(request);
            return new Graph(response.getGraph());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Returns filtered list of relations in the system.
     * @param type filter object type
     * @param filter filter aliases
     * @param first index of the first object
     * @param size maximal number of objects
     * @return list of filtered objects in the system
     * @throws IdNotFoundException if id error occurs
     */
    public synchronized Pair<List<Relation>, Integer> getRelationList(
            final RelationType type, final String filter, final int first,
            final int size) throws IdNotFoundException {
        try {
            final GetFilteredRelationsRequest request =
                    new GetFilteredRelationsRequest();
            if (type != null) {
                request.setRelationTypeId(type.getId());
            }
            request.setAnchorFilter(filter);
            request.setFirstResult(first);
            request.setMaxResults(size);
            final GetFilteredRelationsResponse response =
                    getDataProvider().getFilteredRelations(request);
            //TODO extract directly from response when it's ready
            final GetObjectsByIdsRequest objectRequest =
                    new GetObjectsByIdsRequest();
            final List<Long> objectIds = objectRequest.getObjectIds();
            response.getRelations().stream()
                    .map(cz.cuni.mff.ufal.textan.commons.models.Relation::getInRelations)
                    .flatMap(List::stream)
                    .map(cz.cuni.mff.ufal.textan.commons.models.Relation.InRelation::getObjectId)
                    .forEach(objectIds::add);
            final GetObjectsByIdsResponse objectsResponse =
                    getDataProvider().getObjectsByIds(objectRequest);
            final Map<Long, Object> objects = objectsResponse.getObjects().stream()
                    .collect(Collectors.toMap(
                            cz.cuni.mff.ufal.textan.commons.models.Object::getId,
                            Object::new));
            //
            final List<Relation> relations = response.getRelations().stream()
                    .map(rel -> new Relation(rel, objects))
                    .collect(Collectors.toList());
            return new Pair<>(relations, response.getTotalNumberOfResults());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Returns set of all relation types in the system.
     * @return set of all relation types in the system
     * @see IDataProvider#getRelationTypes(cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void)
     */
    public synchronized List<RelationType> getRelationTypesList() {
        final GetRelationTypesResponse response =
                getDataProvider().getRelationTypes(new Void());
        return response.getRelationTypes().stream()
                .map(RelationType::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns list of role for given relation type.
     * @param type relation type
     * @return list of role for given relation type
     * @throws IdNotFoundException if id error occurs
     */
    public synchronized List<String> getRolesForRelationType(
            final RelationType type) throws IdNotFoundException {
        try {
            final GetRolesForRelationTypeByIdRequest request =
                    new GetRolesForRelationTypeByIdRequest();
            request.setRelationTypeId(type.getId());
            return getDataProvider().getRolesForRelationTypeById(request).getRoles();
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
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
     * @return ticket for document processing
     * @see IDocumentProcessor#getEditingTicket(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEditingTicketRequest)
     */
    public synchronized Ticket getTicket() {
        final GetEditingTicketRequest request = new GetEditingTicketRequest();
        final IDocumentProcessor docProc = getDocumentProcessor();
        final GetEditingTicketResponse response =
                docProc.getEditingTicket(request);
        return new Ticket(response.getEditingTicket());
    }

    /**
     * Creates new grapher for providing graph information.
     * @return new grapher for providing graph information
     */
    public ObjectGrapher createObjectGrapher() {
        return new ObjectGrapher(this);
    }

    /**
     * Creates new pipeline for processing new report.
     * @return new pipeline for processing new report
     */
    public ProcessReportPipeline createNewReportPipeline() {
        return new ProcessReportPipeline(this);
    }

    /**
     * Creates new grapher for providing graph information.
     * @return new grapher for providing graph information
     */
    public RelationGrapher createRelationGrapher() {
        return new RelationGrapher(this);
    }

    /**
     * Converts reportEntities and reportRelations to objects,
     * objectsOccurrences, relations and relationOccurrences.
     * @param reportEntities list of entities
     * @param reportRelations list of relations
     * @param objects recognized objects
     * @param objectOccurrences object occurrences
     * @param relations recognized relations
     * @param relationOccurrences relation occurrences
     */
    private void prepareSaveRequest(final List<Entity> reportEntities,
            final List<RelationBuilder> reportRelations,
            final List<cz.cuni.mff.ufal.textan.commons.models.Object> objects,
            final List<ObjectOccurrence> objectOccurrences,
            final List<cz.cuni.mff.ufal.textan.commons.models.Relation> relations,
            final List<RelationOccurrence> relationOccurrences) {
        for (Entity ent : reportEntities) {
            if (ent.getCandidate() != null) {
                objects.add(ent.getCandidate().toObject());
                objectOccurrences.add(ent.toObjectOccurrence());
            }
        }
        for (RelationBuilder relation : reportRelations) {
            relations.add(relation.toRelation());
            final RelationOccurrence occ = relation.toRelationOccurrence();
            relationOccurrences.add(occ);
        }
    }

    /**
     * Saves processed documents.
     * If returns false, check {@link #getProblems(Ticket)}.
     * @param ticket editing ticket
     * @param id document id
     * @param reportEntities report entities
     * @param reportRelations report relations
     * @param force  force save?
     * @return true if saving was successfull, false otherwise
     * @throws IdNotFoundException if id error occurs
     * @throws DocumentChangedException if document has been changed under our hands
     * @throws DocumentAlreadyProcessedException if document has been processed under our hands
     */
    public synchronized boolean saveProcessedDocument(final Ticket ticket,
            final long id, final List<Entity> reportEntities,
            final List<RelationBuilder> reportRelations,
            final boolean force) throws IdNotFoundException,
            DocumentChangedException, DocumentAlreadyProcessedException {
        final SaveProcessedDocumentByIdRequest request =
                new SaveProcessedDocumentByIdRequest();
        final List<cz.cuni.mff.ufal.textan.commons.models.Object> objects =
                request.getObjects();
        final List<ObjectOccurrence> objectOccurrences =
                request.getObjectOccurrences();
        final List<cz.cuni.mff.ufal.textan.commons.models.Relation> relations =
                request.getRelations();
        final List<RelationOccurrence> relationOccurrences =
                request.getRelationOccurrences();
        prepareSaveRequest(reportEntities, reportRelations, objects,
                objectOccurrences, relations, relationOccurrences);
        request.setDocumentId(id);
        request.setForce(force);
        try {
            final SaveProcessedDocumentByIdResponse response =
                    getDocumentProcessor().saveProcessedDocumentById(
                            request,
                            ticket.toTicket());
            return response.isResult();
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentChangedException e) {
            throw new DocumentChangedException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentAlreadyProcessedException e) {
            throw new DocumentAlreadyProcessedException(e);
        }
    }

    /**
     * Saves processed documents.
     * If returns false, check {@link #getProblems(Ticket)}.
     * @param ticket          editing ticket
     * @param text            report text
     * @param reportEntities  report entities
     * @param reportRelations report relations
     * @param force           force save?
     * @return true if saving was successfull, false otherwise
     * @throws IdNotFoundException if id error occurs
     */
    public synchronized boolean saveProcessedDocument(final Ticket ticket,
                                         final String text, final List<Entity> reportEntities,
                                         final List<RelationBuilder> reportRelations,
                                         final boolean force) throws IdNotFoundException {
        final SaveProcessedDocumentFromStringRequest request =
                new SaveProcessedDocumentFromStringRequest();

        final List<cz.cuni.mff.ufal.textan.commons.models.Object> objects =
                request.getObjects();
        final List<ObjectOccurrence> objectOccurrences =
                request.getObjectOccurrences();
        final List<cz.cuni.mff.ufal.textan.commons.models.Relation> relations = request.getRelations();
        final List<RelationOccurrence> relationOccurrences =
                request.getRelationOccurrences();
        prepareSaveRequest(reportEntities, reportRelations, objects,
                objectOccurrences, relations, relationOccurrences);

        request.setText(text);
        request.setForce(force);

        try {
            final SaveProcessedDocumentFromStringResponse response =
                    getDocumentProcessor().saveProcessedDocumentFromString(
                            request,
                            ticket.toTicket());
            return response.isResult();
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Saves processed document replacing the text in the db.
     * If returns false, check {@link #getProblems(Ticket)}.
     * @param ticket editing ticket
     * @param id document id
     * @param text text replacing the old one
     * @param reportEntities report entities
     * @param reportRelations report relations
     * @param force  force save?
     * @return true if saving was successfull, false otherwise
     * @throws IdNotFoundException if id error occurs
     * @throws DocumentAlreadyProcessedException if document has been processed under our hands
     */
    public synchronized boolean saveProcessedDocument(final Ticket ticket,
            final long id, final String text, final List<Entity> reportEntities,
            final List<RelationBuilder> reportRelations,
            final boolean force) throws IdNotFoundException,
            DocumentAlreadyProcessedException {
        final RewriteAndSaveProcessedDocumentByIdRequest request =
              new RewriteAndSaveProcessedDocumentByIdRequest();
        final List<cz.cuni.mff.ufal.textan.commons.models.Object> objects =
                request.getObjects();
        final List<ObjectOccurrence> objectOccurrences =
                request.getObjectOccurrences();
        final List<cz.cuni.mff.ufal.textan.commons.models.Relation> relations =
                request.getRelations();
        final List<RelationOccurrence> relationOccurrences =
                request.getRelationOccurrences();
        prepareSaveRequest(reportEntities, reportRelations, objects,
                objectOccurrences, relations, relationOccurrences);
        request.setDocumentId(id);
        request.setText(text);
        request.setForce(force);
        try {
            final RewriteAndSaveProcessedDocumentByIdResponse response =
                    getDocumentProcessor().rewriteAndSaveProcessedDocumentById(
                            request,
                            ticket.toTicket());
            return response.isResult();
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.DocumentAlreadyProcessedException e) {
            throw new DocumentAlreadyProcessedException(e);
        }
    }

    /**
     * Join given objects.
     * @param id1 first object id
     * @param id2 second object id
     * @return joined object id
     * @throws IdNotFoundException if id error occurs
     * @throws NonRootObjectException if any object is no longer root
     */
    public synchronized long joinObjects(final long id1, final long id2)
            throws IdNotFoundException, NonRootObjectException {
        final MergeObjectsRequest request =
                new MergeObjectsRequest();
        request.setObject1Id(id1);
        request.setObject2Id(id2);
        try {
            final MergeObjectsResponse response =
                    getDataProvider().mergeObjects(request);
            return response.getObjectId();
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        } catch (InvalidMergeException e) {
            e.printStackTrace(); //FIXME: handle exception
            return -1;
        } catch (cz.cuni.mff.ufal.textan.commons.ws.NonRootObjectException e) {
            throw new NonRootObjectException(e);
        }
    }

    /**
     * Updates text of the document with the given id.
     * @param id document id
     * @param text new text
     * @throws IdNotFoundException if id error occurs
     */
    public synchronized void updateDocument(final long id, final String text)
            throws IdNotFoundException {
        final UpdateDocumentRequest request = new UpdateDocumentRequest();
        request.setDocumentId(id);
        request.setText(text);
        try {
            getDataProvider().updateDocument(request);
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Adds new document with given text.
     * @param text new document's text
     */
    public synchronized void addDocument(final String text) {
        final AddDocumentRequest request = new AddDocumentRequest();
        request.setText(text);
        getDataProvider().addDocument(request);
    }
}
