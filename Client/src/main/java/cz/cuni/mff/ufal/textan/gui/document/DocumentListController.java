package cz.cuni.mff.ufal.textan.gui.document;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.Utils.CONTEXT_MENU_STYLE;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class DocumentListController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Document Viewer";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "document.viewer";

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Document> table;

    @FXML
    private TableColumn<Document, Number> idColumn;

    @FXML
    private TableColumn<Document, Date> addTimeColumn;

    @FXML
    private TableColumn<Document, Date> lastChangeTimeColumn;

    @FXML
    private TableColumn<Document, Boolean> processedColumn;

    @FXML
    private TableColumn<Document, Date> processTimeColumn;

    @FXML
    private TableColumn<Document, String> textColumn;

    @FXML
    private ComboBox<Integer> perPageComboBox;

    @FXML
    private Label paginationLabel;

    /** Context menu for documents. */
    protected ContextMenu contextMenu = new ContextMenu();

    /** Localization container. */
    ResourceBundle resourceBundle;

    /** Application controller. */
    TextAnController textAnController;

    /** Client for communication with server. */
    Client client;

    /** Object id to filter the documents. */
    protected long objectId;

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
    public void filter() {
        if (lock.tryAcquire()) {
            final Node node = getMainNode();
            node.setCursor(Cursor.WAIT);
            final int size = perPageComboBox.getValue();
            final int first = perPageComboBox.getValue() * pageNo;
            final Task<Pair<List<Document>, Integer>> task = new Task<Pair<List<Document>, Integer>>() {
                @Override
                protected Pair<List<Document>, Integer> call() throws Exception {
                    Pair<List<Document>, Integer> pair =
                            client.getDocumentsList(objectId, first, size);
                    pair.getFirst().sort((doc1, doc2) -> Long.compare(doc1.getId(), doc2.getId()));
                    return pair;
                }
            };
            task.setOnSucceeded(e -> {
                Pair<List<Document>, Integer> pair = task.getValue();
                table.getItems().clear();
                table.getItems().addAll(FXCollections.observableList(pair.getFirst()));
                objectCount = pair.getSecond();
                pageCount = (int) Math.ceil(1.0 * pair.getSecond() / size);
                final String format = Utils.localize(resourceBundle, "pagination.label");
                paginationLabel.setText(String.format(format, pageNo + 1, pageCount));
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
        this.resourceBundle = rb;
        final MenuItem graphMI = new MenuItem(Utils.localize(resourceBundle, "document.show"));
        graphMI.setOnAction(e -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            if (doc != null) {
                //TODO display new window containing the document
            }
        });
        contextMenu.getItems().add(graphMI);
        final MenuItem processMI = new MenuItem(Utils.localize(resourceBundle, "document.process"));
        processMI.setOnAction(e -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            if (doc != null) {
                //TODO display new window to process the document
            }
        });
        processMI.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            return doc != null ? doc.isProcessed() : true;
        }, table.getSelectionModel().selectedItemProperty()));
        contextMenu.getItems().add(processMI);
        contextMenu.setStyle(CONTEXT_MENU_STYLE);
        contextMenu.setConsumeAutoHidingEvents(false);
        table.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal != null) {
                table.setContextMenu(contextMenu);
            } else {
                table.setContextMenu(null);
            }
        });
        table.setRowFactory((TableView<Document> tv) -> {
            return new TableRow<Document>() {
                /** Tooltip displaying full document text. */
                protected Tooltip tooltip = new Tooltip();

                {
                    tooltip.setWrapText(true);
                    tooltip.setMaxWidth(500);
                    itemProperty().addListener((ov, oldVal, newVal) -> {
                        if (newVal != null) {
                            tooltip.setText(newVal.getText());
                            setTooltip(tooltip);
                        } else {
                            setTooltip(null);
                        }
                    });
                }
            };
        });
        textColumn.prefWidthProperty().bind(table.widthProperty().add(idColumn.widthProperty().add(addTimeColumn.widthProperty()).add(lastChangeTimeColumn.widthProperty()).add(processedColumn.widthProperty()).add(processTimeColumn.widthProperty()).multiply(-1).add(-30)));
        idColumn.setCellValueFactory((TableColumn.CellDataFeatures<Document, Number> p) -> new ReadOnlyLongWrapper(p.getValue().getId()));
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
        final DateToStringConverter dateConverter = new DateToStringConverter();
        addTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Document, Date> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getAddTime()));
        addTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));
        lastChangeTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Document, Date> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getLastChangeTime()));
        lastChangeTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));
        processedColumn.setCellValueFactory((TableColumn.CellDataFeatures<Document, Boolean> p) -> new ReadOnlyObjectWrapper<>(p.getValue().isProcessed()));
        processedColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Boolean>() {
            @Override
            public String toString(Boolean t) {
                return t ? Utils.localize(resourceBundle, "table.true") : Utils.localize(resourceBundle, "table.false");
            }
            @Override
            public Boolean fromString(String string) {
                return Utils.localize(resourceBundle, "table.true").equals(string);
            }
        }));
        processTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Document, Date> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getLastChangeTime()));
        processTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));
        textColumn.setCellValueFactory((TableColumn.CellDataFeatures<Document, String> p) -> new ReadOnlyStringWrapper(p.getValue().getText()));
        textColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Sets client to communicate with the server.
     * @param client new client to communicate with the server
     */
    public void setClient(final Client client) {
        this.client = client;
    }

    /**
     * Sets object id to filter documents.
     * @param objectId new object id
     */
    public void setObjectId(final long objectId) {
        this.objectId = objectId;
    }

    @Override
    public void setSettings(final Properties settings) {
        super.setSettings(settings);
        perPageComboBox.setValue(Integer.parseInt(settings.getProperty("documents.per.page", "25")));
        perPageComboBox.valueProperty().addListener((ov, oldVal, newVal) -> {
            pageNo = 0;
            settings.setProperty("documents.per.page", newVal.toString());
            filter();
        });
    }

    /**
     * Sets application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
    }

    /**
     * Simple date to string converter.
     */
    static class DateToStringConverter extends StringConverter<Date> {

        @Override
        public String toString(Date t) {
            return t != null ? t.toString() : "";
        }

        @Override
        public Date fromString(String string) {
            @SuppressWarnings("deprecation") //this should never happen anyway
            final Date result = new Date(string);
            return result;
        }
    }
}
