package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.JoinedObject;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
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
    private TableView<Relation> newRelationsTable;

    @FXML
    private TableColumn<Relation, String> newRelationsTableRelation;

    @FXML
    private TreeView<Object> treeView;

    @FXML
    Slider slider;

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Context menu for new objects. */
    ContextMenu newObjectsContextMenu = new ContextMenu();

    /** Context menu for joined objects. */
    ContextMenu joinedObjectsContextMenu = new ContextMenu();

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
        //
        final MenuItem joinedGraphMI = new MenuItem(Utils.localize(resourceBundle, "graph.show"));
        joinedGraphMI.setOnAction(e -> {
            final Object obj = treeView.getSelectionModel().getSelectedItem().getValue();
            if (obj != null) {
                textAnController.displayGraph(obj.getId());
            }
        });
        joinedObjectsContextMenu.getItems().add(graphMI);
        joinedObjectsContextMenu.setConsumeAutoHidingEvents(false);
        treeView.setContextMenu(joinedObjectsContextMenu);
        //
        newObjectsTable.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal != null) {
                newObjectsTable.setContextMenu(newObjectsContextMenu);
            } else {
                newObjectsTable.setContextMenu(null);
            }
        });
        newObjectsTableAliasColumn.prefWidthProperty().bind(newObjectsTable.widthProperty().add(newObjectsTableIdColumn.prefWidthProperty().add(newObjectsTableTypeColumn.prefWidthProperty()) .multiply(-1).add(-2)));
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
        //newRelationsTableRelation.prefWidthProperty().bind(newRelationsTable.widthProperty());
        newRelationsTableRelation.setCellValueFactory((TableColumn.CellDataFeatures<Relation, String> p) -> new ReadOnlyStringWrapper(p.getValue().toString()));
        newRelationsTableRelation.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        newObjectsTable.getItems().addAll(pipeline.getProblems().getNewObjects());
        newRelationsTable.getItems().addAll(pipeline.getProblems().getNewRelations());
        treeView.setRoot(new TreeItem<>(null));
        for (JoinedObject joined : pipeline.getProblems().getJoinedObjects()) {
            final TreeItem<Object> parent = new TreeItem<>(joined.root);
            for (Object obj : joined.children) {
                final TreeItem<Object> child = new TreeItem<>(obj);
                parent.getChildren().add(child);
            }
            treeView.getRoot().getChildren().add(parent);
        }
    }
}
