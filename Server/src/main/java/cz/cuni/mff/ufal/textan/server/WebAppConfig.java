package cz.cuni.mff.ufal.textan.server;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
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
     * Creates endpoint for SimpleWebService
     * @return Returns endpoint
     */
    @Bean
    public Server jaxWsServer() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceBean(simpleWebService());
        factory.setAddress("/simple");
        return factory.create();
    }

    /**
     * Creates Spring bean with webservice class
     * @return Returns bean for SimpleWebService
     */
    @Bean
    public SimpleWebService simpleWebService() {
        return new SimpleWebService();
    }
}
