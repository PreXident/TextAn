package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.graph.IGrapher;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Abstract parent for graph controllers.
 */
abstract class GraphController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Graph Viewer";

    /** Identifier used to store properties in {@link #settings}. */
    static protected final String GRAPH_PROPERTY_ID = "graph.viewer";

    /** Minimal height of the graph window. */
    static protected final int MIN_HEIGHT = 400;

    /** Minimal width of the graph window. */
    static protected final int MIN_WIDTH = 600;

    /**
     * Loads FXML suitable for the grapher.
     * If grapher is initialized by distance and center, graph is loaded,
     * otherwise the object list is loaded.
     * @param grapher graph information provider
     * @return loaded fxml root and its controller
     */
    static public Pair<Parent, GraphController> loadFXML(final IGrapher grapher)
            throws IOException {
        String bundleId;
        String fxml;
        if (grapher.isReady()) {
            bundleId = "cz.cuni.mff.ufal.textan.gui.graph.GraphView";
            fxml = "GraphView.fxml";
        } else {
            bundleId = "cz.cuni.mff.ufal.textan.gui.graph.ObjectList";
            fxml = "ObjectList.fxml";
        }
        ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleId);
        final FXMLLoader loader = new FXMLLoader(GraphController.class.getResource(fxml), resourceBundle);
        final Parent loadedRoot = (Parent) loader.load();
        GraphController controller = loader.getController();
        return new Pair<>(loadedRoot, controller);
    }

    /** Graph information provider. */
    protected IGrapher grapher;

    /** Localization container. */
    protected ResourceBundle resourceBundle;

    /** Parent controller. */
    protected TextAnController textAnController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
    }

    /**
     * Sets graph information provider.
     * @param grapher new graph information provider
     */
    public void setGrapher(final IGrapher grapher) {
        this.grapher = grapher;
    }

    /**
     * Sets parent controller.
     * @param textAnController new parent controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
    }
}
