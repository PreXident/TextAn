package cz.cuni.mff.ufal.textan.server.configs;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;


/**
 * The root spring configuration.
 * @author Petr Fanta
 */
@Configuration
@PropertySource("classpath:server-default.properties")
@PropertySource(value = "file:./server.properties", ignoreResourceNotFound = true)
@Import(DataConfig.class)
@ComponentScan("cz.cuni.mff.ufal.textan.server.services")
public class AppConfig {

    @Autowired
    private Environment serverProperties;

    @Autowired
    private DataConfig dataConfig;

    /**
     * Creates preconfigured Jetty server.
     *
     * @return the server
     * @see org.eclipse.jetty.server.Server
     */
    @Bean(destroyMethod = "stop")
    public Server server() {

        Server server = new Server(
                new QueuedThreadPool(
                        serverProperties.getProperty("server.threadPool.maxThreads", int.class),
                        serverProperties.getProperty("server.threadPool.minThreads", int.class),
                        serverProperties.getProperty("server.threadPool.idleTimeout", int.class)
                )
        );

        //TODO: what about SSL connector?
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(serverProperties.getProperty("server.connector.port", int.class));
        connector.setHost(serverProperties.getProperty("server.connector.host", String.class));

        server.setConnectors(new Connector[]{connector});

        return server;
    }

}
