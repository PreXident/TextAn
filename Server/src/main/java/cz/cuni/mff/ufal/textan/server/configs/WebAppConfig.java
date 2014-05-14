package cz.cuni.mff.ufal.textan.server.configs;

import cz.cuni.mff.ufal.textan.server.services.*;
import cz.cuni.mff.ufal.textan.server.ws.DataProvider;
import cz.cuni.mff.ufal.textan.server.ws.DocumentProcessor;
import cz.cuni.mff.ufal.textan.server.ws.TicketInterceptor;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


/**
 * Spring configuration for CXFServlet
 *
 * @author Petr Fanta
 */
@Configuration
public class WebAppConfig {

    @Autowired
    private DirectDataAccessService directDataAccessService;

    @Autowired
    private GraphService graphService;

    @Autowired
    private NamedEntityRecognizerService namedEntityRecognizerService;

    @Autowired
    private ObjectAssignmentService objectAssignmentService;

    @Autowired
    private SaveService saveService;

    /**
     * Creates spring bean with bus for CXF initialization
     *
     * @return Spring bus
     */
    @Bean(destroyMethod = "shutdown")
    public SpringBus cxf() {
        SpringBus bus = new SpringBus();
        bus.getInInterceptors().add(new TicketInterceptor());

        return bus;
    }

    /**
     * Creates endpoint for DataProvider webservice.
     *
     * @return DataProvider endpoint
     */
    @Bean
    @DependsOn("cxf")
    public Server jaxDataProviderServer() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceBean(dataProvider());
        factory.setAddress("/data");
        return factory.create();
    }

    /**
     * Creates Spring bean with dataprovider class.
     *
     * @return bean for webservice
     */
    @Bean
    public DataProvider dataProvider() {
        return new DataProvider(directDataAccessService, graphService);
    }

    /**
     * Creates endpoint for DocumentProcessor webservice.
     *
     * @return DocumentProcessor endpoint
     */
    @Bean
    @DependsOn("cxf")
    public Server jaxDocumentProcessorServer() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceBean(documentProcessor());
        factory.setAddress("/document");
        return factory.create();
    }

    /**
     * Creates Spring bean with documentprocessor class.
     *
     * @return bean for DocumentProcessor webservice
     */
    @Bean
    public DocumentProcessor documentProcessor() {
        return new DocumentProcessor(namedEntityRecognizerService, objectAssignmentService, saveService);
    }
}
