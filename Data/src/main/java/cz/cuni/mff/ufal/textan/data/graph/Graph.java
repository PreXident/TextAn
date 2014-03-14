/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.hibernate.Session;

/**
 *
 * @author Václav Pernička
 */
public class Graph {
    Set<Node> nodes = new HashSet<>();
    Set<Edge> edges = new HashSet<>();

    /**
     * Creates a graph with an object in the "center"
     * You can specify how deep it will search more objects
     * <b>Now it works only for depth = 1 (this value is forced)</b>
     * 
     * @param objectId Id of an object in the center of the graph
     * @param depth How deep should it search. 
     *              0 = returns only one node (with the given ID)
     *              1 = returns neighbors of this node (nodes in a relationship)
     * @return 
     */
    @Deprecated
    public static Graph getGraphFromObject(long objectId, int depth) {
        Graph result = new Graph();
        
        // TODO node to be done in the next wave
        Queue<Node> nodeQueue = new LinkedList<>();
        
        Session s = null; //TODO:repair //HibernateUtil.getSessionFactory().openSession();
        List res = s.createQuery(
                "select new list(obj, rel, inRel.order, inRel2.order, obj2)"
                        + " from ObjectTable obj"
                        + "     left join obj.relations inRel"
                        + "     left join inRel.relation rel"
                        + "     left join rel.objectsInRelation inRel2"
                        + "     left join inRel2.object obj2"
                        + " where obj.id = :pId and obj2.id != obj.id").setParameter("pId", objectId).list();
        s.close();
        for (int i = 0; i < res.size(); i++) {
            List row = (List)res.get(i);
            ObjectNode objectNode = new ObjectNode((ObjectTable)row.get(0));
            RelationNode relationNode = row.get(1)==null ? null : new RelationNode((RelationTable)row.get(1));
            if (relationNode != null) {
                int order = (int)row.get(2);
                Edge edge = new Edge(objectNode, relationNode, order);
                result.nodes.add(relationNode);
                result.edges.add(edge);
                if (row.get(4) != null) {
                    int order2 = (int)row.get(3);
                    ObjectNode objectNode2 = new ObjectNode((ObjectTable)row.get(4));
                    if (result.nodes.add(objectNode2))
                        nodeQueue.add(objectNode2);
                    result.edges.add(new Edge(objectNode2, relationNode, order2));
                }
            }
            result.nodes.add(objectNode);
        }
        
        return result;
    }
    
    private Graph() {
    }

    private Graph(Collection<? extends Node> nodes, Collection<? extends Edge> edges) {
        this.nodes.addAll(nodes);
        this.edges.addAll(edges);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return "Graph{" + "nodes=" + nodes + ", edges=" + edges + '}';
    }
    
    
}
