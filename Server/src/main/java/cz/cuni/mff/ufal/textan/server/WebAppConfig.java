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

@Configuration
public class WebAppConfig {

    @Bean( destroyMethod = "shutdown" )
    public SpringBus cxf() {
        return new SpringBus();
    }

    @Bean
    public Server jaxWsServer() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceBean(simpleWebService());
        factory.setAddress("/simple");
        return factory.create();
    }

    @Bean
    public SimpleWebService simpleWebService() {
        return new SimpleWebService();
    }
}
