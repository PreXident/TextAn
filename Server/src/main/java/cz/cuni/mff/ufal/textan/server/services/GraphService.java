package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.server.models.Graph;
import cz.cuni.mff.ufal.textan.server.models.Ticket;
import org.springframework.stereotype.Service;

/**
 * A service which provides construction of graphs.
 *
 * @author Petr Fanta
 */
@Service
public class GraphService {

    public Graph getPath(long startObjectId, long targetObjectId, Ticket serverTicket) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Graph getGraph(long objectId, int distance, Ticket serverTicket) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Graph getRelatedObjects(long objectId, Ticket serverTicket) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
