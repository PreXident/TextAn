package cz.cuni.mff.ufal.textan.server.configs;

import cz.cuni.mff.ufal.textan.data.interceptors.LogInterceptor;
import cz.cuni.mff.ufal.textan.server.services.*;
import cz.cuni.mff.ufal.textan.server.ws.DataProvider;
import cz.cuni.mff.ufal.textan.server.ws.DocumentProcessor;
import cz.cuni.mff.ufal.textan.server.ws.EntityRecognizer;
import cz.cuni.mff.ufal.textan.server.ws.UsernameTokenInterceptor;
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
    private MergeService mergeService;

    @Autowired
    private NamedEntityRecognizerService namedEntityRecognizerService;

    @Autowired
    private ObjectAssignmentService objectAssignmentService;

    @Autowired
    private SaveService saveService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private LogInterceptor logInterceptor;

    /**
     * Creates spring bean with bus for CXF initialization
     *
     * @return Spring bus
     */
    @Bean(destroyMethod = "shutdown")
    public SpringBus cxf() {
        SpringBus bus = new SpringBus();
        //bus.getInInterceptors().add(new UsernameTokenInterceptor(logInterceptor));

//        LoggingInInterceptor loggingIn = new LoggingInInterceptor();
//        loggingIn.setPrettyLogging(true);
//        LoggingOutInterceptor loggingOut = new LoggingOutInterceptor();
//        loggingOut.setPrettyLogging(true);
//
//        bus.getInInterceptors().add(loggingIn);
//        bus.getInFaultInterceptors().add(loggingIn);
//        bus.getOutInterceptors().add(loggingOut);
//        bus.getOutFaultInterceptors().add(loggingOut);

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
        factory.getInInterceptors().add(usernameTokenInterceptor());
        return factory.create();
    }

    /**
     * Creates Spring bean with dataprovider class.
     *
     * @return bean for webservice
     */
    @Bean
    public DataProvider dataProvider() {
        return new DataProvider(directDataAccessService, graphService, mergeService);
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
        factory.getInInterceptors().add(usernameTokenInterceptor());
        return factory.create();
    }

    /**
     * Creates Spring bean with documentprocessor class.
     *
     * @return bean for DocumentProcessor webservice
     */
    @Bean
    public DocumentProcessor documentProcessor() {
        return new DocumentProcessor(namedEntityRecognizerService, objectAssignmentService, saveService, ticketService);
    }

    /**
     * Creates endpoint for DocumentProcessor webservice.
     *
     * @return DocumentProcessor endpoint
     */
    @Bean
    @DependsOn("cxf")
    public Server jaxEntityRecognizerServer() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceBean(entityRecognizer());
        factory.setAddress("/recognizer");
        //factory.getInInterceptors().add(usernameTokenInterceptor());
        return factory.create();
    }

    /**
     * Creates Spring bean with documentprocessor class.
     *
     * @return bean for DocumentProcessor webservice
     */
    @Bean
    public EntityRecognizer entityRecognizer() {
        return new EntityRecognizer(namedEntityRecognizerService);
    }


    @Bean
    public UsernameTokenInterceptor usernameTokenInterceptor() {
        return new UsernameTokenInterceptor(logInterceptor);
    }

}
