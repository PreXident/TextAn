package cz.cuni.mff.ufal.textan.server.models;

/**
 * A service layer representation of the Ticket.
 * Ticket holds information about user.
 *
 * @author Petr Fanta
 */
public class Ticket {

    private final String username;

    /**
     * Instantiates a new Ticket.
     *
     * @param username the username
     */
    public Ticket(String username) {
        this.username = username;
    }

    /**
     * Creates a {@link cz.cuni.mff.ufal.textan.server.models.Ticket} from a {@link cz.cuni.mff.ufal.textan.commons.models.Ticket}
     *
     * @param commonsTicket the commons ticket
     * @return the service layer ticket
     */
    public static Ticket fromCommonsTicket(cz.cuni.mff.ufal.textan.commons.models.Ticket commonsTicket) {
        return new Ticket(commonsTicket.getUsername());
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        if (username != null ? !username.equals(ticket.username) : ticket.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Ticket{username='" + username + "'}";
    }
}
