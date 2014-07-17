package cz.cuni.mff.ufal.textan.gui.relation;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.Utils.CONTEXT_MENU_STYLE;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.net.URL;
import java.text.Collator;
import java.util.Arrays;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class RelationListController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Relation List";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "relation.view";

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Relation> table;

    @FXML
    private TableColumn<Relation, Number> idColumn;

    @FXML
    private TableColumn<Relation, RelationType> typeColumn;

    @FXML
    private TableColumn<Relation, String> aliasColumn;

    @FXML
    private TableColumn<Relation, Relation> objectsColumn;

    @FXML
    private ComboBox<RelationType> typeComboBox;

    @FXML
    private TextField filterField;

    @FXML
    private ComboBox<Integer> perPageComboBox;

    @FXML
    private Label paginationLabel;

    /** Localizatio container. */
    protected ResourceBundle resourceBundle;

    /** Application controller. */
    protected TextAnController textAnController;

    /** Context menu for relations. */
    protected ContextMenu contextMenu;

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
        if (lock.tryAcquire()) {
            final Node mainNode = getMainNode();
            mainNode.setCursor(Cursor.WAIT);
            final Client client = textAnController.getClient();
            final int first = perPageComboBox.getValue() * pageNo;
            final RelationType selectedType = typeComboBox.getValue();
            final String filter = filterField.getText();
            final int size = perPageComboBox.getValue();
            final Task<Pair<List<Relation>, Integer>> task = new Task<Pair<List<Relation>, Integer>>() {
                @Override
                protected Pair<List<Relation>, Integer> call() throws Exception {
                    Pair<List<Relation>, Integer> pair =
                            client.getRelationList(selectedType, filter, first, size);
                    pair.getFirst().sort((obj1, obj2) -> Long.compare(obj1.getId(), obj2.getId()));
                    return pair;
                }
            };
            task.setOnSucceeded(e -> {
                final Pair<List<Relation>, Integer> pair =
                        task.getValue();
                table.getItems().clear();
                table.getItems().addAll(FXCollections.observableList(pair.getFirst()));
                objectCount = pair.getSecond();
                pageCount = (int) Math.ceil(1.0 * pair.getSecond() / size);
                final String format = Utils.localize(resourceBundle, "pagination.label");
                paginationLabel.setText(String.format(format, pageNo + 1, pageCount));
                mainNode.setCursor(Cursor.DEFAULT);
                lock.release();
            });
            task.setOnFailed(e -> {
                mainNode.setCursor(Cursor.DEFAULT);
                lock.release();
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title(Utils.localize(resourceBundle, "page.load.error"))
                            .showException(task.getException());
                });
                lock.release();
            });
            new Thread(task, "ObjectFilter").start();
        }
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
        resourceBundle = rb;
        table.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal != null) {
                table.setContextMenu(contextMenu);
            } else {
                table.setContextMenu(null);
            }
        });
        objectsColumn.prefWidthProperty().bind(table.widthProperty().add(idColumn.widthProperty().add(typeColumn.widthProperty()).add(aliasColumn.widthProperty()).multiply(-1).add(-30)));
        idColumn.setCellValueFactory((TableColumn.CellDataFeatures<Relation, Number> p) -> new ReadOnlyLongWrapper(p.getValue().getId()));
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
        typeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Relation, RelationType> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getType()));
        typeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<RelationType>() {
            @Override
            public String toString(RelationType t) {
                return t != null ? t.getName() : "";
            }
            @Override
            public RelationType fromString(String string) {
                throw new UnsupportedOperationException("This should never happan!");
            }
        }));
        aliasColumn.setCellValueFactory((TableColumn.CellDataFeatures<Relation, String> p) -> new ReadOnlyStringWrapper(p.getValue().getAnchorString()));
        aliasColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        objectsColumn.setCellValueFactory((TableColumn.CellDataFeatures<Relation, Relation> p) -> new ReadOnlyObjectWrapper<>(p.getValue()));
        objectsColumn.setCellFactory(column -> {
            return new TableCell<Relation, Relation>() {
                @Override
                protected void updateItem(final Relation rel, final boolean empty) {
                    if (empty || rel == null) {
                        this.setGraphic(null);
                    } else {
                        final RelationTreeView tree =
                                new RelationTreeView(Arrays.asList(rel));
                        tree.setTextAnController(textAnController);
                        tree.setAutoSize(true);
                        this.setGraphic(tree);
                    }
                }
            };
        });
        //
        typeComboBox.valueProperty().addListener((ov, oldVal, newVal) -> {
            pageNo = 0;
        });
        filterField.textProperty().addListener((o) -> {
            pageNo = 0;
        });
        filterField.setOnAction(e -> {
            filter();
        });
        //
        contextMenu = new ContextMenu();
        final MenuItem graphMI = new MenuItem(Utils.localize(resourceBundle, "graph.show"));
        graphMI.setOnAction(e -> {
            final Relation rel = table.getSelectionModel().getSelectedItem();
            if (rel != null) {
                textAnController.displayGraph(rel);
            }
        });
        final MenuItem documentMI = new MenuItem(Utils.localize(resourceBundle, "document.show"));
        documentMI.setOnAction(e -> {
            final Relation rel = table.getSelectionModel().getSelectedItem();
            if (rel != null) {
                textAnController.displayDocuments(rel);
            }
        });
        contextMenu.getItems().add(documentMI);
        contextMenu.getItems().add(graphMI);
        contextMenu.setStyle(CONTEXT_MENU_STYLE);
        contextMenu.setConsumeAutoHidingEvents(false);
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
        //
        final Node node = getMainNode();
        node.setCursor(Cursor.WAIT);
        final Task<List<RelationType>> task = new Task<List<RelationType>>() {
            @Override
            protected List<RelationType> call() throws Exception {
                List<RelationType> types = textAnController.getClient().getRelationTypesList();
                final Collator collator = Collator.getInstance();
                types.sort((type1, type2) -> collator.compare(type1.getName(), type2.getName()));
                types.add(0, null);
                return types;
            }
        };
        task.setOnSucceeded(e -> {
            final ObservableList<RelationType> types =
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

    /**
     * Sets application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
    }
}
