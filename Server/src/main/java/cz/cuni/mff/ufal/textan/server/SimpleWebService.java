package cz.cuni.mff.ufal.textan.server;

import cz.cuni.mff.ufal.textan.commons.Document;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * User: Petr Fanta
 * Date: 19.11.13
 * Time: 22:45
 */
@WebService
public class SimpleWebService {

    private static int counter = 0;

    public SimpleWebService() {
        System.out.println("Instance SimpleWebService c.: " + counter);
        counter++;
    }

    @WebMethod
    String helloWorld(){
        return "Hello world!";
    }

    @WebMethod
    String hello(String name) {
        return "Hello " + name;
    }

    @WebMethod
    Document toDocument(String text) {
        return new Document(text);
    }

    @WebMethod
    String fromDocument(Document document) {
        return document.getText();
    }
}
