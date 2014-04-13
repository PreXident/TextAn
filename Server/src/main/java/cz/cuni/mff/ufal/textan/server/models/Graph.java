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

        for (Object node : nodes) {
            commonsGraph.getNodes().getObjects().add(node.toCommonsObject());
        }

        for (Relation edge : edges) {
            commonsGraph.getEdges().getRelations().add(edge.toCommonsRelation());
        }

        return commonsGraph;
    }
}
