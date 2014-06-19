package cz.cuni.mff.ufal.textan.gui.graph;

import PretopoVisual.Jung.BasicHypergraphRenderer;
import PretopoVisual.Jung.PseudoHypergraph;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SetHypergraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.ContextMenu;
import javax.swing.SwingUtilities;
import org.apache.commons.collections15.Transformer;

/**
 * Displays graphs.
 * TODO PretopoLib licensing!
 */
public class GraphView extends SwingNode {

    /** Color for vertices representing relations. */
    final Color RELATION_OBJECT_COLOR = new Color(238, 238, 238);

    /**
     * Properties containing application settings.
     * Handle with care, they are shared!
     */
    final Properties settings;

    /** Graph visualizator. */
    final VisualizationViewer<Object, Relation> visualizator;

    /** Object context menu. */
    ContextMenu objectContextMenu;

    /** Object to display graph for. */
    Object objectForGraph;

    /** Mouse handler. */
    final DefaultModalGraphMouse<Integer,String> graphMouse;

    public GraphView(final Properties settings, final Map<Long, Object> objects,
            final Set<Relation> relations, final long rootId) {
        this.settings = settings;
        final boolean hypergraphs = settings.getProperty(TextAnController.HYPER_GRAPHS, "false").equals("true");
        final Hypergraph<Object, Relation> g = hypergraphs ? new SetHypergraph<>() : new SparseMultigraph<>();
        Object root = null;
        // Add vertices
        for (Object obj : objects.values()) {
            g.addVertex(obj);
            if (obj.getId() == rootId) {
                root = obj;
            }
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
                    final Object dummy = new RelationObject(rel);
                    g.addVertex(dummy);
                    for (Pair<Object, Integer> pair : rel.getObjects()) {
                        final Relation dummyRel = new DummyRelation(rel.getType(), pair);
                        final int order = pair.getSecond();
                        final Object obj = pair.getFirst();
                        if (order < 0) {
                            g.addEdge(dummyRel, Arrays.asList(obj, dummy), EdgeType.DIRECTED);
                        } else if (order % 2 == 1) {
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
        final Transformer<Object, Paint> vertexPaint =
                obj -> obj instanceof RelationObject ? RELATION_OBJECT_COLOR : Utils.idToAWTColor(obj.getType().getId());
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
        graphMouse = new DefaultModalGraphMouse<>();
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        graphMouse.add(new AbstractPopupGraphMousePlugin() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (objectContextMenu != null && objectContextMenu.isShowing()) {
                    Platform.runLater(() -> {
                        objectContextMenu.hide();
                    });
                }
                super.mousePressed(e);
            }

            @Override
            protected void handlePopup(MouseEvent e) {
                @SuppressWarnings("unchecked")
                final VisualizationViewer<Object, Relation> vv =
                        (VisualizationViewer<Object, Relation>) e.getSource();
                final Point2D p = e.getPoint();

                final GraphElementAccessor<Object, Relation> pickSupport = vv.getPickSupport();
                if(pickSupport != null) {
                    final Point s = MouseInfo.getPointerInfo().getLocation(); //e.getLocationOnScreen() is not good enough
                    final Object v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
                    objectForGraph = v;
                    if (v != null && objectContextMenu != null) {
                        Platform.runLater(() -> {
                            objectContextMenu.show(GraphView.this, s.getX(), s.getY());
                        });
                    }/* else {
                        final Relation edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
                        if (edge != null ) {
                            Platform.runLater(() -> {
                                contextMenu.show(GraphView.this, s.getX(), s.getY());
                            });
                        }
                    }*/
                }
            }
        });
        visualizator.setGraphMouse(graphMouse);
        visualizator.addKeyListener(new DefaultModalGraphMouse.ModeKeyAdapter(graphMouse)); //press t and p to change modes!
        //
        try {
            SwingUtilities.invokeAndWait(() -> {
                this.setContent(visualizator);
            });
        } catch (Exception e) { }

        //center to the graph root, for some reason we must wait a bit
        final Object r = root;
        new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (Exception e) { }
            SwingUtilities.invokeLater(() -> {
                Point2D p = layout.transform(r);
                MutableTransformer layout2 = visualizator.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
                Point2D ctr = visualizator.getCenter();
                double scale = visualizator.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                double deltaX = (ctr.getX() - p.getX())*1/scale;
                double deltaY = (ctr.getY() - p.getY())*1/scale;
                layout2.translate(deltaX, deltaY);
            });
        }).start();
    }

    /**
     * Returns context menu for nodes.
     * @return context menu for nodes
     */
    public ContextMenu getObjectContextMenu() {
        return objectContextMenu;
    }

    /**
     * Sets context menu for nodes.
     * @param objectContextMenu new object context menu
     */
    public void setObjectContextMenu(final ContextMenu objectContextMenu) {
        this.objectContextMenu = objectContextMenu;
    }

    /**
     * Switches graph to pick mode.
     */
    public void pick() {
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        SwingUtilities.invokeLater(() -> {
            visualizator.setSize((int) width, (int) height);
        });
    }

    /**
     * Switches graph to transform mode.
     */
    public void transform() {
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
    }
}
