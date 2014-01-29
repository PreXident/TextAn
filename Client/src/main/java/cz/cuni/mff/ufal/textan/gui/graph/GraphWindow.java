package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import java.io.IOException;
import java.util.Properties;
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

    /**
     * Only constructor.
     * @param settings properties with settings
     * @param grapher graph information provider
     */
    public GraphWindow(final Properties settings, final Grapher grapher) {
        super(TITLE, PROPERTY_ID, settings);
        this.grapher = grapher;
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("GraphView.fxml"));
            final Parent loadedRoot = (Parent) loader.load();
            GraphViewController controller = loader.getController();
            controller.setSettings(settings);
            controller.setGrapher(grapher);
            controller.setWindow(this);
            getContentPane().getChildren().add(loadedRoot);
        } catch (IOException e) {
            e.printStackTrace();
            Dialogs.create()
                    .title("Problém při načítání stránky!")
                    .showException(e);
        }
    }
}
