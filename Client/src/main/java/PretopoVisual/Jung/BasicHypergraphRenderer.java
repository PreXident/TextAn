package PretopoVisual.Jung;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import java.awt.Color;
import java.util.ConcurrentModificationException;

/**
 * This class is included into the project just to fix {@link #colorFromIndex(int, boolean)}.
 */
public class BasicHypergraphRenderer<V, E> extends BasicRenderer<V, E> {

    public BasicHypergraphRenderer() {
        setVertexRenderer(new BasicHypervertexRenderer<>());
        setEdgeRenderer(new BasicHyperedgeRenderer<>());
    }

    @Override
    public void render(RenderContext<V, E> renderContext, Layout<V, E> layout) {
        Graph<V, E> g = layout.getGraph();
        if (!(g instanceof PseudoHypergraph)) {
            throw new Error("renderer requires pseudo-hypergraph");
        }
        Hypergraph<V, E> hg = ((PseudoHypergraph<V, E>) g).getHypergraph();
        try {
            for (E e : hg.getEdges()) {
                renderEdge(renderContext, layout, e);

                renderEdgeLabel(renderContext, layout, e);
            }
        } catch (ConcurrentModificationException cme) {
            renderContext.getScreenDevice().repaint();
        }
        try {
            for (V v : hg.getVertices()) {
                renderVertex(renderContext, layout, v);

                renderVertexLabel(renderContext, layout, v);
            }
        } catch (ConcurrentModificationException cme) {
            renderContext.getScreenDevice().repaint();
        }
    }

    static Color colorFromIndex(int index, boolean bright) {
        int STEPS = 6;
        float brightness;
        if (bright) {
            brightness = 0.8F;
        } else {
            brightness = 0.5F;
        }
        float hue = index % STEPS / 6.0F;

        float saturation = 1.0F / (index / STEPS + 1.0F);

        Color c = Color.getHSBColor(hue, saturation, brightness);
        //we want all edges to have proper colors
        //withou discriminating the one with index 0
//        if (index == 0) {
//            c = new Color(0, 0, 0, 0);
//        }
        return c;
    }

    static <V, E> int indexOfHyperedge(Hypergraph<V, E> hg, E edge) {
        int result = -1;
        for (E currentEdge : hg.getEdges()) {
            result++;
            if (edge.equals(currentEdge)) {
                return result;
            }
        }
        return -1;
    }
}
