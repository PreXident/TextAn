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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

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
    private AbstractApplicationContext context;

    @Autowired
    private Environment serverProperties;

    @Autowired
    private DataConfig dataConfig;

    @Autowired
    private IObjectTypeTableDAO objectTypeTableDAO;

    /**
     * Creates a pre-configured Jetty server.
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
