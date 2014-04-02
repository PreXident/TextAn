package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.EditingTicket;
import java.util.Date;

/**
 * Client representation of {@link cz.cuni.mff.ufal.textan.commons.models.EditingTicket}.
 */
public class Ticket {

    /** User login. */
    private final String username;

    /** Ticket timestamp. */
    private final Date timestamp;

    /**
     * Only constructor.
     * @param ticket ticket blue print
     */
    public Ticket(final EditingTicket ticket) {
        username = ticket.getUsername();
        timestamp = ticket.getTimestamp();
    }

    /**
     * Returns user name.
     * @return user name
     */
    public String getUsername() {
        return username;
    }

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
    public cz.cuni.mff.ufal.textan.commons.models.EditingTicket toTicket() {
        final cz.cuni.mff.ufal.textan.commons.models.EditingTicket result =
                new cz.cuni.mff.ufal.textan.commons.models.EditingTicket();
        result.setUsername(username);
        result.setTimestamp(timestamp);
        return result;
    }
}
