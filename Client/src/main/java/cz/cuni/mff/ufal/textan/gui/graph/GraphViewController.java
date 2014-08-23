package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.graph.IGrapher;
import cz.cuni.mff.ufal.textan.gui.ObjectContextMenu;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.Utils.CONTEXT_MENU_STYLE;
import cz.cuni.mff.ufal.textan.gui.Window;
import cz.cuni.mff.ufal.textan.gui.graph.string.Handler;
import java.math.BigDecimal;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import jfxtras.labs.internal.scene.control.skin.BigDecimalFieldSkin;
import jfxtras.labs.scene.control.BigDecimalField;

/**
 * Controls GraphView.
 */
public class GraphViewController extends GraphController {

    /** Checkbox style class. */
    static private final String CHECKBOX = "-check-box-";

    /** Object checkbox style class. */
    static private final String OBJECT_CHECKBOX = "object" + CHECKBOX;

    /** Relation checkbox style class. */
    static private final String RELATION_CHECKBOX = "relation" + CHECKBOX;

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

    @FXML
    private HBox leftPanel;

    @FXML
    private HBox rightPanel;

    @FXML
    private ListView<Pair<BooleanProperty, ObjectType>> objectTypesListView;

    @FXML
    private ListView<Pair<BooleanProperty, RelationType>> relationTypesListView;

    /** Graph container. */
    GraphView graphView;

    /** Synchronization lock. */
    final Semaphore lock = new Semaphore(1);

    /** Context menu for nodes. */
    ObjectContextMenu objectContextMenu;

    /** Context menu for edges. */
    ContextMenu relationContextMenu;

    @FXML
    private void toggleObjectFilter() {
        final boolean hidden = objectTypesListView.getParent() == null;
        settings.setProperty("graph.viewer.objectfilter", hidden ? "true" : "false");
        if (hidden) {
            leftPanel.getChildren().add(0, objectTypesListView);
        } else {
            leftPanel.getChildren().remove(objectTypesListView);
        }
    }

    @FXML
    private void toggleRelationFilter() {
        final boolean hidden = relationTypesListView.getParent() == null;
        settings.setProperty("graph.viewer.relationfilter", hidden ? "true" : "false");
        if (hidden) {
            rightPanel.getChildren().add(relationTypesListView);
        } else {
            rightPanel.getChildren().remove(relationTypesListView);
        }
    }

    @FXML
    private void home() {
        graphView.home();
    }

    @FXML
    private void refresh() {
        grapher.clearCache();
        filter();
    }

    @FXML
    private void filter() {
        if (lock.tryAcquire()) {
            final Node node = getMainNode();
            node.setCursor(Cursor.WAIT);
            grapher.setDistance(distanceField.getNumber().intValue());
            final List<ObjectType> ignoredObjects = objectTypesListView.getItems().stream()
                    .filter(p -> !p.getFirst().get())
                    .map(Pair::getSecond)
                    .collect(Collectors.toList());
            grapher.setIgnoredObjectTypes(ignoredObjects);
            final List<RelationType> ignoredRelations = relationTypesListView.getItems().stream()
                    .filter(p -> !p.getFirst().get())
                    .map(Pair::getSecond)
                    .collect(Collectors.toList());
            grapher.setIgnoredRelationTypes(ignoredRelations);
            new Thread(new GraphGetter(false), "GraphGetter").start();
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
        //
        objectTypesListView.setCellFactory(list -> {
            final CheckBoxListCell<Pair<BooleanProperty, ObjectType>> result = new CheckBoxListCell<>(
                    p -> p.getFirst(),
                    new StringConverter<Pair<BooleanProperty, ObjectType>>() {
                        @Override
                        public String toString(Pair<BooleanProperty, ObjectType> pair) {
                            if (pair != null && pair.getSecond() != null) {
                                return pair.getSecond().getName();
                            } else {
                                return "";
                            }
                        }
                        @Override
                        public Pair<BooleanProperty, ObjectType> fromString(String string) {
                            throw new RuntimeException("This should never happen!");
                        }
                    }
            );
            result.itemProperty().addListener((ov, oldVal, newVal) -> {
                if (oldVal != null) {
                    result.getStyleClass().remove(OBJECT_CHECKBOX + oldVal.getSecond().getId());
                }
                if (newVal != null) {
                    result.getStyleClass().add(OBJECT_CHECKBOX + newVal.getSecond().getId());
                }
            });
            return result;
        });
        relationTypesListView.setCellFactory(list -> {
            final CheckBoxListCell<Pair<BooleanProperty, RelationType>> result = new CheckBoxListCell<>(
                    p -> p.getFirst(),
                    new StringConverter<Pair<BooleanProperty, RelationType>>() {
                        @Override
                        public String toString(Pair<BooleanProperty, RelationType> pair) {
                            if (pair != null && pair.getSecond() != null) {
                                return pair.getSecond().getName();
                            } else {
                                return "";
                            }
                        }
                        @Override
                        public Pair<BooleanProperty, RelationType> fromString(String string) {
                            throw new RuntimeException("This should never happen!");
                        }
                    }
            );
            result.itemProperty().addListener((ov, oldVal, newVal) -> {
                if (oldVal != null) {
                    result.getStyleClass().remove(RELATION_CHECKBOX + oldVal.getSecond().getId());
                }
                if (newVal != null) {
                    result.getStyleClass().add(RELATION_CHECKBOX + newVal.getSecond().getId());
                }
            });
            return result;
        });
    }

    @Override
    public void setGrapher(final IGrapher grapher) {
        super.setGrapher(grapher);
        distanceField.setNumber(new BigDecimal(grapher.getDistance()));
        final Node node = getMainNode();
        node.setCursor(Cursor.WAIT);
        new Thread(new GraphGetter(true), "GraphGetter").start();
    }

    @Override
    public void setSettings(final Properties settings) {
        super.setSettings(settings);
        if (settings.getProperty("graph.viewer.objectfilter", "true").equals("false")) {
            leftPanel.getChildren().remove(objectTypesListView);
        }
        if (settings.getProperty("graph.viewer.relationfilter", "true").equals("false")) {
            rightPanel.getChildren().remove(relationTypesListView);
        }
    }

    @Override
    public void setTextAnController(final TextAnController textAnController) {
        super.setTextAnController(textAnController);
    }

    /**
     * Task for getting graph from server.
     */
    protected class GraphGetter extends Task<Graph> {

        /** Got object types. */
        public List<ObjectType> objectTypes;

        /** Got relation types. */
        public List<RelationType> relationTypes;

        /** Flag indicating whether types need to be fetched. */
        public boolean getTypes = false;

        /**
         * Only constructor.
         * @param getTypes should getter get the types as well?
         */
        public GraphGetter(final boolean getTypes) {
            this.getTypes = getTypes;
            setOnSucceeded(e -> {
                final Graph g = getValue();
                graphView = new GraphView(settings,
                        g.getNodes(), g.getEdges(), grapher.getCenterer());
                stackPane.getChildren().clear();
                stackPane.getChildren().add(graphView);
                graphView.requestFocus();
                getMainNode().setCursor(Cursor.DEFAULT);
                scrollPane.requestFocus();
                final Window w = window == null ? stage.getInnerWindow() : window;
                w.setTitleFixed(Utils.localize(resourceBundle, GRAPH_PROPERTY_ID) + " - " + Utils.shortString(grapher.getTitle()));
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
                //
                if (this.getTypes) {
                    final StringBuilder builder = new StringBuilder();
                    for (ObjectType objType : objectTypes) {
                        objectTypesListView.getItems().add(
                                new Pair<>(new SimpleBooleanProperty(true), objType));
                        final Color color =
                                Utils.resolveEntityColorFX(settings, objType.getId());
                        final String stringColor = Utils.colorToString(color);
                        builder.append(String.format(
                                ".%s%s *.box { -fx-background-color: %s; }\n",
                                OBJECT_CHECKBOX, objType.getId(), stringColor));
                    }
                    for (RelationType relType : relationTypes) {
                        relationTypesListView.getItems().add(
                                new Pair<>(new SimpleBooleanProperty(true), relType));
                        final Color color =
                                Utils.resolveRelationColorFX(settings, relType.getId());
                        final String stringColor = Utils.colorToString(color);
                        builder.append(String.format(
                                ".%s%s *.box { -fx-background-color: %s; }\n",
                                RELATION_CHECKBOX, relType.getId(), stringColor));
                    }
                    final String backup = System.getProperty("java.protocol.handler.pkgs");
                    System.setProperty("java.protocol.handler.pkgs",
                            cz.cuni.mff.ufal.textan.Utils.removeExtension(Handler.class.getPackage().getName()));
                    Handler.registerString("GraphView.css", builder.toString());
                    root.getStylesheets().add("string:GraphView.css");
                    if (backup != null) {
                        System.setProperty("java.protocol.handler.pkgs", backup);
                    }
                }
                //
                lock.release();
            });
            setOnFailed(e -> {
                getException().printStackTrace();
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
            if (getTypes) {
                final Client client = textAnController.getClient();
                final Collator collator = Collator.getInstance();
                relationTypes = textAnController.getClient().getRelationTypesList();
                relationTypes.sort((rt1, rt2) -> collator.compare(rt1.getName(), rt2.getName()));
                objectTypes = client.getObjectTypesList();
                objectTypes.sort((ot1, ot2) -> collator.compare(ot1.getName(), ot2.getName()));
            }
            //
            return grapher.getGraph();
        }
    }
}
