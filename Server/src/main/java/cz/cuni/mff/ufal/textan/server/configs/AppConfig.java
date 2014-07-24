package cz.cuni.mff.ufal.textan.server.configs;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IEntityViewDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTypeTableDAO;
import cz.cuni.mff.ufal.textan.server.commands.CommandInvoker;
import cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

/**
 * The root spring configuration.
 *
 * @author Petr Fanta
 * @author Jakub Vlček
 */
@Configuration
@Import({DataConfig.class, TextProConfig.class})
@ComponentScan("cz.cuni.mff.ufal.textan.server.services")
public class AppConfig implements ApplicationContextAware {

    /** Path to a default server property file (inside jar). */
    private static final String DEFAULT_SERVER_PROPERTIES = "server-default.properties";
    /** Path to an user server property file. The file should be relative to working directory. */
    private static final String USER_SERVER_PROPERTIES = "server.properties";

    /** A Spring application context in which a instance of this config lives. */
    private ApplicationContext context;

    @SuppressWarnings("unused")
    @Autowired
    private IObjectTypeTableDAO objectTypeTableDAO;

    @SuppressWarnings("unused")
    @Autowired
    private IDocumentTableDAO documentTableDAO;

    @SuppressWarnings("unused")
    @Autowired
    private IEntityViewDAO entityViewDAO;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * Loads properties from property files.
     *
     * @return a combination of the default and a user properties, if a user define some, otherwise default properties
     * @throws IOException thrown when loading fails
     */
    @Bean
    public Properties serverProperties() throws IOException {

        //load default properties from jar
        Properties defaults = new Properties();
        defaults.load(getClass().getClassLoader().getResourceAsStream(DEFAULT_SERVER_PROPERTIES));


        //load user properties
        Properties properties = new Properties(defaults);
        File userPropertiesFile = new File(USER_SERVER_PROPERTIES);
        if (userPropertiesFile.exists()) {
            try (InputStream stream = new FileInputStream(userPropertiesFile)) {
                properties.load(stream);
            }
        }

        return properties;
    }

    /**
     * Creates a pre-configured Jetty server.
     *
     * @return the server
     * @see org.eclipse.jetty.server.Server
     */
    @Bean(destroyMethod = "stop")
    public Server server() throws IOException {

        int maxThreads = Integer.parseInt(serverProperties().getProperty("server.threadPool.maxThreads"));
        int minThreads = Integer.parseInt(serverProperties().getProperty("server.threadPool.minThreads"));
        int idleTimeout = Integer.parseInt(serverProperties().getProperty("server.threadPool.idleTimeout"));

        BlockingQueue<Runnable> acceptQueue = null;
        String acceptQueueSizeProperty = serverProperties().getProperty("server.acceptQueue.size");
        if (acceptQueueSizeProperty != null) {
            int acceptQueueSize = Integer.parseInt(acceptQueueSizeProperty);
            int capacity = Math.max(maxThreads, minThreads);
            int grow = Math.min(maxThreads, minThreads);
            int maxCapacity = Math.max(acceptQueueSize, capacity);

            acceptQueue = new BlockingArrayQueue<>(capacity, grow, maxCapacity);
        }

        ThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout, acceptQueue);
        Server server = new Server(threadPool);

        //TODO: what about SSL connector?
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(Integer.parseInt(serverProperties().getProperty("server.connector.port")));
        connector.setHost(serverProperties().getProperty("server.connector.host"));

        server.setConnectors(new Connector[]{connector});

        ServletHolder servletHolder = new ServletHolder(new CXFServlet());

        //Setup servlet handler
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        servletContextHandler.addServlet(servletHolder, "/soap/*");
        servletContextHandler.setInitParameter("contextConfigLocation", WebAppConfig.class.getName());

        //Create root spring's web application context for servlets
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.setParent(context);
        webContext.setServletContext(servletContextHandler.getServletContext());

        //Register root context
        servletContextHandler.addEventListener(new ContextLoaderListener(webContext));

        server.setHandler(servletContextHandler);

        return server;
    }

    /**
     * Creates a command invoker.
     *
     * @return the command invoker
     * @see cz.cuni.mff.ufal.textan.server.commands.CommandInvoker
     */
    @Bean(destroyMethod = "stop")
    public CommandInvoker commandInvoker() {
        return new CommandInvoker();
    }

    /**
     * Creates a named entity recognizer
     * @return the recognizer
     * @see cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer
     */
    @Bean(initMethod = "init")
    public NamedEntityRecognizer namedEntityRecognizer() {
        return new NamedEntityRecognizer(objectTypeTableDAO, entityViewDAO, documentTableDAO);
    }
}
