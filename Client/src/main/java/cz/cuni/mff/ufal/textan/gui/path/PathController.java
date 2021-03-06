package cz.cuni.mff.ufal.textan.gui.path;

import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.graph.PathGrapher;
import cz.cuni.mff.ufal.textan.gui.GetTypesTask;
import cz.cuni.mff.ufal.textan.gui.ObjectContextMenu;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import jfxtras.labs.scene.control.BigDecimalField;

/**
 * Controls joining of two objects.
 */
public class PathController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Path Wizard";

    /** Identifier used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "path.wizard";

    /** Minimal height of the path window. */
    static protected final int MIN_HEIGHT = 500;

    /** Minimal width of the path window. */
    static protected final int MIN_WIDTH = 570;

    @FXML
    private BorderPane root;

    @FXML
    private BigDecimalField distanceField;

    @FXML
    private TableView<Object> leftTable;

    @FXML
    private TableColumn<Object, Number> leftIdColumn;

    @FXML
    private TableColumn<Object, ObjectType> leftTypeColumn;

    @FXML
    private TableColumn<Object, String> leftAliasColumn;

    @FXML
    private TextField leftFilterField;

    @FXML
    private ComboBox<Integer> leftPerPageComboBox;

    @FXML
    private Label leftPaginationLabel;

    @FXML
    private TableView<Object> rightTable;

    @FXML
    private TableColumn<Object, Number> rightIdColumn;

    @FXML
    private TableColumn<Object, ObjectType> rightTypeColumn;

    @FXML
    private TableColumn<Object, String> rightAliasColumn;

    @FXML
    private TextField rightFilterField;

    @FXML
    private ComboBox<Integer> rightPerPageComboBox;

    @FXML
    private Label rightPaginationLabel;

    @FXML
    private ComboBox<ObjectType> leftTypeComboBox;

    @FXML
    private ComboBox<ObjectType> rightTypeComboBox;

    @FXML
    private Button pathButton;

    /** Localization controller. */
    private ResourceBundle resourceBundle;

    /** Application controller. */
    private TextAnController textAnController;

    /** Context menu for left object list. */
    protected ObjectContextMenu leftContextMenu;

    /** Context menu for right object list. */
    protected ObjectContextMenu rightContextMenu;

    /** Number of displayed page. */
    protected int leftPageNo = 0;

    /** Number of pages. */
    protected int leftPageCount = 0;

    /** Number of objects fullfilling the filter. */
    protected int leftObjectCount = 0;

    /** Number of displayed page. */
    protected int rightPageNo = 0;

    /** Number of pages. */
    protected int rightPageCount = 0;

    /** Number of objects fullfilling the filter. */
    protected int rightObjectCount = 0;

    /** Synchronization lock. */
    protected Semaphore leftLock = new Semaphore(1);

    /** Synchronization lock. */
    protected Semaphore rightLock = new Semaphore(1);

    /** Synchronization lock. */
    protected Semaphore lock = new Semaphore(1);

    @FXML
    private void path() {
        final Object leftObject = leftTable.getSelectionModel().getSelectedItem();
        final Object rightObject = rightTable.getSelectionModel().getSelectedItem();
        final int distance = distanceField.getNumber().intValue();
        if (leftObject == null || rightObject == null || leftObject.equals(rightObject)) {
            return;
        }
        if (lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            final PathGrapher grapher =
                    new PathGrapher(textAnController.getClient());
            grapher.setRootId(leftObject.getId());
            grapher.setTargetId(rightObject.getId());
            grapher.setDistance(distance);

            final Task<Graph> task = new Task<Graph>() {
                @Override
                protected Graph call() throws Exception {
                    return grapher.getGraph();
                }
            };
            task.setOnSucceeded(e -> {
                if (task.getValue().getNodes().size() < 2) {
                    getMainNode().setCursor(Cursor.DEFAULT);
                    createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "path.none.title"))
                        .message(Utils.localize(resourceBundle, "path.none.message"))
                        .showInformation();
                } else {
                    closeContainer();
                    textAnController.displayGraph(grapher);
                }
                lock.release();
            });
            task.setOnFailed(e -> {
                task.getException().printStackTrace();
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "path.error"))
                        .showException(task.getException());
                lock.release();
            });
            new Thread(task, "ObjectPath").start();
        }
    }

    @FXML
    private void leftFastForward() {
        leftPageNo = leftPageCount - 1;
        leftFilter();
    }

    @FXML
    private void leftFastRewind() {
        leftPageNo = 0;
        leftFilter();
    }

    @FXML
    private void leftFilter() {
        Utils.filterObjects(textAnController.getClient(), this, leftLock,
                getMainNode(), root, leftTypeComboBox.getValue(),
                leftFilterField.getText(), leftPerPageComboBox.getValue(),
                leftPageNo, resourceBundle, leftPaginationLabel,
                (objectCount, pageCount) -> {
                    leftObjectCount = objectCount;
                    leftPageCount = pageCount;
                }, leftTable.getItems());
    }

    @FXML
    private void leftForward() {
        if (leftPageNo < leftPageCount - 1) {
            ++leftPageNo;
            leftFilter();
        }
    }

    @FXML
    private void leftRefresh() {
        leftPageNo = 0;
        leftFilter();
    }

    @FXML
    private void leftRewind() {
        if (leftPageNo > 0) {
            --leftPageNo;
            leftFilter();
        }
    }

    @FXML
    private void rightFastForward() {
        rightPageNo = rightPageCount - 1;
        rightFilter();
    }

    @FXML
    private void rightFastRewind() {
        rightPageNo = 0;
        rightFilter();
    }

    @FXML
    private void rightFilter() {
        Utils.filterObjects(textAnController.getClient(), this, rightLock,
                getMainNode(), root, rightTypeComboBox.getValue(),
                rightFilterField.getText(), rightPerPageComboBox.getValue(),
                rightPageNo, resourceBundle, rightPaginationLabel,
                (objectCount, pageCount) -> {
                    rightObjectCount = objectCount;
                    rightPageCount = pageCount;
                }, rightTable.getItems());
    }

    @FXML
    private void rightForward() {
        if (rightPageNo < rightPageCount - 1) {
            ++rightPageNo;
            rightFilter();
        }
    }

    @FXML
    private void rightRefresh() {
        rightPageNo = 0;
        rightFilter();
    }

    @FXML
    private void rightRewind() {
        if (rightPageNo > 0) {
            --rightPageNo;
            rightFilter();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
        leftTable.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal != null) {
                leftTable.setContextMenu(leftContextMenu);
            } else {
                leftTable.setContextMenu(null);
            }
        });
        leftAliasColumn.prefWidthProperty().bind(leftTable.widthProperty().add(leftIdColumn.widthProperty().add(leftTypeColumn.widthProperty()).multiply(-1).add(-30)));
        leftIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, Number> p) -> new ReadOnlyLongWrapper(p.getValue().getId()));
        leftIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return t != null ? t.toString() : "";
            }
            @Override
            public Number fromString(String string) {
                return Long.parseLong(string);
            }
        }));
        leftTypeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, ObjectType> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getType()));
        leftTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<ObjectType>() {
            @Override
            public String toString(ObjectType t) {
                return t != null ? t.getName() : "";
            }
            @Override
            public ObjectType fromString(String string) {
                throw new UnsupportedOperationException("This should never happan!");
            }
        }));
        leftAliasColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, String> p) -> new ReadOnlyStringWrapper(p.getValue().getAliasString()));
        leftAliasColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        leftFilterField.textProperty().addListener((o) -> {
            leftPageNo = 0;
        });
        leftFilterField.setOnAction(e -> {
            leftFilter();
        });
        //
        rightTable.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal != null) {
                rightTable.setContextMenu(rightContextMenu);
            } else {
                rightTable.setContextMenu(null);
            }
        });
        rightAliasColumn.prefWidthProperty().bind(rightTable.widthProperty().add(rightIdColumn.widthProperty().add(rightTypeColumn.widthProperty()).multiply(-1).add(-30)));
        rightIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, Number> p) -> new ReadOnlyLongWrapper(p.getValue().getId()));
        rightIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return t != null ? t.toString() : "";
            }
            @Override
            public Number fromString(String string) {
                return Long.parseLong(string);
            }
        }));
        rightTypeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, ObjectType> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getType()));
        rightTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<ObjectType>() {
            @Override
            public String toString(ObjectType t) {
                return t != null ? t.getName() : "";
            }
            @Override
            public ObjectType fromString(String string) {
                throw new UnsupportedOperationException("This should never happan!");
            }
        }));
        rightAliasColumn.setCellValueFactory((TableColumn.CellDataFeatures<Object, String> p) -> new ReadOnlyStringWrapper(p.getValue().getAliasString()));
        rightAliasColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        rightFilterField.textProperty().addListener((o) -> {
            rightPageNo = 0;
        });
        rightFilterField.setOnAction(e -> {
            rightFilter();
        });
        //
        pathButton.prefWidthProperty().bind(root.widthProperty());
    }

    @Override
    public void setSettings(final Properties settings) {
        super.setSettings(settings);
        leftPerPageComboBox.setValue(Integer.parseInt(settings.getProperty("objects.per.page.left", "25")));
        leftPerPageComboBox.valueProperty().addListener((ov, oldVal, newVal) -> {
            leftPageNo = 0;
            settings.setProperty("objects.per.page.left", newVal.toString());
            leftFilter();
        });
        //
        rightPerPageComboBox.setValue(Integer.parseInt(settings.getProperty("objects.per.page.right", "25")));
        rightPerPageComboBox.valueProperty().addListener((ov, oldVal, newVal) -> {
            leftPageNo = 0;
            settings.setProperty("objects.per.page.right", newVal.toString());
            leftFilter();
        });
        //
        distanceField.setNumber(new BigDecimal(settings.getProperty("graph.distance", "5")));
    }

    /**
     * Sets application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
        leftContextMenu = new ObjectContextMenu(textAnController);
        leftContextMenu.objectProperty().bind(leftTable.getSelectionModel().selectedItemProperty());
        rightContextMenu = new ObjectContextMenu(textAnController);
        rightContextMenu.objectProperty().bind(rightTable.getSelectionModel().selectedItemProperty());
        //
        final Node node = getMainNode();
        node.setCursor(Cursor.WAIT);
        final GetTypesTask task = new GetTypesTask(textAnController.getClient());
        task.setOnSucceeded(e -> {
            final List<ObjectType> types = task.getValue();
            leftTypeComboBox.getItems().clear();
            leftTypeComboBox.getItems().addAll(types);
            rightTypeComboBox.getItems().clear();
            rightTypeComboBox.getItems().addAll(types);
            node.setCursor(Cursor.DEFAULT);
            leftFilter();
            rightFilter();
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
        new Thread(task, "GetTypes").start();
    }
}
