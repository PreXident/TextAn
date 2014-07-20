package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.ObjectOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.RelationOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.UsernameToken;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetDocumentsContainingObjectByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetDocumentsContainingObjectByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetFilteredDocumentsRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetFilteredDocumentsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetFilteredObjectsRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetFilteredObjectsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetGraphByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetGraphByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectTypesResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsAndRelationsOccurringInDocumentRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsAndRelationsOccurringInDocumentResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsByTypeIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsByTypeIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRelationTypesResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRelationsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRolesForRelationTypeByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.MergeObjectsRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.MergeObjectsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsFromStringRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsFromStringRequest.Entities;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetAssignmentsFromStringResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEditingTicketRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEditingTicketResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesFromStringRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesFromStringResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetProblemsRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetProblemsResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.SaveProcessedDocumentFromStringRequest;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.SaveProcessedDocumentFromStringResponse;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.commons.ws.IDataProvider;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import cz.cuni.mff.ufal.textan.core.graph.ObjectGrapher;
import cz.cuni.mff.ufal.textan.core.graph.RelationGrapher;
import cz.cuni.mff.ufal.textan.core.processreport.Problems;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
 * Handles all communicatioin with the server.
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
     * @param username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Adds JAX-WS Handler which adds UsernameToken header into SOAP message.
     * @param binding JAW-WS bindings (from web service port)
     */
    private void addSOAPHandler(Binding binding) {
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
     * Returns {@link #documentProcessor}, it is created if needed.
     * @return document processor
     */
    private IDocumentProcessor getDocumentProcessor() {
        if (documentProcessor == null) {
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

                Binding documentProcessorBinding = ((BindingProvider) processor).getBinding();
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
            try {
                Service service = Service.create(
                        new URL(settings.getProperty("url.data.wsdl", "http://textan.ms.mff.cuni.cz:9500/soap/data?wsdl")),
                        DATA_PROVIDER_SERVICE);
                // Endpoint Address
                String endpointAddress = settings.getProperty("url.data", "http://textan.ms.mff.cuni.cz:9500/soap/data");
                // Add a port to the Service
                service.addPort(DATA_PROVIDER_PORT, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
                final IDataProvider provider = service.getPort(IDataProvider.class);

                Binding dataProviderBinding = ((BindingProvider) provider).getBinding();
                addSOAPHandler(dataProviderBinding);
                dataProvider = new SynchronizedDataProvider(provider);

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
     * @param object object whose document should be returned
     * @param processed only processed documents?
     * @param filter document text filter
     * @param first index of the first object
     * @param size maximal number of objects
     * @return list of documents containing object with given id
     * @throws IdNotFoundException if id error occurs
     */
    public synchronized Pair<List<Document>, Integer> getDocumentsList(
            final Object object, final Processed processed, final String filter,
            final int first, final int size) throws IdNotFoundException {
        try {
            final GetDocumentsContainingObjectByIdRequest request =
                    new GetDocumentsContainingObjectByIdRequest();
            request.setObjectId(object.getId());
            request.setFirstResult(first);
            request.setMaxResults(size);
            //TODO set parameters for filtering when ready
            final GetDocumentsContainingObjectByIdResponse response =
                    getDataProvider().getDocumentsContainingObjectById(request);
            final List<Document> list = response.getDocumentCountPairs().stream()
                    .map(x -> new Document(x.getDocument(), x.getCountOfOccurrences()))
                    //TODO remove filtering emulation
                    .filter(d -> processed.filter(d))
                    .filter(d -> d.getText().contains(filter))
                    .collect(Collectors.toCollection(ArrayList::new));
            return new Pair<>(list, response.getTotalNumberOfResults());
        } catch (cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException e) {
            throw new IdNotFoundException(e);
        }
    }

    /**
     * Returns documents containing given relation.
     * @param relation relation whose document should be returned
     * @param processed only processed documents?
     * @param filter document text filter
     * @param first index of the first object
     * @param size maximal number of objects
     * @return list of documents containing object with given id
     * @throws IdNotFoundException if id error occurs
     */
    public synchronized Pair<List<Document>, Integer> getDocumentsList(
            final Relation relation, final Processed processed, final String filter,
            final int first, final int size) throws IdNotFoundException {
        //TODO call proper methods when they are ready
        final Pair<List<Document>, Integer> pair =
                getDocumentsList(processed, filter, 0, Integer.MAX_VALUE);
        final List<Document> documents = pair.getFirst();
        final List<Document> result = new ArrayList<>();
        for (Document document : documents) {
            final DocumentData documentData = getDocumentData(document.getId());
            if (documentData.getRelations().containsKey(relation.getId())) {
                result.add(document);
            }
        }
        return new Pair<>(result.stream()
                .skip(first)
                .limit(size)
                .collect(Collectors.toList()), result.size());
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
     * Returns centered graph with limited distance.
     * @param centerId center object id
     * @param distance maximal distance from center
     * @return centered graph with limited distance
     * @throws IdNotFoundException if object id is not found
     */
    public synchronized Graph getGraph(final long centerId, final int distance)
            throws IdNotFoundException {
        final GetGraphByIdRequest request = new GetGraphByIdRequest();
        request.setDistance(distance);
        request.setObjectId(centerId);
        try {
            final GetGraphByIdResponse response =
                    getDataProvider().getGraphById(request);
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
    public synchronized void getObjects(final Ticket ticket, final String text, final List<Entity> entities) {
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

        final Map<Long, Object> objects = new HashMap<>();
        for (Assignment assignment : response.getAssignments()) {
            final Entity ent = map.get(assignment.getEntity().getPosition());
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
     * Returns problems with report saving.
     * @param ticket editing ticket
     * @return problems with report saving
     */
    public synchronized Problems getProblems(final Ticket ticket) {
        final GetProblemsRequest request =  new GetProblemsRequest();
        final GetProblemsResponse response = getDocumentProcessor().getProblems(request, ticket.toTicket());
        return new Problems(response);
    }

    public synchronized Graph getRelationGraph(final long rootId,
            final int distance) throws IdNotFoundException {
        //TODO call proper methods when they are ready
        final Map<Long, Object> allObjects = getObjectsMap();
        final GetRelationsResponse response = getDataProvider().getRelations(new Void());
        final Relation relation = response.getRelations().stream()
                .map(rel -> new Relation(rel, allObjects))
                .filter(rel -> rel.getId() == rootId)
                .findFirst().get();
        final Map<Long, Object> objects = new HashMap<>();
        final Set<Relation> relations = new HashSet<>();
        relation.getObjects().stream()
                .map(Triple<Integer, String, Object>::getThird)
                .map(obj -> {
                    try {
                        return this.getGraph(obj.getId(), distance);
                    } catch (Exception e) {
                        return new Graph(Collections.emptyMap(), Collections.emptyList());
                    }
                })
                .forEach(graph -> {
                    objects.putAll(graph.getNodes());
                    relations.addAll(graph.getEdges());
                });
        return new Graph(objects, relations);
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
        //TODO call proper methods when they are ready
        final Map<Long, Object> objects = getObjectsMap();
        final GetRelationsResponse response = getDataProvider().getRelations(new Void());
        Stream<Relation> stream = response.getRelations().stream()
                .map(rel -> new Relation(rel, objects));
        if (type != null) {
            stream = stream.filter(rel -> rel.getType().getId() == type.getId());
        }
        if (!filter.isEmpty()) {
            stream = stream.filter(rel -> rel.getAnchorString().contains(filter));
        }
        final List<Relation> relations = stream
                .skip(first)
                .limit(size)
                .collect(Collectors.toList());
        return new Pair<>(relations, response.getRelations().size());
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
     * Saves processed documents.
     *
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
        for (Entity ent : reportEntities) {
            if (ent.getCandidate() != null) {
                objects.add(ent.getCandidate().toObject());
                objectOccurrences.add(ent.toObjectOccurrence());
            }
        }

        final List<cz.cuni.mff.ufal.textan.commons.models.Relation> relations = request.getRelations();
        final List<RelationOccurrence> relationOccurrences =
                request.getRelationOccurrences();
        for (RelationBuilder relation : reportRelations) {
            relations.add(relation.toRelation());
            final RelationOccurrence occ = relation.toRelationOccurrence();
            relationOccurrences.add(occ);
        }

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
     * Join given objects.
     * @param id1 first object id
     * @param id2 second object id
     * @return joined object id
     * @throws IdNotFoundException if id error occurs
     */
    public long joinObjects(final long id1, final long id2)
            throws IdNotFoundException {
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
        }
    }
}
