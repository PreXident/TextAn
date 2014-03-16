package cz.cuni.mff.ufal.textan.server.configs;

//import cz.cuni.mff.ufal.textan.server.ws.DataProvider;
//import cz.cuni.mff.ufal.textan.server.ws.DocumentProcessor;
import cz.cuni.mff.ufal.textan.server.DataProvider;
import cz.cuni.mff.ufal.textan.server.DocumentProcessor;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/*
 * User: Petr Fanta
 * Date: 28.11.13
 * Time: 18:04
 */

/**
 * Spring configuration for CXFServlet
 */
@Configuration
public class WebAppConfig {

    /**
     * Creates spring bean with bus for CXF initialization
     * @return Return Spring bus
     */
    @Bean( destroyMethod = "shutdown" )
    public SpringBus cxf() {
        return new SpringBus();
    }

    /**
     * Creates endpoint for DataProvider webservice.
     * @return DataProvider endpoint
     */
    @Bean
    @DependsOn( "cxf" )
    public Server jaxDataProviderServer() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceBean(dataProvider());
        factory.setAddress("/data");
        return factory.create();
    }

    /**
     * Creates Spring bean with dataprovider class.
     * @return bean for webservice
     */
    @Bean
    public DataProvider dataProvider() {
        return new DataProvider();
    }

    /**
     * Creates endpoint for DocumentProcessor webservice.
     * @return DocumentProcessor endpoint
     */
    @Bean
    @DependsOn( "cxf" )
    public Server jaxDocumentProcessorServer() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceBean(documentProcessor());
        factory.setAddress("/document");
        return factory.create();
    }

    /**
     * Creates Spring bean with documentprocessor class.
     * @return bean for DocumentProcessor webservice
     */
    @Bean
    public DocumentProcessor documentProcessor() {
        return new DocumentProcessor();
    }
}
