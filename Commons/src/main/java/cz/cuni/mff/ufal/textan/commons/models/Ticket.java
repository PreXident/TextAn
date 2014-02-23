package cz.cuni.mff.ufal.textan.commons.models;

import cz.cuni.mff.ufal.textan.commons.models.adapters.TicketAdapter;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by Petr Fanta on 30.1.14.
 */
@XmlJavaTypeAdapter(TicketAdapter.class)
public class Ticket {

    private final String username;
    private final Date timestamp;

    public Ticket(String username, Date timestamp) {
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
