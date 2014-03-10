package cz.cuni.mff.ufal.textan.gui.graph;

import PretopoVisual.Jung.BasicHypergraphRenderer;
import PretopoVisual.Jung.PseudoHypergraph;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.utils.Pair;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SetHypergraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.embed.swing.SwingNode;
import javax.swing.SwingUtilities;
import org.apache.commons.collections15.Transformer;

/**
 * Displays graphs.
 * TODO PretopoLib licensing!
 */
public class GraphView extends SwingNode {

    /**
     * Properties containing application settings.
     * Handle with care, they are shared!
     */
    final Properties settings;

    final VisualizationViewer<Object, Relation> visualizator;

    public GraphView(final Properties settings,
            final Map<Integer, Object> objects, final Set<Relation> relations) {
        this.settings = settings;
        final boolean hypergraphs = settings.getProperty(TextAnController.HYPER_GRAPHS, "false").equals("true");
        final Hypergraph<Object, Relation> g = hypergraphs ? new SetHypergraph<>() : new SparseMultigraph<>();
        // Add vertices
        for (Object obj : objects.values()) {
            g.addVertex(obj);
        }
        // Add edges
        if (hypergraphs) {
            for (Relation rel : relations) {
                final Stream<Pair<Object, Integer>> relatedIDs = rel.getObjects().stream();
                final List<Object> related = relatedIDs.map(pair -> pair.getFirst()).collect(Collectors.toList());
                g.addEdge(rel, related);
            }
        } else {
            for (Relation rel : relations) {
                if (rel.getObjects().size() > 2) {
                    final Object dummy = new Object(-1, new ObjectType(-1, rel.getType().getName()), Arrays.asList(rel.toString()));
                    g.addVertex(dummy);
                    for (Pair<Object, Integer> pair : rel.getObjects()) {
                        final Relation dummyRel = new DummyRelation(rel.getType(), pair);
                        final int order = pair.getSecond();
                        final Object obj = pair.getFirst();
                        if (order < 0) {
                            g.addEdge(dummyRel, Arrays.asList(obj, dummy), EdgeType.DIRECTED);
                        } else if (order % 2 == 0) {
                            g.addEdge(dummyRel, Arrays.asList(dummy, obj), EdgeType.DIRECTED);
                        } else {
                            g.addEdge(dummyRel, Arrays.asList(obj, dummy), EdgeType.UNDIRECTED);
                        }
                    }
                } else if (rel.getObjects().size() == 2) {
                    Iterator<Pair<Object, Integer>> it = rel.getObjects().iterator();
                    final Pair<Object, Integer> first = it.next();
                    final Pair<Object, Integer> second = it.next();
                    if (first.getSecond() < 0 && second.getSecond() >= 0) {
                        g.addEdge(rel, Arrays.asList(first.getFirst(), second.getFirst()), EdgeType.DIRECTED);
                    } else if (second.getSecond() < 0 && first.getSecond() >= 0) {
                        g.addEdge(rel, Arrays.asList(second.getFirst(), first.getFirst()), EdgeType.DIRECTED);
                    } else {
                        g.addEdge(rel, Arrays.asList(first.getFirst(), second.getFirst()), EdgeType.UNDIRECTED);
                    }
                } else {
                    final List<Object> related = rel.getObjects().stream()
                            .map(pair -> pair.getFirst())
                            .collect(Collectors.toList());
                    g.addEdge(rel, related);
                }
            }
        }
        final Layout<Object, Relation> layout = new FRLayout<>(
                hypergraphs ? new PseudoHypergraph<>(g) : (Graph<Object, Relation>) g
        );
        //
        Transformer<Object, Paint> vertexPaint = (Object obj) -> Color.GREEN;
        float dash[] = {10.0f};
        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        Transformer<Relation, Stroke> edgeStrokeTransformer = (Relation s) -> edgeStroke;
        //
        visualizator = new VisualizationViewer<>(layout);
        if (hypergraphs) {
            final BasicHypergraphRenderer<Object, Relation> b = new BasicHypergraphRenderer<>();
            visualizator.setRenderer(b);
        }
        //
        visualizator.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        visualizator.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        visualizator.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
        visualizator.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());
        visualizator.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        // Create a graph mouse and add it to the visualization component
        DefaultModalGraphMouse<Integer,String> gm = new DefaultModalGraphMouse<>();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        visualizator.setGraphMouse(gm);
        //
        try {
            SwingUtilities.invokeAndWait(() -> {
                this.setContent(visualizator);
            });
        } catch (Exception e) { }
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        SwingUtilities.invokeLater(() -> {
            visualizator.setSize((int) width, (int) height);
        });
    }
}
