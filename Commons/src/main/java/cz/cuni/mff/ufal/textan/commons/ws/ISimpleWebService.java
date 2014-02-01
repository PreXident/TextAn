package cz.cuni.mff.ufal.textan.commons.ws;

import cz.cuni.mff.ufal.textan.commons.models.Document;

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
