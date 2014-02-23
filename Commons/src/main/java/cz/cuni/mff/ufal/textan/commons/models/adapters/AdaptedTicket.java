package cz.cuni.mff.ufal.textan.commons.models.adapters;

import cz.cuni.mff.ufal.textan.commons.models.RelationType;
import java.util.Date;

/**
 * Adapting {@link RelationType}.
 */
public class AdaptedTicket {

    private String username;
    private Date timestamp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
