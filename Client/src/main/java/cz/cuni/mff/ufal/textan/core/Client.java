package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.ws.IDataProvider;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
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

    protected IDataProvider dataProvider = null;

    protected IDocumentProcessor documentProcessor = null;

    public Client(final Properties settings) {
        this.settings = settings;
    }

    public IDocumentProcessor getDocumentProcessor() throws WebServiceException {
        if (documentProcessor == null) {
            try {
                Service service = Service.create(new URL("http://localhost:9100/soap/document?wsdl"), new QName("http://server.textan.ufal.mff.cuni.cz/", "DocumentProcessor"));
                // Endpoint Address
                String endpointAddress = "http://localhost:9100/soap/document";
                // Add a port to the Service
                service.addPort(new QName("http://server.textan.ufal.mff.cuni.cz/DocumentProcessor", "DocumentProcessorPort"), SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
                documentProcessor = service.getPort(IDocumentProcessor.class);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new WebServiceException("Malformed URL!", e);
            }
        }
        return documentProcessor;
    }

    public IDataProvider getDataProvider() throws WebServiceException {
        if (dataProvider == null) {
            try {
                Service service = Service.create(new URL("http://localhost:9100/soap/data?wsdl"), new QName("http://server.textan.ufal.mff.cuni.cz/", "DataProvider"));
                // Endpoint Address
                String endpointAddress = "http://localhost:9100/soap/data";
                // Add a port to the Service
                service.addPort(new QName("http://server.textan.ufal.mff.cuni.cz/DataProvider", "DataProviderPort"), SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
                dataProvider = service.getPort(IDataProvider.class);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new WebServiceException("Malformed URL!", e);
            }
        }
        return dataProvider;
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
}
