package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.data.graph.Node;
import cz.cuni.mff.ufal.textan.data.graph.ObjectNode;
import cz.cuni.mff.ufal.textan.data.graph.RelationNode;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.server.models.Graph;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.server.models.Relation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * A service which provides a construction of graphs.
 *
 * @author Petr Fanta
 */
@Service
@Transactional
public class GraphService {

    private final GraphFactory graphFactory;
    private final IObjectTableDAO objectTableDAO;
    private final IAliasTableDAO aliasTableDAO;
    private final IRelationTableDAO relationTableDAO;

    @Autowired
    public GraphService(GraphFactory graphFactory, IObjectTableDAO objectTableDAO, IAliasTableDAO aliasTableDAO, IRelationTableDAO relationTableDAO) {
        this.graphFactory = graphFactory;
        this.objectTableDAO = objectTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.relationTableDAO = relationTableDAO;
    }

    public Graph getPath(long startObjectId, long targetObjectId) throws IdNotFoundException, NonRootObjectException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Graph getGraphFromObject(long objectId, int distance) throws IdNotFoundException, NonRootObjectException {
        return getGraphInner(objectId, distance);
    }

    public Graph getRelatedObjects(long objectId) throws IdNotFoundException, NonRootObjectException {
        return getGraphInner(objectId, 1);
    }

    public Graph getGraphFromRelation(long relationId, int distance) throws IdNotFoundException {

        List<Object> nodes = new ArrayList<>();
        List<Relation> edges = new ArrayList<>();

        cz.cuni.mff.ufal.textan.data.graph.Graph dataGraph = graphFactory.getGraphFromRelation(relationId, distance);

        if (dataGraph.getNodes().isEmpty()) {
            throw new IdNotFoundException("relationId", relationId);
        }

        //TODO: extract private method
        for (Node node : dataGraph.getNodes()) {

            if (node instanceof ObjectNode) {
                long nodeObjectId = node.getId();
                nodes.add(Object.fromObjectTable(objectTableDAO.find(nodeObjectId), aliasTableDAO.findAllAliasesOfObject(nodeObjectId)));
            }

            if (node instanceof RelationNode) {
                long nodeRelationId = node.getId();
                edges.add(Relation.fromRelationTable(relationTableDAO.find(nodeRelationId), aliasTableDAO));
            }
        }

        return new Graph(nodes, edges);
    }

    private Graph getGraphInner(long objectId, int distance) throws IdNotFoundException, NonRootObjectException {
        List<Object> nodes = new ArrayList<>();
        List<Relation> edges = new ArrayList<>();

        ObjectTable objectTable = objectTableDAO.find(objectId);
        if (objectTable == null) {
            throw new IdNotFoundException("objectId", objectId);
        } else if (!objectTable.isRoot()) {
            throw new NonRootObjectException(objectId, objectTable.getRootObject().getId());
        }

        cz.cuni.mff.ufal.textan.data.graph.Graph dataGraph = graphFactory.getGraphFromObject(objectId, distance);

        for (Node node : dataGraph.getNodes()) {

            if (node instanceof ObjectNode) {
                long nodeObjectId = node.getId();
                nodes.add(Object.fromObjectTable(objectTableDAO.find(nodeObjectId), aliasTableDAO.findAllAliasesOfObject(nodeObjectId)));
            }

            if (node instanceof RelationNode) {
                long nodeRelationId = node.getId();
                edges.add(Relation.fromRelationTable(relationTableDAO.find(nodeRelationId), aliasTableDAO));
            }
        }

        return new Graph(nodes, edges);
    }
}
