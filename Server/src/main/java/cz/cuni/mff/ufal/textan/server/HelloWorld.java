package cz.cuni.mff.ufal.textan.server;

import cz.cuni.mff.ufal.textan.commons.IHelloWorld;
import javax.jws.WebService;

/**
 * Implementation of testing webservice.
 */
@WebService(endpointInterface = "cz.cuni.mff.ufal.textan.commons.IHelloWorld", serviceName = "HelloWorld")
public class HelloWorld implements IHelloWorld {

    @Override
    public String hello() {
        return "Hello, World!";
    }

    @Override
    public String helloWithName(String name) {
        return String.format("Hello, %s!", name);
    }
}
