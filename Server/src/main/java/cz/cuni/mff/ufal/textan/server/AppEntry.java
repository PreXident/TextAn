package cz.cuni.mff.ufal.textan.server;

import cz.cuni.mff.ufal.textan.server.commands.CommandInvoker;
import cz.cuni.mff.ufal.textan.server.configs.AppConfig;
import cz.cuni.mff.ufal.textan.server.nametagIntegration.NameTagServices;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;

/**
 * Server entry point.
 *
 * @author Petr Fanta
 */
public class AppEntry {

    private static final Logger LOG = LoggerFactory.getLogger(AppEntry.class);

    //TODO: just for testing
    private static final File NAMETAG_MODEL = new File("models/czech-cnec2.0-140304.ner");

    public static void main(String[] args) {

        try {
            //Create root application context
            AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            context.registerShutdownHook();

            Server server = context.getBean(Server.class);
            CommandInvoker invoker = context.getBean(CommandInvoker.class);
            NameTagServices nameTagServices = context.getBean(NameTagServices.class);

            LOG.info("Initialize named entity recognizer");
            nameTagServices.bindModel(NAMETAG_MODEL.toString());

            LOG.info("Start server");
            server.start();
            LOG.info("Server command invoker");
            invoker.start();

        } catch (Exception e) {

            LOG.error("Unexpected exception: ", e);
            System.exit(1);
        }
    }
}
