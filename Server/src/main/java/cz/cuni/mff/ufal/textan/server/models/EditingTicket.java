package cz.cuni.mff.ufal.textan.server.models;

import java.util.Date;

/**
 * @author Petr Fanta
 */
public class EditingTicket extends Ticket {

    private final Date timestamp;

    public EditingTicket(String username) {
        super(username);
        timestamp = new Date();
    }

    public EditingTicket(String username, Date timestamp) {
        super(username);
        this.timestamp = timestamp;
    }

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

    public static EditingTicket fromCommonsEditingTicket(cz.cuni.mff.ufal.textan.commons.models.EditingTicket editingTicket) {
        //TODO:implement
        return null;
    }
}
