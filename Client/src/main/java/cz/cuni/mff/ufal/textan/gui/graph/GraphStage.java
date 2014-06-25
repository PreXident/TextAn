package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Class for displaying Report Wizard in independent window.
 */
public class GraphStage extends OuterStage {

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
    public GraphStage(final TextAnController textAnController, final Properties settings, final Grapher grapher) {
        super(TITLE, PROPERTY_ID, settings);
        this.grapher = grapher;
        ResourceBundle resourceBundle = null;
        try {
            final Pair<Parent, GraphController> loaded = GraphController.loadFXML(grapher);
            getInnerWindow().getContentPane().getChildren().add(loaded.getFirst());
            final GraphController controller = loaded.getSecond();
            controller.setTextAnController(textAnController);
            resourceBundle = controller.resourceBundle;
            controller.setStage(this);
            controller.setSettings(settings);
            controller.setGrapher(grapher);
            setTitle(Utils.localize(resourceBundle, PROPERTY_ID));
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.create()
                    .title(Utils.localize(resourceBundle, "page.load.error"))
                    .showException(e);
        }
    }

    @Override
    public void close() {
        super.close();
    }
}
