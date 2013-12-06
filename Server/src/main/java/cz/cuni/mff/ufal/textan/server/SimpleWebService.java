package cz.cuni.mff.ufal.textan.server;

import cz.cuni.mff.ufal.textan.commons.Document;
import cz.cuni.mff.ufal.textan.commons.ISimpleWebService;

import javax.jws.WebService;

/**
 * User: Petr Fanta
 * Date: 19.11.13
 * Time: 22:45
 */
@WebService(endpointInterface = "cz.cuni.mff.ufal.textan.commons.ISimpleWebService", serviceName = "SimpleWebService")
public class SimpleWebService implements ISimpleWebService {

    private static int counter = 0;

    public SimpleWebService() {
        System.out.println("Instance SimpleWebService c.: " + counter);
        counter++;
    }

    @Override
    public String helloWorld(){
        return "Hello world!";
    }

    @Override
    public String hello(String name) {
        return "Hello " + name;
    }

    @Override
    public Document toDocument(String text) {
        return new Document(text);
    }

    @Override
    public String fromDocument(Document document) {
        return document.getText();
    }
}
