package cz.cuni.mff.ufal.textan.core;

import java.util.Date;

/**
 * Client representaion of {@link cz.cuni.mff.ufal.textan.commons.models.Ticket}.
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
    public Ticket(final cz.cuni.mff.ufal.textan.commons_old.models.Ticket ticket) {
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
    public cz.cuni.mff.ufal.textan.commons_old.models.Ticket toTicket() {
        return new cz.cuni.mff.ufal.textan.commons_old.models.Ticket(
                username, timestamp
        );
    }
}
