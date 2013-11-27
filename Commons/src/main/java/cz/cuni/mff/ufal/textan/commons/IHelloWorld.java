package cz.cuni.mff.ufal.textan.commons;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Hello World interface for testing webservice.
 */
@WebService
public interface IHelloWorld {
    String hello();
    String helloWithName(@WebParam(name="name") String name);
}
