package cz.cuni.mff.ufal.textan.commons.models.adapters;

import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapts {@link Ticket}
 */
public class TicketAdapter extends XmlAdapter<AdaptedTicket, Ticket> {

    @Override
    public Ticket unmarshal(AdaptedTicket adaptedTicket) throws Exception {
        return new Ticket(adaptedTicket.getUsername(), adaptedTicket.getTimestamp());
    }

    @Override
    public AdaptedTicket marshal(Ticket ticket) throws Exception {
        final AdaptedTicket at = new AdaptedTicket();
        at.setUsername(ticket.getUsername());
        at.setTimestamp(ticket.getTimestamp());
        return at;
    }
}
