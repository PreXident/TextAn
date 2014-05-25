package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.UsernameToken;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapHeaderInterceptor;
import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.ServiceModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;

/**
 * A CXF interceptor which extracts UsernameToken header from SOAP message.
 * @author Petr Fanta
 */
public class UsernameTokenInterceptor extends AbstractSoapInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(UsernameTokenInterceptor.class);

    private static final QName USERNAME_TOKEN_HEADER = new QName("http://models.commons.textan.ufal.mff.cuni.cz", "usernameToken");

    /**
     * Instantiates a new Ticket interceptor.
     */
    public UsernameTokenInterceptor() {

        //Ticket interceptor must be processed before CXF converts soap to java calls
        super(Phase.UNMARSHAL);
        getBefore().add(SoapHeaderInterceptor.class.getName());
    }

    /**
     * Searches tickets in SOAP message and sets username.
     *
     * @param message
     * @throws Fault
     */
    @Override
    public void handleMessage(SoapMessage message) throws Fault {

        Header header;
        if ((header = message.getHeader(USERNAME_TOKEN_HEADER)) != null) {
            UsernameToken token = getToken(message, header);
            LOG.debug("Username token: {}", token);

            String username = token.getUsername();

            if ((username == null) || (username.isEmpty())) {
                Fault exception = new Fault(new Exception("User name can not be empty!"));
                exception.setFaultCode(Fault.FAULT_CODE_SERVER);

                LOG.warn("User name can not be empty!", exception);
                throw exception;
            }

            LOG.debug("Username: {}", username);

        } else {
            //TODO: better exception
            Fault exception = new Fault(new Exception("Did not find UsernameToken header"));
            exception.setFaultCode(Fault.FAULT_CODE_SERVER);

            LOG.warn("Did not find any header with Ticket!", exception);
            throw exception;
        }
    }

    private UsernameToken getToken(SoapMessage message, Header header) {
        DataReader<Node> dataReader = getNodeDataReader(message);
        return (UsernameToken) dataReader.read(USERNAME_TOKEN_HEADER, (Node) header.getObject(), UsernameToken.class);
    }

    private DataReader<Node> getNodeDataReader(SoapMessage message) {
        Service service = ServiceModelUtil.getService(message.getExchange());
        return service.getDataBinding().createReader(Node.class);
    }
}
