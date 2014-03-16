package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons_old.models.Rating;
import cz.cuni.mff.ufal.textan.commons_old.ws.IDataProvider;
import cz.cuni.mff.ufal.textan.commons_old.ws.IDocumentProcessor;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
     * Returns {@link #documentProcessor}, it is created if needed.
     * @return document processor
     */
    private IDocumentProcessor getDocumentProcessor() {
        if (documentProcessor == null) {
            try {
                Service service = Service.create(new URL("http://localhost:9100/soap/document?wsdl"), new QName("http://server.textan.ufal.mff.cuni.cz/", "DocumentProcessorService"));
                // Endpoint Address
                String endpointAddress = "http://localhost:9100/soap/document";
                // Add a port to the Service
                service.addPort(new QName("http://server.textan.ufal.mff.cuni.cz/DocumentProcessorService", "DocumentProcessorPort"), SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
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
                Service service = Service.create(new URL("http://localhost:9100/soap/data?wsdl"), new QName("http://server.textan.ufal.mff.cuni.cz/", "DataProviderService"));
                // Endpoint Address
                String endpointAddress = "http://localhost:9100/soap/data";
                // Add a port to the Service
                service.addPort(new QName("http://server.textan.ufal.mff.cuni.cz/DataProviderService", "DataProviderPort"), SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
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
     * @param text text to process
     * @return entities identified in text
     * @see IDocumentProcessor#getEntities(java.lang.String)
     */
    public List<Entity> getEntities(final String text) {
        return Arrays.asList(getDocumentProcessor().getEntities(text)).stream()
                .map(ent -> new Entity(ent))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns new {@link Graph}.
     * @param centerId id of the central object
     * @param distance distance to cover
     * @return new Graph
     * @see IDataProvider#getGraph(cz.cuni.mff.ufal.textan.commons.models.Object, int)
     */
    public Graph getGraph(final int centerId, final int distance) {
        return new Graph(getDataProvider().getGraphByID(centerId, distance));
    }

    /**
     * Fills candidates of entities.
     * @param text report to process
     * @param entities where to store candidates
     * @see IDocumentProcessor#getObjects(java.lang.String, cz.cuni.mff.ufal.textan.commons.models.Entity[])
     */
    public void getObjects(final String text, final List<Entity> entities) {
        Rating[] candidates = getDocumentProcessor().getObjects(text, entities.stream()
                .map(ent -> ent.toEntity())
                .toArray(i -> new cz.cuni.mff.ufal.textan.commons_old.models.Entity[i])
        );
        for (int i = 0; i < candidates.length; ++i) {
            entities.get(i).getCandidates().clear();
            final Rating candidate = candidates[i];
            for (int j = 0; j < candidate.candidate.length; ++j) {
                entities.get(i).getCandidates().put(
                        candidate.rating[j],
                        new Object(candidate.candidate[j])
                );
            }
        }
    }

    /**
     * Returns list of all objects in the system with specified type.
     * @param typeId type id to filter
     * @return list of all objects in the system with specified type
     * @see IDataProvider#getObjectsByTypeId(int)
     */
    public List<Object> getObjectsListByTypeId(final int typeId) {
        return Arrays.asList(getDataProvider().getObjectsByTypeId(typeId)).stream()
                .map(obj -> new Object(obj))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns list of all objects in the system.
     * @return list of all objects in the system
     * @see IDataProvider#getObjects()
     */
    public List<Object> getObjectsList() {
        return Arrays.asList(getDataProvider().getObjects()).stream()
                .map(obj -> new Object(obj))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns set of all objects in the system.
     * @return set of all objects in the system
     * @see IDataProvider#getObjects()
     */
    public Set<Object> getObjectsSet() {
        return Arrays.asList(getDataProvider().getObjects()).stream()
                .map(obj -> new Object(obj))
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns list of all object types in the system.
     * @return list of all object types in the system
     * @see IDataProvider#getObjectTypes()
     */
    public List<ObjectType> getObjectTypesList() {
        return Arrays.asList(getDataProvider().getObjectTypes()).stream()
                .map(type -> new ObjectType(type))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns set of all object types in the system.
     * @return set of all object types in the system
     * @see IDataProvider#getObjectTypes()
     */
    public Set<ObjectType> getObjectTypesSet() {
        return Arrays.asList(getDataProvider().getObjectTypes()).stream()
                .map(type -> new ObjectType(type))
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
        return new Ticket(getDocumentProcessor().getTicket(username));
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
     * @param text report text
     * @param reportEntities report entities
     * @param reportRelations report relations
     * @param ticket ticket
     */
    public void saveProcessedDocument(final String text, final List<Entity> reportEntities, final Set<Relation> reportRelations, final Ticket ticket) {
        final int documentId = getDocumentProcessor().addDocument(text);
        getDocumentProcessor().saveProcessedDocument(
                documentId,
                reportEntities.stream()
                        .sequential()
                        .filter(ent -> ent.getCandidate() != null)
                        .map(ent -> ent.getCandidate().toObject())
                        .toArray(i -> new cz.cuni.mff.ufal.textan.commons_old.models.Object[i]),
                reportRelations.stream()
                    .map(rel -> rel.toRelation())
                    .toArray(i -> new cz.cuni.mff.ufal.textan.commons_old.models.Relation[i]),
                ticket.toTicket(),
                false);
    }
}
