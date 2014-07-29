package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.UsernameToken;
import cz.cuni.mff.ufal.textan.data.interceptors.LogInterceptor;
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
    private final LogInterceptor logInterceptor;

    /**
     * Instantiates a new Ticket interceptor.
     * @param logInterceptor
     */
    public UsernameTokenInterceptor(LogInterceptor logInterceptor) {
        super(Phase.PRE_INVOKE);
        getBefore().add(SoapHeaderInterceptor.class.getName());

        this.logInterceptor = logInterceptor;
    }

    /**
     * Searches a UsernameToken in SOAP message and sets username.
     *
     * @param message SOAP message received from a client
     * @throws Fault exception thrown when user token header not found or when user token header is invalid
     */
    @Override
    public void handleMessage(SoapMessage message) throws Fault {

        Header header;
        if ((header = message.getHeader(USERNAME_TOKEN_HEADER)) != null) {
            DataReader<Node> reader = getNodeDataReader(message);
            UsernameToken token = getUsernameToken(header, reader);
            LOG.debug("Username token: {}", token);

            String username = token.getUsername();

            if ((username != null) && (!username.isEmpty())) {
                logInterceptor.setUsername(username);
            } else {
                Fault exception = new Fault(new Exception("User name can not be empty!"));
                exception.setFaultCode(Fault.FAULT_CODE_SERVER);

                LOG.warn("User name can not be empty!", exception);
                throw exception;
            }

            LOG.debug("Username: {}", username);

        } else {
            Fault exception = new Fault(new Exception("Did not find UsernameToken header"));
            exception.setFaultCode(Fault.FAULT_CODE_SERVER);

            LOG.warn("Did not find any header with UsernameToken!", exception);
            throw exception;
        }
    }

    /**
     * Extract an instance of an {@link cz.cuni.mff.ufal.textan.commons.models.UsernameToken} from a header
     * @param header the header which contains a serialized UsernameToken
     * @param reader the DataReader from message with the header
     * @return the instance of {@link cz.cuni.mff.ufal.textan.commons.models.UsernameToken}
     */
    private UsernameToken getUsernameToken(Header header, DataReader<Node> reader ) {
        return (UsernameToken) reader.read(USERNAME_TOKEN_HEADER, (Node) header.getObject(), UsernameToken.class);
    }

    /**
     * Gets DataReader from message.
     * @param message a SOAP message
     * @return the DataReader
     */
    private DataReader<Node> getNodeDataReader(SoapMessage message) {
        Service service = ServiceModelUtil.getService(message.getExchange());
        return service.getDataBinding().createReader(Node.class);
    }
}
