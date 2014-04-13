package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.server.models.Graph;
import cz.cuni.mff.ufal.textan.server.models.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service which provides construction of graphs.
 *
 * @author Petr Fanta
 */
@Service
@Transactional
public class GraphService {

    private final GraphFactory graphFactory;

    @Autowired
    public GraphService(GraphFactory graphFactory) {
        this.graphFactory = graphFactory;
    }


    public Graph getPath(long startObjectId, long targetObjectId, Ticket serverTicket) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Graph getGraph(long objectId, int distance, Ticket serverTicket) {

        cz.cuni.mff.ufal.textan.data.graph.Graph dataGraph = graphFactory.getGraphFromObject(objectId, distance);

        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Graph getRelatedObjects(long objectId, Ticket serverTicket) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
