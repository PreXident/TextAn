/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    IObjectTableDAO objectTableDAO;
    
    SessionFactory sessionFactory;

    /**
     *
     * @param sessionFactory
     */
    @Autowired
    public GraphFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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
        return getGraphFromObject(objectId, depth, new HashSet<Node>());
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

    @SuppressWarnings({"rawtypes"})
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
                
                        + " where obj.id = :pId and obj2.id != obj.id"
                  )
                .setParameter("pId", objectId)
                .list();
        // s.close();
        
        if (res.isEmpty()) {
            return new Graph().add(new ObjectNode(objectTableDAO.find(objectId)));
        }
        
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
                    result.mergeIntoThis(nextWave);
                }
            }
        }
        return result;
    }


  
}
