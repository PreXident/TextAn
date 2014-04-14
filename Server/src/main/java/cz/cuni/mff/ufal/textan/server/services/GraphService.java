package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.data.graph.Node;
import cz.cuni.mff.ufal.textan.data.graph.ObjectNode;
import cz.cuni.mff.ufal.textan.data.graph.RelationNode;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTableDAO;
import cz.cuni.mff.ufal.textan.server.models.Graph;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.server.models.Relation;
import cz.cuni.mff.ufal.textan.server.models.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * A service which provides construction of graphs.
 *
 * @author Petr Fanta
 */
@Service
@Transactional
public class GraphService {

    private final GraphFactory graphFactory;
    private final IObjectTableDAO objectTableDAO;
    private final IRelationTableDAO relationTableDAO;

    @Autowired
    public GraphService(GraphFactory graphFactory, IObjectTableDAO objectTableDAO, IRelationTableDAO relationTableDAO) {
        this.graphFactory = graphFactory;
        this.objectTableDAO = objectTableDAO;
        this.relationTableDAO = relationTableDAO;
    }

    public Graph getPath(long startObjectId, long targetObjectId, Ticket serverTicket) throws IdNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Graph getGraph(long objectId, int distance, Ticket serverTicket) throws IdNotFoundException {
        return getGraph(objectId, distance);
    }

    public Graph getRelatedObjects(long objectId, Ticket serverTicket) throws IdNotFoundException {
        return getGraph(objectId, 1);
    }

    private Graph getGraph(long objectId, int distance) {

        //TODO implement properly

        List<Object> nodes = new ArrayList<Object>();
        List<Relation> edges = new ArrayList<Relation>();

        cz.cuni.mff.ufal.textan.data.graph.Graph dataGraph = graphFactory.getGraphFromObject(objectId, distance);

        for (Node node : dataGraph.getNodes()) {

            if (node instanceof ObjectNode) {
                long nodeObjectId = ((ObjectNode) node).getId();
                nodes.add(Object.fromObjectTable(objectTableDAO.find(nodeObjectId)));
            }

            if (node instanceof RelationNode) {
                long nodeRelationId = ((RelationNode) node).getId();
                edges.add(Relation.fromRelationTable(relationTableDAO.find(nodeRelationId)));
            }
        }

        return new Graph(nodes, edges);
    }
}
