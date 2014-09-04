package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.exceptions.PathDoesNotExistException;
import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.data.graph.Node;
import cz.cuni.mff.ufal.textan.data.graph.ObjectNode;
import cz.cuni.mff.ufal.textan.data.graph.RelationNode;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
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

    public Graph getPath(long startObjectId, long targetObjectId, int maxLength) throws IdNotFoundException, NonRootObjectException {
        ObjectTable startObject = getTestedObjectTable(startObjectId, "startObjectId");
        ObjectTable targetObject = getTestedObjectTable(targetObjectId, "targetObjectId");

        try {
            return transformGraph(graphFactory.getShortestPathBetweenObjects(startObject, targetObject, maxLength));
        } catch (PathDoesNotExistException e) {
            return new Graph();
        }
    }

    public Graph getGraphFromObject(long objectId, int distance) throws IdNotFoundException, NonRootObjectException {
        return getGraphInner(objectId, distance);
    }

    public Graph getRelatedObjects(long objectId) throws IdNotFoundException, NonRootObjectException {
        return getGraphInner(objectId, 1);
    }

    public Graph getGraphFromRelation(long relationId, int distance) throws IdNotFoundException {
        RelationTable relationTable = relationTableDAO.find(relationId);
        if (relationTable == null) {
            throw new IdNotFoundException("relationId", relationId);
        }

        return transformGraph(graphFactory.getGraphFromRelation(relationId, distance));
    }

    private Graph getGraphInner(long objectId, int distance) throws IdNotFoundException, NonRootObjectException {
        getTestedObjectTable(objectId, "objectId");
        return transformGraph(graphFactory.getGraphFromObject(objectId, distance));
    }

    private ObjectTable getTestedObjectTable(long objectId, String fieldName) throws IdNotFoundException, NonRootObjectException {
        ObjectTable objectTable = objectTableDAO.find(objectId);
        if (objectTable == null) {
            throw new IdNotFoundException(fieldName, objectId);
        } else if (!objectTable.isRoot()) {
            throw new NonRootObjectException(objectId, objectTable.getRootObject().getId());
        }

        return objectTable;
    }

    private Graph transformGraph(cz.cuni.mff.ufal.textan.data.graph.Graph dataGraph) {
        List<Object> nodes = new ArrayList<>();
        List<Relation> edges = new ArrayList<>();

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
