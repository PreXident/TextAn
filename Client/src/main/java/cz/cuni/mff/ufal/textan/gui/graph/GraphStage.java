package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
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

    /** Localization container. */
    protected ResourceBundle resourceBundle;

    /**
     * Only constructor.
     * @param settings properties with settings
     * @param grapher graph information provider
     */
    public GraphStage(final Properties settings, final Grapher grapher) {
        super(TITLE, PROPERTY_ID, settings);
        this.grapher = grapher;
        try {
            resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.graph.ObjectList");
            setTitle(Utils.localize(resourceBundle, PROPERTY_ID));
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("ObjectList.fxml"), resourceBundle);
            final Parent loadedRoot = (Parent) loader.load();
            GraphViewController controller = loader.getController();
            controller.setSettings(settings);
            controller.setGrapher(grapher);
            controller.setStage(this);
            getInnerWindow().getContentPane().getChildren().add(loadedRoot);
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
        //TODO save logic here
    }
}
