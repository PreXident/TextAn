package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import jfxtras.labs.scene.control.BigDecimalField;

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

    @FXML
    private BigDecimalField distanceField;

    @FXML
    private ToolBar toolbar;

    @FXML
    private HBox leftToolbar;

    @FXML
    private HBox rightToolbar;

    /** Localization container. */
    ResourceBundle resourceBundle;

    /** Graph container. */
    GraphView graphView;

    final Semaphore lock = new Semaphore(1);

    @FXML
    private void newDistance() {
        if (lock.tryAcquire()) {
            final Node node = getMainNode();
            node.setCursor(Cursor.WAIT);
            grapher.setDistance(distanceField.getNumber().intValue());
            new Thread(new GraphGetter(), "GraphGetter").start();
        }
    }

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
        leftToolbar.prefWidthProperty().bind(toolbar.widthProperty().add(-25).divide(2));
        rightToolbar.prefWidthProperty().bind(toolbar.widthProperty().add(-25).divide(2));
    }

    /**
     * Sets graph information provider.
     * @param grapher new graph information provider
     */
    @Override
    public void setGrapher(final Grapher grapher) {
        super.setGrapher(grapher);
        distanceField.setNumber(new BigDecimal(grapher.getDistance()));
        final Node node = getMainNode();
        node.setCursor(Cursor.WAIT);
        new Thread(new GraphGetter(), "GraphGetter").start();
    }

    /**
     * Task for getting graph from server.
     */
    protected class GraphGetter extends Task<Graph> {

        /**
         * Only constructor.
         */
        public GraphGetter() {
            setOnSucceeded(e -> {
                final Graph g = getValue();
                graphView = new GraphView(settings,
                        g.getNodes(), g.getEdges(), grapher.getRootId());
                stackPane.getChildren().clear();
                stackPane.getChildren().add(graphView);
                graphView.requestFocus();
                getMainNode().setCursor(Cursor.DEFAULT);
                scrollPane.requestFocus();
                lock.release();
            });
            setOnFailed(e -> {
                getMainNode().setCursor(Cursor.DEFAULT);
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title(Utils.localize(resourceBundle, "page.load.error"))
                            .showException(getException());
                });
                lock.release();
            });
        }

        @Override
        protected Graph call() throws Exception {
            return grapher.getGraph();
        }
    }
}
