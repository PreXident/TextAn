package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Controls GraphView.
 */
public class GraphViewController extends GraphController {

    @FXML
    private BorderPane root;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private ToggleButton transformButton;

    @FXML
    private ToggleButton pickButton;

    /** Localization container. */
    ResourceBundle resourceBundle;

    /** Graph container. */
    GraphView graphView;

    @FXML
    private void pick() {
        if (graphView != null) {
            graphView.pick();
            pickButton.setSelected(true);
        } else {
            transformButton.setSelected(true);
        }
    }

    @FXML
    private void transform() {
        if (graphView != null) {
            graphView.transform();
        }
        transformButton.setSelected(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //localization
        this.resourceBundle = rb;
        stackPane.prefWidthProperty().bind(scrollPane.widthProperty());
        stackPane.prefHeightProperty().bind(scrollPane.heightProperty());
    }

    /**
     * Sets graph information provider.
     * @param grapher new graph information provider
     */
    @Override
    public void setGrapher(final Grapher grapher) {
        super.setGrapher(grapher);
        final Node node = getMainNode();
        node.setCursor(Cursor.WAIT);
        final Task<Graph> task = new Task<Graph>() {
            @Override
            protected Graph call() throws Exception {
                return grapher.getGraph();
            }
        };
        task.setOnSucceeded(e -> {
            final Graph g = task.getValue();
            graphView = new GraphView(settings,
                    g.getNodes(), g.getEdges(), grapher.getRootId());
            stackPane.getChildren().add(graphView);
            graphView.requestFocus();
            node.setCursor(Cursor.DEFAULT);
            scrollPane.requestFocus();
        });
        task.setOnFailed(e -> {
            node.setCursor(Cursor.DEFAULT);
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "page.load.error"))
                        .showException(task.getException());
            });
        });
        new Thread(task, "Grapher").start();
    }
}
