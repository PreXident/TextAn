package cz.cuni.mff.ufal.textan.server.models;

import java.util.List;

/**
 * A service layer representation of the Graph
 * @author Petr Fanta
 */
public class Graph {

    //TODO: imlement

    private final List<Object> nodes;
    private final List<Relation> edges;

    public Graph(List<Object> nodes, List<Relation> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<Object> getNodes() {
        return nodes;
    }

    public List<Relation> getEdges() {
        return edges;
    }

    public cz.cuni.mff.ufal.textan.commons.models.Graph toCommonsGraph() {
        cz.cuni.mff.ufal.textan.commons.models.Graph commonsGraph = new cz.cuni.mff.ufal.textan.commons.models.Graph();

        cz.cuni.mff.ufal.textan.commons.models.Graph.Nodes commonsNodes = new cz.cuni.mff.ufal.textan.commons.models.Graph.Nodes();
        for (Object node : nodes) {
            commonsNodes.getObjects().add(node.toCommonsObject());
        }
        commonsGraph.setNodes(commonsNodes);

        cz.cuni.mff.ufal.textan.commons.models.Graph.Edges commonsEdges = new cz.cuni.mff.ufal.textan.commons.models.Graph.Edges();
        for (Relation edge : edges) {
            commonsEdges.getRelations().add(edge.toCommonsRelation());
        }
        commonsGraph.setEdges(commonsEdges);

        return commonsGraph;
    }
}
