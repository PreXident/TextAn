package cz.cuni.mff.ufal.textan.server;

import cz.cuni.mff.ufal.textan.server.configs.AppConfig;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Server entry point.
 *
 * @author Petr Fanta
 */
public class AppEntry {

    private static final Logger LOG = LoggerFactory.getLogger(AppEntry.class);

    public static void main(String[] args) {

        try {
            //Create root aplication context
            AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            context.registerShutdownHook();

            Server server = (Server) context.getBean("server");

            LOG.info("Start server.");
            server.start();

            LOG.info("Server running.");
            server.join();

        } catch (Exception e) {

            LOG.error("Unexpected exception: ", e);
            System.exit(1);
        }
    }
}
