package cz.cuni.mff.ufal.textan.commons.models;

import java.util.Date;

/**
 * Created by Petr Fanta on 30.1.14.
 */
public class Ticket {

    private final String username;
    private final Date timestamp;

    Ticket(String username, Date timestamp) {
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
