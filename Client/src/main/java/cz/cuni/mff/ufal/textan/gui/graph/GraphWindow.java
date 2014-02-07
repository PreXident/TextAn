package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
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

    /** Localization container. */
    protected ResourceBundle resourceBundle;

    /**
     * Only constructor.
     * @param settings properties with settings
     * @param grapher graph information provider
     */
    public GraphWindow(final Properties settings, final Grapher grapher) {
        super(TITLE, PROPERTY_ID, settings);
        this.grapher = grapher;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.graph.ObjectList");
            setTitle(Utils.localize(resourceBundle, PROPERTY_ID));
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("ObjectList.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            GraphController controller = loader.getController();
            controller.setSettings(settings);
            controller.setGrapher(grapher);
            controller.setWindow(this);
            getContentPane().getChildren().add(loadedRoot);
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.create()
                    .title(Utils.localize(resourceBundle, "page.load.error"))
                    .showException(e);
        }
    }
}
