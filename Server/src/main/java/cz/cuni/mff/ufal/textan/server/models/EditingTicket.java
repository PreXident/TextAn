package cz.cuni.mff.ufal.textan.server.models;

import java.util.Date;

/**
 * A service layer representation of the Ticket.
 * Ticket holds information about user and a start of editing.
 *
 * @author Petr Fanta
 * @see cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable
 * @see cz.cuni.mff.ufal.textan.data.repositories.dao.GlobalVersionTableDAO
 */
public class EditingTicket{

    private final long version;
    private final Date timestamp;

    /**
     * Instantiates a new Editing ticket.
     *
     * @param version global version
     */
    public EditingTicket(long version) {
        this.version = version;
        timestamp = new Date();
    }

    /**
     * Instantiates a new Editing ticket.
     * @param version the database global version
     * @param timestamp the timestamp   */
    public EditingTicket(long version, Date timestamp) {
        this.timestamp = timestamp;
        this.version = version;
    }

    /**
     * Converts a {@link  cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket} to {@link cz.cuni.mff.ufal.textan.server.models.EditingTicket}
     *
     * @param commonsEditingTicket the commons editing ticket
     * @return the editing ticket
     */
    public static EditingTicket fromCommonsEditingTicket(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket commonsEditingTicket) {
        return new EditingTicket(commonsEditingTicket.getVersion() ,commonsEditingTicket.getTimestamp());
    }

    /**
     * Converts the instance to {@link cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket}
     * @return the {@link cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket}
     */
    public cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket toCommonsEditingTicket() {
        cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket commonsTicket = new cz.cuni.mff.ufal.textan.commons.models.documentprocessor.EditingTicket();
        commonsTicket.setVersion(version);
        commonsTicket.setTimestamp(timestamp);
        return commonsTicket;
    }

    /**
     * Gets the global version.
     * @return the global version
     */
    public long getVersion() {
        return version;
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

        EditingTicket that = (EditingTicket) o;

        if (version != that.version) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (version ^ (version >>> 32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EditingTicket{");
        sb.append("version=").append(version);
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');
        return sb.toString();
    }
}
