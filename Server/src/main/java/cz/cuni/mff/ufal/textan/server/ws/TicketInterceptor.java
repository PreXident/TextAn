package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.EditingTicket;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
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
 * A CXF interceptor which extracts Tickets headers from SOAP message.
 * @author Petr Fanta
 */
public class TicketInterceptor extends AbstractSoapInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TicketInterceptor.class);

    //TODO: remove timestamp from ticket?
    private static final QName TICKET_HEADER = new QName("http://models.commons.textan.ufal.mff.cuni.cz", "ticket");
    private static final QName EDITING_TICKET_HEADER = new QName("http://models.commons.textan.ufal.mff.cuni.cz", "editingTicket");

    /**
     * Instantiates a new Ticket interceptor.
     */
    public TicketInterceptor() {

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

        String username = null;

        Header header;
        if ((header = message.getHeader(TICKET_HEADER)) != null) {
            Ticket ticket = getTicket(message, header);
            LOG.debug("Ticket found: {}", ticket);

            username = ticket.getUsername();

        } else if ((header = message.getHeader(EDITING_TICKET_HEADER)) != null) {
            EditingTicket editingTicket = getEditingTicket(message, header);
            LOG.debug("Editing ticket found: {}", editingTicket);

            username = editingTicket.getUsername();

        } else {
            //TODO: better exception
            Fault exception = new Fault(new Exception("Did not find any Ticket header"));
            exception.setFaultCode(Fault.FAULT_CODE_SERVER);

            LOG.warn("Did not find any header with Ticket!", exception);
            throw exception;
        }


        if ((username == null) || (username.isEmpty())) {
            Fault exception = new Fault(new Exception("User name can not be empty!"));
            exception.setFaultCode(Fault.FAULT_CODE_SERVER);

            LOG.warn("User name can not be empty!", exception);
            throw exception;
        }

        LOG.debug("Username: {}", username);
    }

    private Ticket getTicket(SoapMessage message, Header header) {
        DataReader<Node> dataReader = getNodeDataReader(message);
        return (Ticket) dataReader.read(TICKET_HEADER, (Node) header.getObject(), Ticket.class);
    }

    private EditingTicket getEditingTicket(SoapMessage message, Header header) {
        DataReader<Node> dataReader = getNodeDataReader(message);
        return (EditingTicket) dataReader.read(EDITING_TICKET_HEADER, (Node) header.getObject(), EditingTicket.class);
    }

    private DataReader<Node> getNodeDataReader(SoapMessage message) {
        Service service = ServiceModelUtil.getService(message.getExchange());
        return service.getDataBinding().createReader(Node.class);
    }
}
