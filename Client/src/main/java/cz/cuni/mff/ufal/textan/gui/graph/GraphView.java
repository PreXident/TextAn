package cz.cuni.mff.ufal.textan.gui.graph;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import javafx.embed.swing.SwingNode;
import javax.swing.SwingUtilities;
import org.apache.commons.collections15.Transformer;

/**
 * Displays graphs.
 */
public class GraphView extends SwingNode {

    final VisualizationViewer<Integer,String> visualizator;

    public GraphView() {
        // Graph<V, E> where V is the type of the vertices
        // and E is the type of the edges
        Graph<Integer, String> g = new DirectedSparseMultigraph<>();
        // Add some vertices. From above we defined these to be type Integer.
        g.addVertex((Integer)1);
        g.addVertex((Integer)2);
        g.addVertex((Integer)3);
        // Add some edges. From above we defined these to be of type String
        // Note that the default is for undirected edges.
        g.addEdge("Edge-A", 1, 2); // Note that Java 1.5 auto-boxes primitives
        g.addEdge("Edge-B", 2, 3);
        // Let's see what we have. Note the nice output from the
        // SparseMultigraph<V,E> toString() method
        System.out.println("The graph g = " + g.toString());
        //
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<Integer, String> layout = new CircleLayout<>(g);
        layout.setSize(new Dimension(300,300)); // sets the initial size of the space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        //BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<>(layout);
        visualizator = new VisualizationViewer<>(layout);

        visualizator.setPreferredSize(new Dimension(350, 350)); //Sets the viewing area size
        //
        // Setup up a new vertex to paint transformer...
        Transformer<Integer,Paint> vertexPaint = (Integer i) -> Color.GREEN;
        // Set up a new stroke Transformer for the edges
        float dash[] = {10.0f};
        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        Transformer<String, Stroke> edgeStrokeTransformer = (String s) -> edgeStroke;
        visualizator.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        visualizator.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        visualizator.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
        visualizator.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());
        visualizator.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        //
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
