package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Inner window displaying graphs.
 */
public class GraphWindow extends InnerWindow {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Graph Viewer";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "graph.viewer";

    /** Graph information provider. */
    final protected Grapher grapher;

    /**
     * Only constructor.
     * If grapher is initialized by distance and center, graph is displayed,
     * otherwise the object list is displayed.
     * @param textAnController parent controller
     * @param settings properties with settings
     * @param grapher graph information provider
     */
    public GraphWindow(final TextAnController textAnController, final Properties settings, final Grapher grapher) {
        super(TITLE, PROPERTY_ID, settings);
        this.grapher = grapher;
        ResourceBundle resourceBundle = null;
        try {
            final Pair<Parent, GraphController> loaded = GraphController.loadFXML(grapher);
            getContentPane().getChildren().add(loaded.getFirst());
            final GraphController controller = loaded.getSecond();
            resourceBundle = controller.resourceBundle;
            setTitle(Utils.localize(resourceBundle, PROPERTY_ID));
            controller.setTextAnController(textAnController);
            controller.setWindow(this);
            controller.setSettings(settings);
            controller.setGrapher(grapher);
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.create()
                    .title(Utils.localize(resourceBundle, "page.load.error"))
                    .showException(e);
        }
    }
}
