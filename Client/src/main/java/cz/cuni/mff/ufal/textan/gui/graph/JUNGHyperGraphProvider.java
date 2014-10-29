package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Pure JUNG implementation of IHypergraphProvider.
 */
public class JUNGHyperGraphProvider implements IHyperGraphProvider {

    @Override
    public String getId() {
        return "JUNGHyperGraphProvider";
    }

    @Override
    public String getDescKey() {
        return "hypergraph.jung";
    }

    @Override
    public String getDesc() {
        return "Additional vertex";
    }

    @Override
    public Hypergraph<Object, Relation> createHyperGraph() {
        return new SparseMultigraph<>();
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
            if (rel.getObjects().size() > 2) {
                final Object dummy = new RelationObject(rel);
                graph.addVertex(dummy);
                for (Triple<Integer, String, Object> triple : rel.getObjects()) {
                    final Relation dummyRel = new DummyRelation(rel.getType(), triple);
                    final int order = triple.getFirst();
                    final Object obj = triple.getThird();
                    if (order < 0) {
                        graph.addEdge(dummyRel, Arrays.asList(obj, dummy), EdgeType.DIRECTED);
                    } else if (order % 2 == 1) {
                        graph.addEdge(dummyRel, Arrays.asList(dummy, obj), EdgeType.DIRECTED);
                    } else {
                        graph.addEdge(dummyRel, Arrays.asList(obj, dummy), EdgeType.UNDIRECTED);
                    }
                }
            } else if (rel.getObjects().size() == 2) {
                Iterator<Triple<Integer, String, Object>> it = rel.getObjects().iterator();
                final Triple<Integer, String, Object> first = it.next();
                final Triple<Integer, String, Object> second = it.next();
                if (first.getFirst() < 0 && second.getFirst() >= 0) {
                    graph.addEdge(rel, Arrays.asList(first.getThird(), second.getThird()), EdgeType.DIRECTED);
                } else if (second.getFirst() < 0 && first.getFirst() >= 0) {
                    graph.addEdge(rel, Arrays.asList(second.getThird(), first.getThird()), EdgeType.DIRECTED);
                } else {
                    graph.addEdge(rel, Arrays.asList(first.getThird(), second.getThird()), EdgeType.UNDIRECTED);
                }
            } else {
                final List<Object> related = rel.getObjects().stream()
                        .map(triple -> triple.getThird())
                        .collect(Collectors.toList());
                graph.addEdge(rel, related);
            }
        }
    }

    @Override
    public Graph<Object, Relation> transformForLayout(Hypergraph<Object, Relation> graph) {
        return (Graph<Object, Relation>) graph;
    }

    @Override
    public Renderer<Object, Relation> createRenderer() {
        return null;
    }
}
