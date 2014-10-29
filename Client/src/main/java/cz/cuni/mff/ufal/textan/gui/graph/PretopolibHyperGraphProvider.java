package cz.cuni.mff.ufal.textan.gui.graph;

import PretopoVisual.Jung.BasicHypergraphRenderer;
import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SetHypergraph;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * IHyperGraphProvider using Pretopolib.
 */
public class PretopolibHyperGraphProvider implements IHyperGraphProvider {

    @Override
    public String getId() {
        return "PretopolibHyperGraphProvider";
    }

    @Override
    public String getDescKey() {
        return "hypergraph.pretopolib";
    }

    @Override
    public String getDesc() {
        return "Clouds";
    }

    @Override
    public Hypergraph<Object, Relation> createHyperGraph() {
        return new SetHypergraph<>();
    }

    @Override
    public void addObjects(Hypergraph<Object, Relation> graph, Collection<Object> objects) {
        for (Object obj : objects) {
            graph.addVertex(obj);
        }
    }

    @Override
    public void addRelations(Hypergraph<Object, Relation> graph, Collection<Relation> relations) {
        for (Relation rel : relations) {
            final Stream<Triple<Integer, String, Object>> relatedIDs = rel.getObjects().stream();
            final List<Object> related = relatedIDs.map(triple -> triple.getThird()).collect(Collectors.toList());
            graph.addEdge(rel, related);
        }
    }

    @Override
    public Graph<Object, Relation> transformForLayout(Hypergraph<Object, Relation> graph) {
        return new PseudoHypergraph<>(graph);
    }

    @Override
    public Renderer<Object, Relation> createRenderer() {
        return new BasicHypergraphRenderer<>();
    }
}
