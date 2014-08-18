package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.graph.IGrapher;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.graph.GraphController.GRAPH_PROPERTY_ID;
import static cz.cuni.mff.ufal.textan.gui.graph.GraphController.MIN_HEIGHT;
import static cz.cuni.mff.ufal.textan.gui.graph.GraphController.MIN_WIDTH;
import static cz.cuni.mff.ufal.textan.gui.graph.GraphController.TITLE;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Inner window displaying graphs.
 */
public class GraphWindow extends InnerWindow {

    /** Graph information provider. */
    final protected IGrapher grapher;

    /**
     * Only constructor.
     * If grapher is initialized by distance and center, graph is displayed,
     * otherwise the object list is displayed.
     * @param textAnController parent controller
     * @param settings properties with settings
     * @param grapher graph information provider
     */
    public GraphWindow(final TextAnController textAnController,
            final Properties settings, final IGrapher grapher) {
        super(TITLE, GRAPH_PROPERTY_ID, settings);
        this.grapher = grapher;
        setMinHeight(MIN_HEIGHT);
        setMinWidth(MIN_WIDTH);
        ResourceBundle resourceBundle = null;
        try {
            final Pair<Parent, GraphController> loaded = GraphController.loadFXML(grapher);
            getContentPane().getChildren().add(loaded.getFirst());
            final GraphController controller = loaded.getSecond();
            resourceBundle = controller.resourceBundle;
            setTitle(Utils.localize(resourceBundle, GRAPH_PROPERTY_ID));
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
