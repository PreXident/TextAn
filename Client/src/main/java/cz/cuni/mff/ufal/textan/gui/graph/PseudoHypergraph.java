package cz.cuni.mff.ufal.textan.gui.graph;

import edu.uci.ics.jung.graph.Hypergraph;
import java.util.Collection;

/**
 * Fixes original PseudoHypergraph's getOpposite method.
 * The original method just returns null, which causes NullPointerException for
 * unary edges. Our implementation tries to fullfil the getOpposite's contract,
 * but for unary edges returns the given vertex.
 * @param <V> vertex type
 * @param <E> edge type
 */
public class PseudoHypergraph<V, E> extends PretopoVisual.Jung.PseudoHypergraph<V, E> {

    /**
     * Only constructor.
     * @param hypergraph undelying hypergraph
     */
    public PseudoHypergraph(final Hypergraph<V, E> hypergraph) {
        super(hypergraph);
    }


    /**
     * Returns the vertex at the other end of <code>edge</code> from <code>vertex</code>.
     * (That is, returns the vertex incident to <code>edge</code> which is not <code>vertex</code>,
     * unless the edge is only unary, in which case returns <code>vertex</code>.
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return the vertex at the other end of <code>edge</code> from <code>vertex</code>
     * @see edu.uci.ics.jung.graph.Graph#getOpposite(Object, Object)
     */
    @Override
    public V getOpposite(final V vertex, final E edge) {
        final Collection<V> nodes = getHypergraph().getIncidentVertices(edge);
        return nodes.stream()
                .filter(v -> !v.equals(vertex)) //try to find any other vertex
                .findFirst().orElse(vertex); //we must return something...
    }
}
