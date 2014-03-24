package cz.cuni.mff.ufal.textan.server.configs;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cz.cuni.mff.ufal.textan.data.repositories.Data;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;


/**
 * The root spring configuration.
 * @author Petr Fanta
 */
@Configuration
@PropertySource("classpath:server.properties")
public class AppConfig {

    /**
     * The default maximum number of threads in the Jetty thread pool.
     */
    public static final int DEFAULT_THREAD_PULL_MAX_THREADS = 200;
    /**
     * The default minimum number of threads in the Jetty thread pool.
     */
    public static final int DEFAULT_THREAD_PULL_THREADS = 8;
    /**
     * The default maximum thread idle time in the Jetty thread pool.
     */
    public static final int DEFAULT_THREAD_PULL_IDLE_TIMEOUT = 60000;

    /**
     * The default port number of the Socket Address
     */
    public static final int DEFAULT_CONNECTOR_PORT = 9100;
    /**
     * The default hostname of the Socket Address.
     */
    public static final String DEFAULT_CONNECTOR_HOST = "0.0.0.0";

    @Autowired
    private Environment serverProperties;

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
                        serverProperties.getProperty("server.threadPool.minThreads", int.class, DEFAULT_THREAD_PULL_MAX_THREADS),
                        serverProperties.getProperty("server.threadPool.maxThreads", int.class, DEFAULT_THREAD_PULL_THREADS),
                        serverProperties.getProperty("server.threadPool.idleTimeout", int.class, DEFAULT_THREAD_PULL_IDLE_TIMEOUT)
                )
        );

        //TODO: what about SSL connector?
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(serverProperties.getProperty("server.connector.port", int.class, DEFAULT_CONNECTOR_PORT));
        connector.setHost(serverProperties.getProperty("server.connector.host", String.class, DEFAULT_CONNECTOR_HOST));

        server.setConnectors(new Connector[]{connector});

        return server;
    }

    @Bean
    public Data dataSource() {
        return new Data();
    }

//    @Bean
//    public DataProviderService dataProviderService() {
//        return new  DataProviderService(dataSource());
//    }
}
