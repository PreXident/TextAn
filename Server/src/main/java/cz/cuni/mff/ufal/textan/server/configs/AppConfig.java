package cz.cuni.mff.ufal.textan.server.configs;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTypeTableDAO;
import cz.cuni.mff.ufal.textan.server.commands.CommandInvoker;
import cz.cuni.mff.ufal.textan.server.nametagIntegration.NameTagServices;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
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

/**
 * The root spring configuration.
 *
 * @author Petr Fanta
 */
@Configuration
@Import(DataConfig.class)
@ComponentScan("cz.cuni.mff.ufal.textan.server.services")
public class AppConfig implements ApplicationContextAware {

    private static final String DEFAULT_SERVER_PROPERTIES = "server-default.properties";
    private static final String USER_SERVER_PROPERTIES = "server.properties";

    private ApplicationContext context;

    @SuppressWarnings("unused")
    @Autowired
    private DataConfig dataConfig;

    @SuppressWarnings("unused")
    @Autowired
    private IObjectTypeTableDAO objectTypeTableDAO;

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

        Server server = new Server(
                new QueuedThreadPool(
                        Integer.parseInt(serverProperties().getProperty("server.threadPool.maxThreads")),
                        Integer.parseInt(serverProperties().getProperty("server.threadPool.minThreads")),
                        Integer.parseInt(serverProperties().getProperty("server.threadPool.idleTimeout"))
                )
        );

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

    @Bean
    public NameTagServices nametagServices() {
        return new NameTagServices(objectTypeTableDAO);
    }
}
