package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket;
import java.io.Serializable;
import java.util.Date;

/**
 * Client representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket}.
 */
public class Ticket implements Serializable {

    /** Ticket timestamp. */
    private final Date timestamp;

    /**
     * Only constructor.
     * @param ticket ticket blue print
     */
    public Ticket(final EditingTicket ticket) {
        timestamp = ticket.getTimestamp();
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
    public cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket toTicket() {
        final cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket result =
                new cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket();
        result.setTimestamp(timestamp);
        return result;
    }
}
