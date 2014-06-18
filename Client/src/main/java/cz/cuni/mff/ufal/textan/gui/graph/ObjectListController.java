package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.Semaphore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class ObjectListController extends GraphController {

    @FXML
    private BorderPane root;

//    @FXML
//    private BigDecimalField distanceField;

    @FXML
    private ListView<Object> listView;

    @FXML
    private ComboBox<ObjectType> typeComboBox;

    @FXML
    private TextField filterField;

    @FXML
    private ComboBox<Integer> perPageComboBox;

    /** Context menu for objects. */
    protected ContextMenu contextMenu = new ContextMenu();

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
            final Node node = getMainNode();
            node.setCursor(Cursor.WAIT);
            final ObjectType selectedType = typeComboBox.getValue();
            final String filter = filterField.getText();
            final int size = perPageComboBox.getValue();
            final int first = perPageComboBox.getValue() * pageNo;
            final Task<Pair<List<Object>, Integer>> task = new Task<Pair<List<Object>, Integer>>() {
                @Override
                protected Pair<List<Object>, Integer> call() throws Exception {
                    final Client client = grapher.getClient();
                    Pair<List<Object>, Integer> pair =
                            client.getObjectsList(selectedType, filter, first, size);
                    //FIXME: is the comparison right?
                    pair.getFirst().sort((obj1, obj2) -> Long.compare(obj1.getId(), obj2.getId()));
                    return pair;
                }
            };
            task.setOnSucceeded(e -> {
                Pair<List<Object>, Integer> pair = task.getValue();
                listView.getItems().clear();
                listView.getItems().addAll(FXCollections.observableList(pair.getFirst()));
                objectCount = pair.getSecond();
                pageCount = (int) Math.ceil(1.0 * pair.getSecond() / size);

                node.setCursor(Cursor.DEFAULT);
                lock.release();
            });
            task.setOnFailed(e -> {
                node.setCursor(Cursor.DEFAULT);
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title(Utils.localize(resourceBundle, "page.load.error"))
                            .showException(task.getException());
                });
                lock.release();
            });
            new Thread(task, "Filter").start();
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
        super.initialize(url, rb);
        final MenuItem graphMI = new MenuItem(Utils.localize(resourceBundle, "graph.show"));
        graphMI.setOnAction(e -> {
            final Object obj = listView.getSelectionModel().getSelectedItem();
            if (obj != null) {
                textAnController.displayGraph(obj.getId());
            }
        });
        contextMenu.getItems().add(graphMI);
        listView.setCellFactory((ListView<Object> p) -> {
            return new ListCell<Object>() {
                @Override
                protected void updateItem(Object obj, boolean bln) {
                    super.updateItem(obj, bln);
                    if (obj != null) {
                        setText(obj.getId() + " - " + String.join(",", obj.getAliases()));
                        setContextMenu(contextMenu);
                    } else {
                        setText("");
                        setContextMenu(null);
                    }
                }
            };
        });
        typeComboBox.valueProperty().addListener((ov, oldVal, newVal) -> {
            pageNo = 0;
        });
    }

    @Override
    public void setGrapher(final Grapher grapher) {
        super.setGrapher(grapher);
        final Node node = getMainNode();
        node.setCursor(Cursor.WAIT);
        final GetTypesTask task = new GetTypesTask();
        task.setOnSucceeded(e -> {
            final ObservableList<ObjectType> types =
                    FXCollections.observableArrayList(task.types);
            types.add(0, null);
            typeComboBox.setItems(types);
            typeComboBox.setValue(task.selectedType);
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

    /**
     * Simple task to get information for object list.
     * After run, fields are filled.
     */
    class GetTypesTask extends Task<Void> {

        /** List of all object types. */
        List<ObjectType> types;

        /** Selected type. */
        ObjectType selectedType = null;

        @Override
        protected Void call() throws Exception {
            types = grapher.getClient().getObjectTypesList();
            final Collator collator = Collator.getInstance();
            types.sort((type1, type2) -> collator.compare(type1.getName(), type2.getName()));
            return null;
        }
    }
}
