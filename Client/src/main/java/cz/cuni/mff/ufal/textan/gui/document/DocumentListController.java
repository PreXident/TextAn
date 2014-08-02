package cz.cuni.mff.ufal.textan.gui.document;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Client.Processed;
import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import static cz.cuni.mff.ufal.textan.gui.Utils.CONTEXT_MENU_STYLE;
import cz.cuni.mff.ufal.textan.gui.Window;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class DocumentListController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Document Viewer";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "documents.viewer";

    @FXML
    public BorderPane root;

    @FXML
    public GridPane filterPane;

    @FXML
    public CheckBox processedCheckBox;

    @FXML
    public TextField filterField;

    @FXML
    public Button newButton;

    @FXML
    public TableView<Document> table;

    @FXML
    public TableColumn<Document, Number> idColumn;

    @FXML
    public TableColumn<Document, Date> addTimeColumn;

    @FXML
    public TableColumn<Document, Date> lastChangeTimeColumn;

    @FXML
    public TableColumn<Document, Boolean> processedColumn;

    @FXML
    public TableColumn<Document, Date> processTimeColumn;

    @FXML
    public TableColumn<Document, Number> countColumn;

    @FXML
    public TableColumn<Document, String> textColumn;

    @FXML
    public ComboBox<Integer> perPageComboBox;

    @FXML
    public Label paginationLabel;

    /** Context menu for documents. */
    public ContextMenu contextMenu = new ContextMenu();

    /** Menu item for processing the document. */
    public MenuItem processMI;

    /** Menu item for editing the document. */
    public MenuItem editMI;

    /** Localization container. */
    ResourceBundle resourceBundle;

    /** Application controller. */
    TextAnController textAnController;

    /** Client for communication with server. */
    Client client;

    /** Object to filter the documents. */
    protected Object object = null;

    /** Object to filter the documents. */
    protected Relation relation = null;

    /** Number of displayed page. */
    protected int pageNo = 0;

    /** Number of pages. */
    protected int pageCount = 0;

    /** Number of objects fullfilling the filter. */
    protected int objectCount = 0;

    /** Synchronization lock. */
    protected Semaphore lock = new Semaphore(1);

    @FXML
    public void fastForward() {
        pageNo = pageCount - 1;
        filter();
    }

    @FXML
    public void fastRewind() {
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
            final String filter = filterField.getText();
            final Processed processed = processedCheckBox.isIndeterminate()
                    ? Processed.BOTH
                    : processedCheckBox.isSelected() ? Processed.YES : Processed.NO;
            final Task<Pair<List<Document>, Integer>> task = new Task<Pair<List<Document>, Integer>>() {
                @Override
                protected Pair<List<Document>, Integer> call() throws Exception {
                    Pair<List<Document>, Integer> pair;
                    if (object != null) {
                        pair = client.getDocumentsList(object, filter, first, size);
                    } else if (relation != null) {
                        pair = client.getDocumentsList(relation, filter, first, size);
                    } else {
                        pair = client.getDocumentsList(processed, filter, first, size);
                    }
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
                if (pageCount == 0) {
                    pageCount = 1;
                }
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
    public void forward() {
        if (pageNo < pageCount - 1) {
            ++pageNo;
            filter();
        }
    }

    @FXML
    public void newDocument() {
        textAnController.newDocument();
    }

    @FXML
    public void rewind() {
        if (pageNo > 0) {
            --pageNo;
            filter();
        }
    }

    @Override
     public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
        final MenuItem showMI = new MenuItem(Utils.localize(resourceBundle, "document.show"));
        showMI.setOnAction(e -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            if (doc != null) {
                textAnController.displayDocument(doc);
            }
        });
        contextMenu.getItems().add(showMI);
        final MenuItem graphMI = new MenuItem(Utils.localize(resourceBundle, "document.graph"));
        graphMI.setOnAction(e -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            if (doc != null) {
                textAnController.displayGraph(doc);
            }
        });
        contextMenu.getItems().add(graphMI);
        processMI = new MenuItem(Utils.localize(resourceBundle, "document.process"));
        processMI.setOnAction(e -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            if (doc != null) {
                textAnController.processDocument(doc);
            }
        });
        processMI.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            return doc != null ? doc.isProcessed() : true;
        }, table.getSelectionModel().selectedItemProperty()));
        contextMenu.getItems().add(processMI);
        editMI = new MenuItem(Utils.localize(resourceBundle, "document.edit"));
        editMI.setOnAction(e -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            if (doc != null) {
                textAnController.editDocument(doc);
            }
        });
        editMI.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            final Document doc = table.getSelectionModel().getSelectedItem();
            return doc != null ? doc.isProcessed() : true;
        }, table.getSelectionModel().selectedItemProperty()));
        contextMenu.getItems().add(editMI);
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
        final DateToStringConverter dateConverter = new DateToStringConverter(Utils.localize(resourceBundle, "date.format", "YYYY-MM-dd hh:mm:ss"));
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
        processTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Document, Date> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getProcessTime()));
        processTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));
        countColumn.setCellValueFactory((TableColumn.CellDataFeatures<Document, Number> p) -> new ReadOnlyIntegerWrapper(p.getValue().getCount()));
        countColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return t != null ? t.toString() : "";
            }
            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        table.getColumns().remove(countColumn); //this will be readded if object or relation is set
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
     * Sets object to filter documents.
     * @param object new object
     */
    public void setObject(final Object object) {
        this.object = object;
        convertToOccurrenceList(Utils.localize(resourceBundle, PROPERTY_ID) + " - " + Utils.shortString(object.toString()));
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
     * Sets relation to filter documents.
     * @param relation new relation
     */
    public void setRelation(final Relation relation) {
        this.relation = relation;
        convertToOccurrenceList(Utils.localize(resourceBundle, PROPERTY_ID) + " - " + Utils.shortString(relation.toString() + ": " + relation.getAnchorString()));
    }

    /**
     * Sets application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
    }

    /**
     * Converts the list window to occurrence list for an object/relation.
     * @param title new title
     */
    protected void convertToOccurrenceList(final String title) {
        final Window w = window != null ? window : stage.getInnerWindow();
        Platform.runLater(() -> {
            filterPane.getChildren().remove(processedCheckBox);
            table.getColumns().add(table.getColumns().size() - 1, countColumn);
            textColumn.prefWidthProperty().bind(table.widthProperty().add(idColumn.widthProperty().add(addTimeColumn.widthProperty()).add(lastChangeTimeColumn.widthProperty()).add(processedColumn.widthProperty()).add(processTimeColumn.widthProperty()).add(countColumn.widthProperty()).multiply(-1).add(-30)));
            w.setTitle(title);
        });
    }

    /**
     * Simple date to string converter.
     */
    static class DateToStringConverter extends StringConverter<Date> {

        private final DateFormat format;

        public DateToStringConverter(final String format) {
            this.format = new SimpleDateFormat(format);
        }

        @Override
        public String toString(Date date) {
            return date != null ? format.format(date) : "";
        }

        @Override
        public Date fromString(String string) {
            try {
                return format.parse(string);
            } catch (ParseException e) {
                return null;
            }
        }
    }
}
