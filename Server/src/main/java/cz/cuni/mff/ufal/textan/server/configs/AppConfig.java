package cz.cuni.mff.ufal.textan.server.configs;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IEntityViewDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTypeTableDAO;
import cz.cuni.mff.ufal.textan.server.commands.CommandInvoker;
import cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer;
import cz.cuni.mff.ufal.textan.server.web.TextanWelcomePage;
import cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Path to a default server property file (inside jar).
     */
    private static final String DEFAULT_SERVER_PROPERTIES = "server-default.properties";
    /**
     * Path to an user server property file. The file should be relative to working directory.
     */
    private static final String USER_SERVER_PROPERTIES = "server.properties";

    /**
     * A Spring application context in which a instance of this config lives.
     */
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
        if (acceptQueueSizeProperty != null && !acceptQueueSizeProperty.isEmpty()) {
            int acceptQueueSize = Integer.parseInt(acceptQueueSizeProperty);
            int capacity = Math.max(maxThreads, minThreads);
            int grow = Math.min(maxThreads, minThreads);
            int maxCapacity = Math.max(acceptQueueSize, capacity);

            acceptQueue = new BlockingArrayQueue<>(capacity, grow, maxCapacity);
        }

        ThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout, acceptQueue);
        Server server = new Server(threadPool);

        boolean useSsl = false;
        String sslProperty = serverProperties().getProperty("server.ssl");
        if (sslProperty != null && !sslProperty.isEmpty()) {
            useSsl = Boolean.parseBoolean(sslProperty);
        }

        List<ServerConnector> connectors = new ArrayList<>(2);
        if (useSsl) {
            int sslPort = Integer.parseInt(serverProperties().getProperty("server.ssl.port"));

            HttpConfiguration httpConfig = new HttpConfiguration();
            httpConfig.setSecureScheme("https");
            httpConfig.setSecurePort(sslPort);

            ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
            http.setPort(Integer.parseInt(serverProperties().getProperty("server.connector.port")));
            http.setHost(serverProperties().getProperty("server.connector.host"));

            HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
            httpsConfig.addCustomizer(new SecureRequestCustomizer());

            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(serverProperties().getProperty("server.ssl.keyStore.path"));
            sslContextFactory.setKeyStorePassword(serverProperties().getProperty("server.ssl.keyStore.password"));
            sslContextFactory.setKeyManagerPassword(serverProperties().getProperty("server.ssl.keyManager.password"));
            sslContextFactory.setKeyStoreType(serverProperties().getProperty("server.ssl.keyStore.type", "JKS"));
            //sslContextFactory.setCertAlias();

            ServerConnector https = new ServerConnector(
                    server,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(httpsConfig)
            );
            https.setPort(Integer.parseInt(serverProperties().getProperty("server.ssl.port")));
            https.setHost(serverProperties().getProperty("server.connector.host"));

            connectors.add(http);
            connectors.add(https);
        } else {
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(Integer.parseInt(serverProperties().getProperty("server.connector.port")));
            connector.setHost(serverProperties().getProperty("server.connector.host"));

            connectors.add(connector);
        }

        server.setConnectors(connectors.toArray(new ServerConnector[connectors.size()]));

        ServletHolder servletHolder = new ServletHolder(new CXFServlet());

        //Setup servlet handler
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        servletContextHandler.addServlet(servletHolder, "/soap/*");
        servletContextHandler.setInitParameter("contextConfigLocation", WebAppConfig.class.getName());
        servletContextHandler.addServlet(TextanWelcomePage.class, "/");
        //servletContextHandler.setErrorHandler(new TextanErrorHandler()); //FIXME

        if (useSsl) {
            Constraint constraint = new Constraint();
            constraint.setDataConstraint(2);

            ConstraintMapping mapping = new ConstraintMapping();
            mapping.setConstraint(constraint);
            mapping.setPathSpec("/*");

            ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
            securityHandler.addConstraintMapping(mapping);

            servletContextHandler.setSecurityHandler(securityHandler);
        }

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
     *
     * @return the recognizer
     * @see cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer
     */
    @Bean(initMethod = "init")
    @DependsOn("transactionManager")
    public NamedEntityRecognizer namedEntityRecognizer() {
        return new NamedEntityRecognizer(objectTypeTableDAO, entityViewDAO, documentTableDAO);
    }
}
