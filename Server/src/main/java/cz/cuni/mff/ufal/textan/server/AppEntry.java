package cz.cuni.mff.ufal.textan.server;

import javax.xml.ws.Endpoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.UrlResource;
import org.eclipse.jetty.servlet.ServletContextHandler;
/**
 * User: Petr Fanta
 * Date: 19.11.13
 * Time: 22:01
 */

public class AppEntry {

    public static void main(String[] args) throws Exception {
//        Server server = new Server(9002);
//        server.start();
//        server.join();

//        Resource config = Resource.newSystemResource(args.length == 1?args[0]:"cz/cuni/mff/ufal/textan/server/jetty-spring.xml");
//        XmlBeanFactory bf = new XmlBeanFactory(new UrlResource(config.getURL()));
//        Server server = (Server)bf.getBean(args.length == 2?args[1]:"Server");
//        server.join();

//        System.out.println("Starting Server");
//        HelloWorld implementor = new HelloWorld();
//        String address = "http://localhost:9000/helloWorld";
//        Endpoint.publish(address, implementor);
//        System.out.println("Server ready...");
//        Thread.sleep(5 * 60 * 1000);
//        System.out.println("Server exiting");
//        System.exit(0);

        new ClassPathXmlApplicationContext(new String[] {"cz/cuni/mff/ufal/textan/server/beans-jetty.xml"});
        System.out.println("Server ready...");
        Thread.sleep(5 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
