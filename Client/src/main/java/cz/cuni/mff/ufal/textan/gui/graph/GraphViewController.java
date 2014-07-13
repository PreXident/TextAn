package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.ObjectContextMenu;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.Utils.CONTEXT_MENU_STYLE;
import cz.cuni.mff.ufal.textan.gui.Window;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import jfxtras.labs.internal.scene.control.skin.BigDecimalFieldSkin;
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

    /** Graph container. */
    GraphView graphView;

    /** Synchronization lock. */
    final Semaphore lock = new Semaphore(1);

    /** Context menu for nodes. */
    ObjectContextMenu objectContextMenu;

    /** Context menu for edges. */
    ContextMenu relationContextMenu;

    @FXML
    private void home() {
        graphView.home();
    }

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
        super.initialize(url, rb);
        stackPane.prefWidthProperty().bind(scrollPane.widthProperty());
        stackPane.prefHeightProperty().bind(scrollPane.heightProperty());
        leftToolbar.prefWidthProperty().bind(toolbar.widthProperty().add(-25).divide(2));
        rightToolbar.prefWidthProperty().bind(toolbar.widthProperty().add(-25).divide(2));
        //ugly shortcut for centering by HOME key
        root.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.getCode() == KeyCode.HOME
                    && !(t.getTarget() instanceof BigDecimalFieldSkin.NumberTextField)) {
                t.consume();
                home();
            }
        });
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

    @Override
    public void setTextAnController(final TextAnController textAnController) {
        super.setTextAnController(textAnController);
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
                final Object center = g.getNodes().get(grapher.getRootId());
                final Window w = window == null ? stage.getInnerWindow() : window;
                w.setTitle(Utils.localize(resourceBundle, GRAPH_PROPERTY_ID) + " - " + Utils.shortString(center.toString()));
                objectContextMenu = new ObjectContextMenu(textAnController);
                objectContextMenu.objectProperty().bind(graphView.objectForGraph);
                objectContextMenu.distanceProperty().bind(distanceField.numberProperty());
                graphView.setObjectContextMenu(objectContextMenu);
                relationContextMenu = new ContextMenu();
                relationContextMenu.setStyle(CONTEXT_MENU_STYLE);
                graphView.relationForGraph.addListener((ov, oldVal, newVal) -> {
                    relationContextMenu.getItems().clear();
                    final List<Triple<Integer, String, Object>> list =
                            new ArrayList<>(newVal.getObjects());
                    Collections.sort(list, (t1, t2) -> t1.getFirst() - t2.getFirst());
                    list.forEach(t -> {
                        final String text = String.format("%s: %s - %s",
                                t.getFirst(), t.getSecond(), t.getThird());
                        final MenuItem mi = new MenuItem(text);
                        mi.setUserData(t.getThird());
                        mi.setOnAction(ev -> {
                            final Object obj = (Object) ((MenuItem)ev.getSource()).getUserData();
                            graphView.centerToObject(obj);
                        });
                        relationContextMenu.getItems().add(mi);
                    });
                });
                graphView.setRelationContextMenu(relationContextMenu);
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
