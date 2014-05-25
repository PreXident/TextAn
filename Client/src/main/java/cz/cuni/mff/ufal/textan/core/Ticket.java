package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket;
import java.util.Date;

/**
 * Client representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket}.
 */
public class Ticket {

    /** User login. */
    //private final String username; fixme

    /** Ticket timestamp. */
    private final Date timestamp;

    /**
     * Only constructor.
     * @param ticket ticket blue print
     */
    public Ticket(final EditingTicket ticket) {
        //username = null; //ticket.getUsername(); fixme
        timestamp = ticket.getTimestamp();
    }

    /**
     * Returns user name.
     * @return user name
     */
//    public String getUsername() {
//        return username;
//    }

    /**
     * Returns time stamp.
     * @return time stamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Creates new commons Ticket.
     * @return new commons Ticket
     */
    public cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket toTicket() {
        final cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket result =
                new cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket();
        //result.setUsername(username); fixme
        result.setTimestamp(timestamp);
        return result;
    }
}
