package cz.cuni.mff.ufal.textan.commons.models.adapters;

import cz.cuni.mff.ufal.textan.commons.models.Graph;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapting {@link Graph}.
 */
public class GraphAdapter extends XmlAdapter<AdaptedGraph, Graph> {

    @Override
    public Graph unmarshal(AdaptedGraph adaptedGraph) throws Exception {
        return new Graph(adaptedGraph.getNodes(), adaptedGraph.getEdges());
    }

    @Override
    public AdaptedGraph marshal(Graph graph) throws Exception {
        final AdaptedGraph ag = new AdaptedGraph();
        ag.setEdges(graph.getEdges());
        ag.setNodes(graph.getNodes());
        return ag;
    }

}
