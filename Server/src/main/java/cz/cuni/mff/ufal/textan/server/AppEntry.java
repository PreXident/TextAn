package cz.cuni.mff.ufal.textan.server;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * User: Petr Fanta
 * Date: 19.11.13
 * Time: 22:01
 */

public class AppEntry {

    public static void main(String[] args) throws Exception {

        Server server = new Server(9100);

        final ServletHolder servletHolder = new ServletHolder(new CXFServlet());
        final ServletContextHandler context = new ServletContextHandler();

        context.setContextPath("/");
        context.addServlet(servletHolder, "/soap/*");
        context.addEventListener(new ContextLoaderListener());

        context.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
        context.setInitParameter("contextConfigLocation", AppConfig.class.getName());

        System.out.println("Set Handler.");
        server.setHandler(context);
        System.out.println("Start server.");
        server.start();

        System.out.println("Server running...");
        server.join();
    }
}
