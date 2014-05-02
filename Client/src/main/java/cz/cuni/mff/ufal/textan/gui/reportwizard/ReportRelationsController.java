package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.processreport.AbstractBuilder.SplitException;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.reportwizard.FXRelationBuilder.RelationInfo;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Controls editing the report relations.
 */
public class ReportRelationsController extends ReportWizardController {

    /** Style class for selected words. */
    static final String SELECTED = "selected";

    /**
     * Adds clazz style class to all items in the list.
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
     * @param list items from which remove the style class
     */
    static void removeClass(final String clazz, final Iterable<? extends Node> list) {
        list.forEach(node -> node.getStyleClass().remove(clazz));
    }

    @FXML
    BorderPane root;

    @FXML
    TextFlow textFlow;

    @FXML
    ScrollPane scrollPane;

    @FXML
    TableView<RelationInfo> table;

    @FXML
    TableColumn<RelationInfo, Number> orderColumn;

    @FXML
    TableColumn<RelationInfo, Object> objectColumn;

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

    /** Context menu with entity selection. */
    ContextMenu contextMenu = new ContextMenu();

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Words with assigned EntitityBuilders. */
    List<Word> words;

    /** Currently selected relation. */
    FXRelationBuilder selectedRelation;

    /** Texts assigned to objects. */
    Map<Object, List<Text>> objectWords = new HashMap<>();

    /** TextField in ContextMenu with filter. */
    TextField filterField;

    /** List with all relation types. */
    ObservableList<RelationType> allTypes;

    /** ListView in ContextMenu with list of relation types. */
    ListView<RelationType> listView;

    /** Content of {@link #textFlow}. */
    List<Text> texts;

    @FXML
    private void add() {
        if (selectedRelation != null) {
            selectedRelation.getData().add(new RelationInfo(0, null));
        }
    }

    @FXML
    private void back() {
        pipeline.back();
    }

    @FXML
    private void cancel() {
        closeContainer();
    }

    @FXML
    private void next() {
        pipeline.setReportRelations(words);
    }

    @FXML
    private void remove() {
        if (selectedRelation != null) {
            final int index = table.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                final RelationInfo remove = selectedRelation.getData().remove(index);
                //remove selection background
                final RelationType type = selectedRelation.getType();
                final Object obj = remove.getObject();
                final List<Text> texts = objectWords.get(obj);
                if (texts != null) {
                    texts.forEach(Utils::unstyleTextBackground);
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty());
        table.setEditable(true);
        objectColumn.prefWidthProperty().bind(table.widthProperty().add(orderColumn.prefWidthProperty().multiply(-1).add(-2)));
        orderColumn.setCellValueFactory((CellDataFeatures<RelationInfo, Number> p) -> p.getValue().order);
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
        orderColumn.setOnEditCommit(
            (CellEditEvent<RelationInfo, Number> t) -> {
                t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).order.setValue(t.getNewValue());
        });
        objectColumn.setCellValueFactory((CellDataFeatures<RelationInfo, Object> p) -> p.getValue().object);
        objectColumn.setOnEditCommit(
            (CellEditEvent<RelationInfo, Object> t) -> {
                final Object oldObj = t.getOldValue();
                final List<Text> oldTexts = objectWords.get(oldObj);
                if (oldTexts != null) {
                    oldTexts.stream().forEach(Utils::unstyleTextBackground);
                }
                t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).object.setValue(t.getNewValue());
                final RelationType type = selectedRelation.getType();
                final long id = type.getId();
                final Object newObj = t.getNewValue();
                final List<Text> newTexts = objectWords.get(newObj);
                if (newTexts != null) {
                    newTexts.stream().forEach(txt -> Utils.styleTextBackground(txt, id));
                }
        });
        //create popup
        BorderPane border = new BorderPane();
        listView = new ListView<>();
        listView.setPrefHeight(100);
        border.setCenter(listView);
        filterField = new TextField();
        filterField.textProperty().addListener(e -> {
            listView.setItems(allTypes.filtered(t -> {
                final String filter = filterField.getText();
                if (filter == null || filter.isEmpty()) {
                    return true;
                }
                if (t == null) {
                    return false;
                }
                return t.getName().toLowerCase().contains(filter.toLowerCase());
            }));
        });
        filterField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN && listView.getItems().size() > 0) {
                listView.getSelectionModel().select(0);
                listView.requestFocus();
            }
        });
        filterField.setOnAction(ev -> {
            if (listView.getItems().size() == 1) {
                contextMenu.hide();
                final RelationType rt = listView.getItems().get(0);
                assignRelationToSelectedTexts(rt);
            }
        });
        border.setTop(filterField);
        contextMenu = new ContextMenu(new CustomMenuItem(border, true));
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        texts = new ArrayList<>();
        words = pipeline.getReportWords();
        for (final Word word: words) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                final long entityId = word.getEntity().getId();
                Utils.styleText(text, "ENTITY", entityId);
                //
                final int entityIndex = word.getEntity().getIndex();
                final Object obj = pipeline.getReportEntities().get(entityIndex).getCandidate();
                List<Text> objTexts = objectWords.get(obj);
                if (objTexts == null) {
                    objTexts = new ArrayList<>();
                    objectWords.put(obj, objTexts);
                }
                objTexts.add(text);
            }
            text.setOnMouseEntered((MouseEvent t) -> {
                if (word.getEntity() != null) {
                    final int entityIndex = word.getEntity().getIndex();
                    final Object obj = pipeline.getReportEntities().get(entityIndex).getCandidate();
                    if (obj != null) {
                        final String newTip = obj.toString();
                        tooltip.setText(newTip);
                        Bounds bounds = text.getLayoutBounds();
                        final Point2D p =text.localToScreen(bounds.getMaxX(), bounds.getMaxY());
                        tooltip.show(text, p.getX(), p.getY());
                    }
                } else {
                    tooltip.hide();
                }
            });
            text.setOnMouseExited((MouseEvent t) -> {
                tooltip.hide();
            });
            text.setOnMousePressed(e -> {
                clearSelectedRelationBackground();
                selectedRelation = null;
                if (!text.getStyleClass().contains(SELECTED) && word.getEntity() == null) {
                    removeSelectedClass(texts);
                    dragging = true;
                    firstDragged = texts.indexOf(text);
                    lastDragged = firstDragged;
                    firstSelectedIndex = firstDragged;
                    lastSelectedIndex = firstDragged;
                    text.getStyleClass().add(SELECTED);
                }
                if (word.getRelation() != null) {
                    clearSelectedRelationBackground();
                    selectedRelation = (FXRelationBuilder) word.getRelation();
                    final RelationType type = selectedRelation.getType();
                    final long id = type.getId();
                    selectedRelation.getData().stream()
                            .flatMap(relInfo -> objectWords.get(relInfo.object.get()).stream())
                            .forEach(t -> Utils.styleTextBackground(t, id));
                    table.setItems(selectedRelation.getData());
                } else {
                    clearSelectedRelationBackground();
                    selectedRelation = null;
                    table.setItems(null);
                }
            });
            text.setOnDragDetected(e -> {
                text.startFullDrag();
            });
            text.setOnMouseDragEntered(e -> {
                if (dragging) {
                    if (word.getEntity() != null) {
                        dragging = false;
                        for (int i = firstSelectedIndex; i <= lastSelectedIndex; ++i) {
                            texts.get(i).getStyleClass().remove(SELECTED);
                        }
                        return;
                    }
                    removeSelectedClass(texts);
                    final int myIndex = texts.indexOf(text);
                    final int min = Math.min(firstDragged, myIndex);
                    final int max = Math.max(firstDragged, myIndex);
                    addSelectedClass(texts.subList(min, max + 1));
                    firstSelectedIndex = min;
                    lastSelectedIndex = max;
                    if (!separators.contains(text.getText().charAt(0))) { //ignore separators in displaying the contextmenu
                        lastDragged = myIndex;
                    }
                }
            });
            text.setOnMouseReleased(e -> {
                if (dragging) {
                    dragging = false;
                    contextMenu.show(texts.get(lastDragged), Side.BOTTOM, 0, 0);
                    filterField.requestFocus();
                }
            });
            texts.add(text);
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(texts);
        //
        listView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                contextMenu.hide();
                final RelationType item =
                        listView.getSelectionModel().getSelectedItem();
                assignRelationToSelectedTexts(item);
            }
        });
        listView.setCellFactory(new Callback<ListView<RelationType>, ListCell<RelationType>>() {
            @Override
            public ListCell<RelationType> call(ListView<RelationType> p) {
                return new ListCell<RelationType>() {
                    {
                        this.setOnMouseClicked((MouseEvent t) -> {
                            contextMenu.hide();
                            @SuppressWarnings("unchecked")
                            final RelationType rt = ((ListCell<RelationType>) t.getSource()).getItem();
                            assignRelationToSelectedTexts(rt);
                        });
                    }
                    @Override
                    protected void updateItem(RelationType t, boolean empty) {
                        super.updateItem(t, empty);
                        if (empty) {
                            setText("");
                            return;
                        }
                        if (t != null) {
                            setText(t.getName());
                        } else {
                            setText(Utils.localize(resourceBundle, "relation.none"));
                        }
                    }
                };
            }
        });
        final List<RelationType> types = pipeline.getClient().getRelationTypesList();
        final Collator collator = Collator.getInstance();
        Collections.sort(types, (o1, o2) -> collator.compare(o1.getName(), o2.getName()));
        types.add(0, null); //None relation
        allTypes = FXCollections.observableArrayList(types);
        listView.setItems(allTypes);

        objectColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(
                pipeline.getReportEntities().stream()
                        .map(ent -> ent.getCandidate())
                        .distinct()
                        .collect(Collectors.toList())
        )));
    }

    /**
     * Assigns relation to selected texts.
     * @param relation RelationType to assign
     */
    protected void assignRelationToSelectedTexts(final RelationType relation) {
        if (relation == null) {
            for (int i = firstSelectedIndex; i <= lastSelectedIndex; ++i) {
                words.get(i).setRelation(null);
                Utils.unstyleText(texts.get(i));
            }
            return;
        }
        final FXRelationBuilder builder = new FXRelationBuilder(relation);
        try {
            Pair<Integer, Integer> bounds = builder.add(words, firstSelectedIndex, lastSelectedIndex, i -> Utils.unstyleText(texts.get(i)));
            for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
                Utils.styleText(texts.get(i), "RELATION", ~relation.getId());
            }
            clearSelectedRelationBackground();
            selectedRelation = builder;
            table.setItems(builder.getData());
        } catch (SplitException ex) {
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "error.split.entities"))
                        .showException(ex);
            });
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "error"))
                        .showException(ex);
            });
        }
    }

    /**
     * Clears background of the selected relation.
     */
    protected void clearSelectedRelationBackground() {
        if (selectedRelation != null) {
             selectedRelation.getData().stream()
                     .flatMap(relInfo -> objectWords.get(relInfo.object.get()).stream())
                     .forEach(Utils::unstyleTextBackground);
         }
    }
}
