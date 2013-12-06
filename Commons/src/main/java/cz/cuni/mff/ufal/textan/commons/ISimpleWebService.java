package cz.cuni.mff.ufal.textan.commons;

import javax.jws.WebService;

/**
 * Interface for simple web service.
 */
@WebService
public interface ISimpleWebService {
    String helloWorld();

    String hello(String name);

    Document toDocument(String text);

    String fromDocument(Document document);
}
