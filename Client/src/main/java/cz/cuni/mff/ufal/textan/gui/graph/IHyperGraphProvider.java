package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.util.Collection;

/**
 * Provides interface for creating visual components displaying hypergraphs.
 */
public interface IHyperGraphProvider {

    /**
     * Returns unique identifier for the provider.
     * @return unique identifier for the provider
     */
    String getId();

    /**
     * Returns localization key for the description.
     * @return localization key for the description
     */
    String getDescKey();

    /**
     * Returns English description if no localization is found for descKey.
     * @return English description if no localization is found for descKey
     */
    String getDesc();

    /**
     * Returns new hypergraph.
     * @return new hypergraph
     */
    Hypergraph<Object, Relation> createHyperGraph();

    /**
     * Adds objects to graph.
     * @param graph graph to add objects to
     * @param objects objects to add to the graph
     */
    void addObjects(Hypergraph<Object, Relation> graph, Collection<Object> objects);

    /**
     * Adds relations to graph.
     * @param graph graph to add relations to
     * @param relations relations to add to the graph
     */
    void addRelations(Hypergraph<Object, Relation> graph, Collection<Relation> relations);

    /**
     * Transforms the graph to form suitable for layout.
     * @param graph graph to transform
     * @return transformed graph
     */
    Graph<Object, Relation> transformForLayout(Hypergraph<Object, Relation> graph);

    /**
     * Creates new graph renderer or null.
     * @return creates new graph renderer or null
     */
    Renderer<Object, Relation> createRenderer();
}
