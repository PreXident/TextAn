package cz.cuni.mff.ufal.textan.server.models;

import java.util.Date;

/**
 * A service layer representation of the Ticket.
 * Ticket holds information about user and a start of editing.
 *
 * @author Petr Fanta
 */
public class EditingTicket{

    private final Date timestamp;

    /**
     * Instantiates a new Editing ticket.
     *
     */
    public EditingTicket() {
        timestamp = new Date();
    }

    /**
     * Instantiates a new Editing ticket.
     *
     * @param timestamp the timestamp
     */
    public EditingTicket(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Converts a {@link cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket} to {@link cz.cuni.mff.ufal.textan.server.models.Entity}
     *
     * @param commonsEditingTicket the commons editing ticket
     * @return the editing ticket
     */
    public static EditingTicket fromCommonsEditingTicket(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket commonsEditingTicket) {
        return new EditingTicket(commonsEditingTicket.getTimestamp());
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EditingTicket that = (EditingTicket) o;

        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EditingTicket{");
        sb.append("timestamp=").append(timestamp).append(", ");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
