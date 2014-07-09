package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.JoinedObject;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.Utils.OBJECT_CONTEXT_MENU;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Controls editing the report relations.
 */
public class ReportErrorsController extends ReportWizardController {

    @FXML
    BorderPane root;

    @FXML
    private TableView<Object> newObjectsTable;

    @FXML
    private TableColumn<Object, Number> newObjectsTableIdColumn;

    @FXML
    private TableColumn<Object, ObjectType> newObjectsTableTypeColumn;

    @FXML
    private TableColumn<Object, String> newObjectsTableAliasColumn;

    @FXML
    ListView<FXRelationBuilder> relationsListView;

    @FXML
    private TreeView<java.lang.Object> relationsTreeView;

    @FXML
    private TreeView<Object> joinedObjectsTreeView;

    @FXML
    private VBox vbox;

    @FXML
    Slider slider;

    @FXML
    Label processedLabel;

    @FXML
    Label changedLabel;

    @FXML
    GridPane gridPane;

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Context menu for new objects. */
    ContextMenu newObjectsContextMenu = new ContextMenu();

    /** Context menu for joined objects. */
    ContextMenu joinedObjectsContextMenu = new ContextMenu();

    /** Context menu for relation's objects. */
    ContextMenu relationsContextMenu = new ContextMenu();

    @FXML
    private void back() {
        if (pipeline.lock.tryAcquire()) {
            pipeline.back();
        }
    }

    @FXML
    private void force() {
        if (pipeline.lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            new Thread(() -> {
                pipeline.forceSave();
            }, "ForceSave").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
        slider.addEventFilter(EventType.ROOT, e -> e.consume());
        slider.setLabelFormatter(new SliderLabelFormatter());
        final MenuItem graphMI = new MenuItem(Utils.localize(resourceBundle, "graph.show"));
        graphMI.setOnAction(e -> {
            final Object obj = newObjectsTable.getSelectionModel().getSelectedItem();
            if (obj != null) {
                textAnController.displayGraph(obj.getId());
            }
        });
        newObjectsContextMenu.getItems().add(graphMI);
        newObjectsContextMenu.setConsumeAutoHidingEvents(false);
        newObjectsContextMenu.setStyle(OBJECT_CONTEXT_MENU);
        //
        final MenuItem joinedGraphMI = new MenuItem(Utils.localize(resourceBundle, "graph.show"));
        joinedGraphMI.setOnAction(e -> {
            final Object obj = joinedObjectsTreeView.getSelectionModel().getSelectedItem().getValue();
            if (obj != null) {
                textAnController.displayGraph(obj.getId());
            }
        });
        joinedObjectsContextMenu.getItems().add(joinedGraphMI);
        joinedObjectsContextMenu.setConsumeAutoHidingEvents(false);
        joinedObjectsContextMenu.setStyle(OBJECT_CONTEXT_MENU);
        joinedObjectsTreeView.setContextMenu(joinedObjectsContextMenu);
        //
        newObjectsTable.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal != null) {
                newObjectsTable.setContextMenu(newObjectsContextMenu);
            } else {
                newObjectsTable.setContextMenu(null);
            }
        });
        newObjectsTableIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, Number> p) -> new ReadOnlyLongWrapper(p.getValue().getId()));
        newObjectsTableIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return t != null ? t.toString() : "";
            }
            @Override
            public Number fromString(String string) {
                return Long.parseLong(string);
            }
        }));
        newObjectsTableTypeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, ObjectType> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getType()));
        newObjectsTableTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<ObjectType>() {
            @Override
            public String toString(ObjectType t) {
                return t != null ? t.getName() : "";
            }
            @Override
            public ObjectType fromString(String string) {
                throw new UnsupportedOperationException("This should never happan!");
            }
        }));
        newObjectsTableAliasColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, String> p) -> new ReadOnlyStringWrapper(p.getValue().getAliasString()));
        newObjectsTableAliasColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        //
        final MenuItem relationGraphMI = new MenuItem(Utils.localize(resourceBundle, "graph.show"));
        relationGraphMI.setOnAction(e -> {
            final java.lang.Object obj = relationsTreeView.getSelectionModel().getSelectedItem().getValue();
            if (obj instanceof RelationTriple) {
                final RelationTriple t = (RelationTriple) obj;
                textAnController.displayGraph(t.triple.getThird().getId());
            }
        });
        relationsContextMenu.getItems().add(relationGraphMI);
        relationsContextMenu.setConsumeAutoHidingEvents(false);
        relationsContextMenu.setStyle(OBJECT_CONTEXT_MENU);
        relationsTreeView.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal == null || !(newVal.getValue() instanceof RelationTriple)) {
                relationsTreeView.setContextMenu(null);
            } else {
                relationsTreeView.setContextMenu(relationsContextMenu);
            }
        });
        //
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.setFillWidth(true);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        column2.setFillWidth(true);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setHgrow(Priority.ALWAYS);
        column3.setFillWidth(true);
        gridPane.getColumnConstraints().addAll(column1, column2, column3);
        RowConstraints row3 = new RowConstraints();
        row3.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), row3);
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        newObjectsTable.getItems().addAll(pipeline.getProblems().getNewObjects());
        //
        joinedObjectsTreeView.setRoot(new TreeItem<>(null));
        for (JoinedObject joined : pipeline.getProblems().getJoinedObjects()) {
            final TreeItem<Object> parent = new TreeItem<>(joined.root);
            for (Object obj : joined.children) {
                final TreeItem<Object> child = new TreeItem<>(obj);
                parent.getChildren().add(child);
            }
            joinedObjectsTreeView.getRoot().getChildren().add(parent);
        }
        //
        relationsTreeView.setRoot(new TreeItem<>(null));
        for (Relation relation : pipeline.getProblems().getNewRelations()) {
            final TreeItem<java.lang.Object> parent = new TreeItem<>(relation);
            for (Triple<Integer, String, Object> triple : relation.getObjects()) {
                final TreeItem<java.lang.Object> child =
                        new TreeItem<>(new RelationTriple(triple));
                parent.getChildren().add(child);
            }
            relationsTreeView.getRoot().getChildren().add(parent);
        }
        //
        if (!pipeline.getProblems().isProcessed()) {
            vbox.getChildren().remove(processedLabel);
        }
        if (!pipeline.getProblems().isChanged()) {
            vbox.getChildren().remove(changedLabel);
        }
    }

    /**
     * Simple holder for displaying relation triple.
     */
    static private class RelationTriple {

        /** Wrapped triple. */
        final Triple<Integer, String, Object> triple;

        /**
         * Only constructor.
         * @param triple triple to wrap
         */
        public RelationTriple(final Triple<Integer, String, Object> triple) {
            this.triple = triple;
        }

        @Override
        public String toString() {
            return triple.getFirst() + " - " + triple.getSecond()
                    + " - " + triple.getThird().toString();
        }
    }
}
