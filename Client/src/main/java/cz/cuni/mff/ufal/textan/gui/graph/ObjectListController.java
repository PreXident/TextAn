package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.GetTypesTask;
import cz.cuni.mff.ufal.textan.gui.ObjectContextMenu;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class ObjectListController extends GraphController {

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Object> table;

    @FXML
    private TableColumn<Object, Number> idColumn;

    @FXML
    private TableColumn<Object, ObjectType> typeColumn;

    @FXML
    private TableColumn<Object, String> aliasColumn;

    @FXML
    private ComboBox<ObjectType> typeComboBox;

    @FXML
    private TextField filterField;

    @FXML
    private ComboBox<Integer> perPageComboBox;

    @FXML
    private Label paginationLabel;

    /** Context menu for objects. */
    protected ObjectContextMenu contextMenu;

    /** Number of displayed page. */
    protected int pageNo = 0;

    /** Number of pages. */
    protected int pageCount = 0;

    /** Number of objects fullfilling the filter. */
    protected int objectCount = 0;

    /** Synchronization lock. */
    protected Semaphore lock = new Semaphore(1);

    @FXML
    private void fastForward() {
        pageNo = pageCount - 1;
        filter();
    }

    @FXML
    private void fastRewind() {
        pageNo = 0;
        filter();
    }

    @FXML
    private void filter() {
        Utils.filterObjects(textAnController.getClient(), this, lock,
                getMainNode(), root, typeComboBox.getValue(),
                filterField.getText(), perPageComboBox.getValue(),
                pageNo, resourceBundle, paginationLabel,
                (objectCnt, pageCnt) -> {
                    objectCount = objectCnt;
                    pageCount = pageCnt;
                }, table.getItems());
    }

    @FXML
    private void forward() {
        if (pageNo < pageCount - 1) {
            ++pageNo;
            filter();
        }
    }

    @FXML
    private void rewind() {
        if (pageNo > 0) {
            --pageNo;
            filter();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        table.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal != null) {
                table.setContextMenu(contextMenu);
            } else {
                table.setContextMenu(null);
            }
        });
        aliasColumn.prefWidthProperty().bind(table.widthProperty().add(idColumn.widthProperty().add(typeColumn.widthProperty()).multiply(-1).add(-30)));
        idColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, Number> p) -> new ReadOnlyLongWrapper(p.getValue().getId()));
        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return t != null ? t.toString() : "";
            }
            @Override
            public Number fromString(String string) {
                return Long.parseLong(string);
            }
        }));
        typeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, ObjectType> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getType()));
        typeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<ObjectType>() {
            @Override
            public String toString(ObjectType t) {
                return t != null ? t.getName() : "";
            }
            @Override
            public ObjectType fromString(String string) {
                throw new UnsupportedOperationException("This should never happan!");
            }
        }));
        aliasColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, String> p) -> new ReadOnlyStringWrapper(p.getValue().getAliasString()));
        aliasColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        typeComboBox.valueProperty().addListener((ov, oldVal, newVal) -> {
            pageNo = 0;
        });
        filterField.textProperty().addListener((o) -> {
            pageNo = 0;
        });
        filterField.setOnAction(e -> {
            filter();
        });
    }

    @Override
    public void setGrapher(final Grapher grapher) {
        super.setGrapher(grapher);
        final Node node = getMainNode();
        node.setCursor(Cursor.WAIT);
        final GetTypesTask task = new GetTypesTask(textAnController.getClient());
        task.setOnSucceeded(e -> {
            final ObservableList<ObjectType> types =
                    FXCollections.observableArrayList(task.getValue());
            typeComboBox.setItems(types);
            node.setCursor(Cursor.DEFAULT);
            filter();
        });
        task.setOnFailed(e -> {
            node.setCursor(Cursor.DEFAULT);
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "page.load.error"))
                        .showException(task.getException());
                closeContainer();
            });
        });
        new Thread(task, "Grapher").start();
    }

    @Override
    public void setSettings(final Properties settings) {
        super.setSettings(settings);
        perPageComboBox.setValue(Integer.parseInt(settings.getProperty("objects.per.page", "25")));
        perPageComboBox.valueProperty().addListener((ov, oldVal, newVal) -> {
            pageNo = 0;
            settings.setProperty("objects.per.page", newVal.toString());
            filter();
        });
    }

    @Override
    public void setTextAnController(final TextAnController textAnController) {
        super.setTextAnController(textAnController);
        contextMenu = new ObjectContextMenu(textAnController);
        contextMenu.objectProperty().bind(table.getSelectionModel().selectedItemProperty());
    }
}
