package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.JoinedObject;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.ObjectContextMenu;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.relation.RelationTreeView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    private RelationTreeView relationsTreeView;

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

    @FXML
    Button forceButton;

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Context menu for new objects. */
    ObjectContextMenu newObjectsContextMenu;

    /** Context menu for joined objects. */
    ObjectContextMenu joinedObjectsContextMenu;

    /** JoinedObject that is selected. */
    ObjectProperty<Object> selectedJoinedObject = new SimpleObjectProperty<>();

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
                handleDocumentChangedException(root, () -> {
                    pipeline.forceSave();
                    return null;
                });
            }, "ForceSave").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
        slider.addEventFilter(EventType.ROOT, e -> e.consume());
        slider.setLabelFormatter(new SliderLabelFormatter());
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
        relationsTreeView.addRelations(pipeline.getProblems().getNewRelations());
        //
        if (!pipeline.getProblems().isProcessed()) {
            vbox.getChildren().remove(processedLabel);
        } else {
            forceButton.setVisible(false);
        }
        if (!pipeline.getProblems().isChanged()) {
            vbox.getChildren().remove(changedLabel);
        }
    }

    @Override
    public void setTextAnController(final TextAnController textAnController) {
        newObjectsContextMenu = new ObjectContextMenu(textAnController);
        newObjectsContextMenu.objectProperty().bind(newObjectsTable.getSelectionModel().selectedItemProperty());
        //
        joinedObjectsContextMenu = new ObjectContextMenu(textAnController);
        joinedObjectsContextMenu.objectProperty().bind(Bindings.createObjectBinding(() -> {
            final TreeItem<Object> selected =
                    joinedObjectsTreeView.getSelectionModel().getSelectedItem();
            return selected != null ? selected.getValue() : null;
        }, joinedObjectsTreeView.getSelectionModel().selectedItemProperty()));
        joinedObjectsTreeView.setContextMenu(joinedObjectsContextMenu);
        //
        relationsTreeView.setTextAnController(textAnController);
    }
}
