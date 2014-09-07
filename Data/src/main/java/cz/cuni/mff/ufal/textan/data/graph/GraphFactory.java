package cz.cuni.mff.ufal.textan.data.graph;

import com.sun.media.jfxmedia.logging.Logger;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.exceptions.PathDoesNotExistException;
import cz.cuni.mff.ufal.textan.data.graph.pathfinding.IPathNode;
import cz.cuni.mff.ufal.textan.data.graph.pathfinding.ObjectPathNode;
import cz.cuni.mff.ufal.textan.data.graph.pathfinding.RelationPathNode;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.util.ArrayDeque;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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
        //Logger.setLevel(Logger.DEBUG);

        Logger.logMsg(Logger.DEBUG, "Object1: " + obj1);
        Logger.logMsg(Logger.DEBUG, "Object2: " + obj2);
        
        
        Queue<Pair<ObjectNode, Integer>> objQueue1 = new ArrayDeque<>();
        Graph graph1 = new Graph();
        initBidirectionalSearch(obj1, objQueue1, graph1);
        
        Queue<Pair<ObjectNode, Integer>> objQueue2 = new ArrayDeque<>();
        Graph graph2 = new Graph();
        initBidirectionalSearch(obj2, objQueue2, graph2);
        boolean found = false;
        
        for (int i = 0; i <= (maxDepth+1)/2; i++) {
            found |= bidirectionalSearchIteration(objQueue1, graph1, graph2, i, (maxDepth+1)/2);
            if (found) break;
            if (i == (maxDepth+1)/2) {
                Logger.logMsg(Logger.DEBUG, "Graph1: " + graph1);
                Logger.logMsg(Logger.DEBUG, "Graph2: " + graph2);
                
                Logger.logMsg(Logger.DEBUG, "objQueue1: " + objQueue1);
                Logger.logMsg(Logger.DEBUG, "objQueue2: " + objQueue2);

                throw new PathDoesNotExistException();
            }
            found |= bidirectionalSearchIteration(objQueue2, graph2, graph1, i, maxDepth/2);
            if (found) break;
        }
        Logger.logMsg(Logger.DEBUG, "FOUND");
        
        Logger.logMsg(Logger.DEBUG, "Graph1: " + graph1);
        Logger.logMsg(Logger.DEBUG, "Graph2: " + graph2);

        Logger.logMsg(Logger.DEBUG, "objQueue1: " + objQueue1);
        Logger.logMsg(Logger.DEBUG, "objQueue2: " + objQueue2);
        
        Graph intersection1 = Graph.intersection(graph1, graph2);
        Node node1 = intersection1.nodes.iterator().next();
        
        Graph intersection2 = Graph.intersection(graph2, graph1);
        
        Node node2 = null;
        for (Node node : intersection2.nodes) {
            if (node.equals(node1)) {
                node2 = node;
            }
        }
        if (node2 == null) throw new UnknownError("Graph is fucked up");
        
        Graph result = getPath((IPathNode) node1);
        Logger.logMsg(Logger.DEBUG, "path after node1: " + result);
        Graph path2 = getPath((IPathNode) node2);
        Logger.logMsg(Logger.DEBUG, "path2: " + path2);
        result.unionIntoThis(path2);
        Logger.logMsg(Logger.DEBUG, "path after node2: " + result);
        result.nodes.add(node2);

        
        Logger.logMsg(Logger.DEBUG, "Result: " + result);
        
        return result;
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
 
    @SuppressWarnings({"rawtypes"})    
    private Graph getNeighborsWithPath(long objectId) {
        Graph result = new Graph();
               
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
            
            RelationTable relation = (RelationTable) row.get(1);
            
            ObjectPathNode objectNode = new ObjectPathNode((ObjectTable) row.get(0));
            RelationPathNode relationNode = row.get(1) == null ? null : new RelationPathNode(relation);
            
            if (relationNode != null) {
                
                int order = (int) row.get(2);
                Edge edge = new Edge(objectNode, relationNode, order);

                relationNode.setPreviousNode(objectNode);
                relationNode.setPreviousEdge(edge);
                
                result.nodes.add(relationNode);
                result.edges.add(edge);
                if (row.get(4) != null) {
                    int order2 = (int) row.get(3);
                    ObjectTable obj = (ObjectTable) row.get(4);
                    ObjectPathNode objectNode2 = new ObjectPathNode(obj);
                    Edge edge2 = new Edge(objectNode2, relationNode, order2);
                    
                    objectNode2.setPreviousNode(relationNode);
                    objectNode2.setPreviousEdge(edge2);
                    
                    result.nodes.add(objectNode2);
                    result.edges.add(edge2);
                }
            }
            result.nodes.add(objectNode);
        }
        return result;
    }
    
    /**
     * 
     * @param nodesToProcess pair of nodes and length of the path to the node
     * @param otherSide
     * @return true if there was found a path
     */
    private boolean bidirectionalSearchIteration(Queue<Pair<ObjectNode, Integer>> nodesToProcess, Graph thisSide, Graph otherSide, int curDepth, int maxDepth) {
        if (!Graph.intersection(thisSide, otherSide).nodes.isEmpty()) return true;
        if (nodesToProcess.isEmpty()) return false;
        
        
        while (nodesToProcess.peek().getSecond() <= curDepth && nodesToProcess.peek().getSecond() < maxDepth) {
            Pair<ObjectNode, Integer> pairNodeInt = nodesToProcess.poll();

            ObjectNode node = pairNodeInt.getFirst();
            Integer depth = pairNodeInt.getSecond();
            Set<Node> intersectionWithOther;
            Set<ObjectNode> newObjects;
            {
                Graph neighbors = getNeighborsWithPath(node.id);

                neighbors.subractIntoThis(thisSide);
                thisSide.unionIntoThis(neighbors);

                newObjects = neighbors.nodes.stream()
                        .filter((Node x) -> x instanceof ObjectNode)
                        .map((Node x) -> (ObjectNode)x)
                        .collect(Collectors.toSet());

                neighbors.nodes.addAll(thisSide.nodes);
                neighbors.nodes.retainAll(otherSide.nodes);
                intersectionWithOther = neighbors.nodes;
            }
            if (!intersectionWithOther.isEmpty()) {
                return true;
            }
            newObjects.stream()
                    .forEach(x -> nodesToProcess.add(new Pair<>(x, depth+1)));
            
        }
        
        return false;
        
    }
    private void initBidirectionalSearch(ObjectTable obj, Queue<Pair<ObjectNode, Integer>> queue, Graph graph) {
        ObjectPathNode node = new ObjectPathNode(obj);
        queue.add(new Pair<>(node, 0));
        graph.nodes.add(node);
    }
    
    private Graph getPath(IPathNode end) {
        Graph result = new Graph();
        while (end.getPreviousNode() != null) {
            result.nodes.add(end.getPreviousNode());
            result.edges.add(end.getPreviousEdge());
            end = (IPathNode)end.getPreviousNode();
        }
        
        return result;
    }
}
