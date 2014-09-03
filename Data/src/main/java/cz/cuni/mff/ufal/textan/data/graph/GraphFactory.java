package cz.cuni.mff.ufal.textan.data.graph;

import cz.cuni.mff.ufal.textan.data.exceptions.PathDoesNotExistException;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Vaclav Pernicka
 */
@Transactional
public class GraphFactory {

    final IObjectTableDAO objectTableDAO;
    final SessionFactory sessionFactory;

    /**
     * Only constructor.
     * @param objectTableDAO object table
     * @param sessionFactory session factory
     */
    public GraphFactory(SessionFactory sessionFactory, IObjectTableDAO objectTableDAO) {
        this.objectTableDAO = objectTableDAO;
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns shortest path between objects or throws an exception if there
     * does not exist any path.
     *
     * @param obj1 first object
     * @param obj2 second object
     * @param maxDepth maximal depth to search
     * @return graph containg the path
     * @throws PathDoesNotExistException if the path does not exist
     */
    public Graph getShortestPathBetweenObjects(ObjectTable obj1, ObjectTable obj2, int maxDepth) throws PathDoesNotExistException {
        Graph result1 = getGraphFromObject(obj1, maxDepth/2);
        Graph result2 = getGraphFromObject(obj2, maxDepth/2 + maxDepth%2);

        Graph intersection = Graph.intersection(result1, result2);

        if (!intersection.getNodes().isEmpty())
            throw new PathDoesNotExistException();

        result1.unionIntoThis(result2);

        return result1;
    }

    /**
     * Creates a graph with an object in the "center"
     * You can specify how deep it will search more objects
     *
     * @param objectId Id of an object in the center of the graph
     * @param depth How deep should it search.
     *              0 = returns only one object
     *              1 = returns neighbors of this node (nodes in a relationship)
     *              2 = same as 1 but also neighbors of neighbors are added
     *              3 = 2 + their neighbors
     * @return desired graph
     */
    public Graph getGraphFromObject(long objectId, int depth) {
        return getGraphFromObject(objectId, depth, new HashSet<>());
    }

    /**
     * Creates a graph with an object in the "center"
     * You can specify how deep it will search more objects
     *
     * @param obj object in the center of the graph
     * @param depth How deep should it search.
     *              0 = returns only one object
     *              1 = returns neighbors of this node (nodes in a relationship)
     *              2 = same as 1 but also neighbors of neighbors are added
     *              3 = 2 + their neighbors
     * @return desired graph
     */
    public Graph getGraphFromObject(ObjectTable obj, int depth) {
        return getGraphFromObject(obj.getId(), depth);
    }

    /**
     * Creates a graph with a relation in the "center".
     * You can specify how deep it will search more objects.
     *
     * @param relation center relation of the graph
     * @param depth how deep it will search
     * @return desired graph
     */
    public Graph getGraphFromRelation(RelationTable relation, int depth) {
        return getGraphFromRelation(relation.getId(), depth);
    }

    /**
     * Creates a graph with a relation in the "center".
     * You can specify how deep it will search more objects.
     *
     * @param relationId id of a center relation of the graph
     * @param depth how deep it will search
     * @return desired graph
     */
    public Graph getGraphFromRelation(long relationId, int depth) {
        Session s = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        List<Node> res = s.createQuery(
                "select new cz.cuni.mff.ufal.textan.data.graph.ObjectNode(obj.id, obj.data) "
                        + " from ObjectTable obj"
                        + "     left join obj.relations inRel"
                        + "     left join inRel.relation rel"
                        + " where rel.id = :pId "
                  )
                .setParameter("pId", relationId)
                .list();
        Set<Node> passedNodes = new HashSet<>(res);
        Graph result = new Graph();
        for (Node node : res) {
            result.unionIntoThis(getGraphFromObject(node.getId(), depth, passedNodes));
        }
        return result;
    }

    @SuppressWarnings({"rawtypes"})
    @Transactional(readOnly = true)
    private Graph getGraphFromObject(long objectId, int depth, Set<Node> passedNodes) {
        if (depth <= 0) {
            return new Graph().add(new ObjectNode(objectTableDAO.find(objectId)));
        }


        Graph result = new Graph();

        // TODO node to be done in the next wave
        // TODO get results in one query?
        Set<Node> thisWave = new HashSet<>();

        Session s = sessionFactory.getCurrentSession();
        List res = s.createQuery(
                "select new list(obj, rel, inRel.order, inRel2.order, obj2)"
                        + " from ObjectTable obj"
                        + "     left join obj.relations inRel"
                        + "     left join inRel.relation rel"
                        + "     left join rel.objectsInRelation inRel2"
                        + "     left join inRel2.object obj2"

                        + " where obj.id = :pId"
                  )
                .setParameter("pId", objectId)
                .list();
        // s.close();

        if (res.isEmpty()) {
            return new Graph().add(new ObjectNode(objectTableDAO.find(objectId)));
        }

        for (Object re : res) {
            List row = (List) re;
            ObjectNode objectNode = new ObjectNode((ObjectTable) row.get(0));
            RelationNode relationNode = row.get(1) == null ? null : new RelationNode((RelationTable) row.get(1));
            if (relationNode != null) {
                int order = (int) row.get(2);
                Edge edge = new Edge(objectNode, relationNode, order);
                result.nodes.add(relationNode);
                result.edges.add(edge);
                if (row.get(4) != null) {
                    int order2 = (int) row.get(3);
                    ObjectNode objectNode2 = new ObjectNode((ObjectTable) row.get(4));
                    if (result.nodes.add(objectNode2))
                        thisWave.add(objectNode2);
                    result.edges.add(new Edge(objectNode2, relationNode, order2));
                }
            }
            result.nodes.add(objectNode);
            thisWave.add(objectNode);
        }

        if (depth > 1) {
            for (Node node : thisWave) {
                if (!passedNodes.contains(node)) {
                    passedNodes.add(node);
                    Graph nextWave = getGraphFromObject(node.getId(), depth - 1, passedNodes);
                    result.unionIntoThis(nextWave);
                }
            }
        }
        return result;
    }
}
