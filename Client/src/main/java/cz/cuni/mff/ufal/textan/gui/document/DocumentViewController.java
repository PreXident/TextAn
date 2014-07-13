package cz.cuni.mff.ufal.textan.gui.document;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.DocumentData;
import cz.cuni.mff.ufal.textan.core.DocumentData.Occurrence;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;
import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.gui.ObjectContextMenu;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import cz.cuni.mff.ufal.textan.gui.reportwizard.*;
import cz.cuni.mff.ufal.textan.gui.reportwizard.FXRelationBuilder.RelationInfo;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * Controls editing the report relations.
 */
public class DocumentViewController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Document";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "document.viewer";

    /** Style class for selected words. */
    static final String SELECTED = "selected";

    /**
     * Adds clazz style class to all items in the list.
     * @param clazz style class to add
     * @param list items to which add the style class
     */
    static void addClass(final String clazz, final Iterable<? extends Node> list) {
        list.forEach(node -> node.getStyleClass().add(clazz));
    }

    /**
     * Adds {@link #SELECTED} style class to all items in the list.
     * @param list items to which add the style class
     */
    static void addSelectedClass(Iterable<? extends Node> list) {
        addClass(SELECTED, list);
    }

    /**
     * Removes {@link #SELECTED} style class from all items in the list.
     * @param list items from which remove the style class
     */
    static void removeSelectedClass(Iterable<? extends Node> list) {
        removeClass(SELECTED, list);
    }

    /**
     * Removes clazz style class from all items in the list.
     * @param clazz class to be added
     * @param list items from which remove the style class
     */
    static void removeClass(final String clazz, final Iterable<? extends Node> list) {
        list.forEach(node -> node.getStyleClass().remove(clazz));
    }

    /**
     * Splits report into words.
     * @param report report to split
     * @return list of words
     */
    static List<String> splitReportText(final String report) {
        int start = 0;
        final List<String> words = new ArrayList<>();
        for(int i = 0; i < report.length(); ++i) {
            if (separators.contains(report.charAt(i))) {
                if (start < i) {
                    final String s = report.substring(start, i);
                    words.add(s);
                }
                final String s = report.substring(i, i + 1);
                words.add(s);
                start = i + 1;
            }
        }
        if (start < report.length()) {
            final String s = report.substring(start, report.length());
            words.add(s);
        }
        return words;
    }

    @FXML
    BorderPane root;

    @FXML
    ScrollPane scrollPane;

    @FXML
    TextFlow textFlow;

    @FXML
    TableView<Triple<Integer, String, Object>> table;

    @FXML
    TableColumn<Triple<Integer, String, Object>, Number> orderColumn;

    @FXML
    TableColumn<Triple<Integer, String, Object>, String> roleColumn;

    @FXML
    TableColumn<Triple<Integer, String, Object>, Object> objectColumn;

    @FXML
    ListView<Relation> relationsListView;

    /** Application controller. */
    TextAnController textAnController;

    /** Client to communicate with the server. */
    Client client;

    /** Document itself. */
    Document document;

    /** Document data. */
    DocumentData documentData;

    /** Texts's tooltip. */
    Tooltip tooltip = new Tooltip("");

    /** Index of the first selected {@link Text} node. */
    int firstDragged = -1;

    /** Index of the lasty dragged {@link Text} node. */
    int lastDragged = -1;

    /** Index of the first selected {@link Text} node. */
    int firstSelectedIndex = -1;

    /** Index of the last selected {@link Text} node. */
    int lastSelectedIndex = -1;

    /** Flag indicating whether dragging is taking place. */
    boolean dragging = false;

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** List of document's words. */
    List<String> words;

    /** Currently selected relation. */
    Relation selectedRelation;

    /** Texts assigned to objects. */
    Map<Object, List<Text>> objectWords = new HashMap<>();

    /** Texts assigned to relations. */
    Map<Relation, List<Text>> relationWords = new HashMap<>();

    /** TextField in ContextMenu with filter. */
    TextField filterField;

    /** List with all relation types. */
    ObservableList<RelationType> allTypes;

    /** Content of {@link #textFlow}. */
    List<Text> texts;

    /** Context menu for objects in textFlow. */
    ObjectContextMenu objectContextMenu;

    /** Context menu for objects in table. */
    ObjectContextMenu tableContextMenu;

    /** Object to display graph for. */
    ObjectProperty<Object> objectForGraph = new SimpleObjectProperty<>();

    /** Synchronization lock. */
    final Semaphore lock = new Semaphore(1);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty().add(-20));
        scrollPane.vvalueProperty().addListener(e -> {
            textFlow.layoutChildren();
        });
        //
        table.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal != null) {
                table.setContextMenu(tableContextMenu);
            } else {
                table.setContextMenu(null);
            }
        });
        //
        objectColumn.prefWidthProperty().bind(table.widthProperty().add(orderColumn.prefWidthProperty().add(roleColumn.prefWidthProperty()) .multiply(-1).add(-2)));
        orderColumn.setCellValueFactory((CellDataFeatures<Triple<Integer, String, Object>, Number> t) -> new ReadOnlyIntegerWrapper(t.getValue().getFirst()));
        orderColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return t.toString();
            }
            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        roleColumn.setCellValueFactory((CellDataFeatures<Triple<Integer, String, Object>, String> t) -> new ReadOnlyStringWrapper(t.getValue().getSecond()));
        roleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        objectColumn.setCellValueFactory((CellDataFeatures<Triple<Integer, String, Object>, Object> t) -> new ReadOnlyObjectWrapper<>(t.getValue().getThird()));
    }

    /**
     * Sets new client to communicate with server.
     * @param client new client to communicate with server
     */
    public void setClient(final Client client) {
        this.client = client;
        fetchDocument();
    }

    /**
     * Sets document.
     * @param document new document;
     */
    public void setDocument(final Document document) {
        this.document = document;
        fetchDocument();
    }

    /**
     * Sets application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
        objectContextMenu = new ObjectContextMenu(textAnController);
        objectContextMenu.objectProperty().bind(objectForGraph);
        tableContextMenu = new ObjectContextMenu(textAnController);
        tableContextMenu.objectProperty().bind(objectForGraph);
    }

    /**
     * Clears background of the selected relation.
     */
    protected void clearSelectedRelationBackground() {
        if (selectedRelation != null) {
             selectedRelation.getObjects().stream()
                    .flatMap(triple -> {
                        final List<Text> words = objectWords.get(triple.getThird());
                        return words != null ? words.stream() : Stream.empty();
                    })
                    .forEach(Utils::unstyleTextBackground);
         }
    }

    /**
     * Fetches document data.
     */
    protected void fetchDocument() {
        if (client == null || document == null || !lock.tryAcquire()) {
            return;
        }
        final Task<DocumentData> task = new Task<DocumentData>() {
            @Override
            protected DocumentData call() throws Exception {
                return client.getDocumentData(document.getId());
            }
        };
        task.setOnSucceeded(e -> {
            documentData = task.getValue();
            relationsListView.setItems(FXCollections.observableArrayList(documentData.getRelations().values()));
            relationsListView.getSelectionModel().selectedItemProperty().addListener(
                    (ov, oldVal, newVal) -> { selectRelation(newVal); });
            //
            texts = new ArrayList<>();
            words = splitReportText(document.getText());
            final List<Occurrence> occurrences = documentData.getOccurrences();
            int pos = 0;
            int occIndex = 0;
            for (final String word: words) {
                final Text text = new Text(word);
                final int wordEnd = pos + word.length();
                while (occIndex < occurrences.size()
                        && occurrences.get(occIndex).last < pos) {
                    ++occIndex;
                }
                Object obj = null;
                Relation rel = null;
                if (occIndex < occurrences.size()) {
                    final Occurrence occ = occurrences.get(occIndex);
                    if (occ.position < wordEnd && pos < occ.last ) {
                        if (occ.type == DocumentData.OccurrenceType.OBJECT) {
                            obj = documentData.getObjects().get(occ.id);
                            List<Text> t = objectWords.get(obj);
                            if (t == null) {
                                t = new ArrayList<>();
                                objectWords.put(obj, t);
                            }
                            t.add(text);
                            Utils.styleText(text, "ENTITY", obj.getId());
                        } else /* if (occ.type == DocumentData.OccurrenceType.RELATION)*/ {
                            rel = documentData.getRelations().get(occ.id);
                            List<Text> t = relationWords.get(rel);
                            if (t == null) {
                                t = new ArrayList<>();
                                relationWords.put(rel, t);
                            }
                            t.add(text);
                            Utils.styleText(text, "RELATION", ~rel.getType().getId());
                        }
                    }
                }
                final Object finObj = obj;
                final Relation finRel = rel;
                text.setOnMouseEntered((MouseEvent t) -> {
                    if (finObj != null) {
                        final String newTip = finObj.toString();
                        tooltip.setText(newTip);
                        Bounds bounds = text.getLayoutBounds();
                        final Point2D p =text.localToScreen(bounds.getMaxX(), bounds.getMaxY());
                        tooltip.show(text, p.getX(), p.getY());
                    } else if (finRel != null) {
                        final String newTip = finRel.getType().toString();
                        tooltip.setText(newTip);
                        Bounds bounds = text.getLayoutBounds();
                        final Point2D p =text.localToScreen(bounds.getMaxX(), bounds.getMaxY());
                        tooltip.show(text, p.getX(), p.getY());
                    } else {
                        tooltip.hide();
                    }
                });
                text.setOnMouseExited((MouseEvent t) -> {
                    tooltip.hide();
                });
                text.setOnMouseClicked(ev -> {
                    clearSelectedRelationBackground();
                    selectedRelation = null;
                    relationsListView.getSelectionModel().select(-1);
                    if (!text.getStyleClass().contains(SELECTED) && finObj == null) {
                        removeSelectedClass(texts);
                        dragging = true;
                        firstDragged = texts.indexOf(text);
                        lastDragged = firstDragged;
                        firstSelectedIndex = firstDragged;
                        lastSelectedIndex = firstDragged;
                        text.getStyleClass().add(SELECTED);
                    }
                    if (finRel != null) {
                        selectRelation(finRel);
                        relationsListView.getSelectionModel().select(finRel);
                    } else {
                        clearSelectedRelationBackground();
                        selectedRelation = null;
                        table.getItems().clear();
                    }
                });
                if (finObj != null) {
                    text.setOnMousePressed(ev -> {
                        if (ev.isSecondaryButtonDown()) {
                            objectForGraph.set(finObj);
                            objectContextMenu.show(text, Side.BOTTOM, 0, 0);
                        }
                    });
                }
                texts.add(text);
                pos += word.length();
            }
            textFlow.getChildren().clear();
            textFlow.getChildren().addAll(texts);
            //
            lock.release();
            Utils.runFXlater(() -> textFlow.layoutChildren());
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            getMainNode().setCursor(Cursor.DEFAULT);
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "page.load.error"))
                        .showException(task.getException());
            });
            lock.release();
        });
        new Thread(task, "DocumentFetcher").start();
    }

    /**
     * Does all necessary steps to select given relation.
     * Clears backgrounds and old selection, selects relation's words,
     * prepares backgrounds, sets table content.
     * @param relation FXRelationBuilder to select
     */
    protected void selectRelation(final Relation relation) {
        clearSelectedRelationBackground();
        removeSelectedClass(texts);
        selectedRelation = relation;
        if (relation == null) {
            relationsListView.getSelectionModel().select(-1);
            table.getItems().clear();
            return;
        }
        final List<Text> relTexts = relationWords.get(relation);
        addSelectedClass(relTexts);
        final RelationType type = selectedRelation.getType();
        final long id = type.getId();
        selectedRelation.getObjects().stream()
                .flatMap(triple -> {
                    final List<Text> words = objectWords.get(triple.getThird());
                    return words != null ? words.stream() : Stream.empty();
                })
                .forEach(t -> Utils.styleTextBackground(t, id));
        table.getItems().clear();
        table.getItems().addAll(selectedRelation.getObjects());
    }
}
