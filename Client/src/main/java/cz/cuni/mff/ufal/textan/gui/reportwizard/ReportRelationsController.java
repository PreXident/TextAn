package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.utils.Ref;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.processreport.AbstractBuilder.IClearer;
import cz.cuni.mff.ufal.textan.core.processreport.AbstractBuilder.SplitException;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import static cz.cuni.mff.ufal.textan.gui.TextAnController.CLEAR_FILTERS;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.reportwizard.FXRelationBuilder.RelationInfo;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
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

    @FXML
    BorderPane root;

    @FXML
    ScrollPane scrollPane;

    @FXML
    TableView<RelationInfo> table;

    @FXML
    TableColumn<RelationInfo, Number> orderColumn;

    @FXML
    TableColumn<RelationInfo, Object> objectColumn;

    @FXML
    ListView<FXRelationBuilder> relationsListView;

    @FXML
    Slider slider;

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
            pipeline.resetStepsBack();
            selectedRelation.getData().add(new RelationInfo(0, null));
        }
    }

    @FXML
    private void addRelation() {
        clearSelectedRelationBackground();
        Ref<RelationType> relation = new Ref<>();
        callWithContentBackup(() -> {
            relation.val = createDialog()
                .owner(getDialogOwner(root))
                .title(Utils.localize(resourceBundle, "select.relation.type"))
                .showChoices(allTypes);
        });
        if (relation.val != null) {
            pipeline.resetStepsBack();
            selectedRelation = new FXRelationBuilder(relation.val, relationsListView.getItems());
            table.setItems(selectedRelation.getData());
            relationsListView.getSelectionModel().select(selectedRelation);
        }
    }

    @FXML
    private void back() {
        if (pipeline.lock.tryAcquire()) {
            final List<RelationBuilder> rels = pipeline.getReportRelations();
            rels.clear();
            rels.addAll(relationsListView.getItems());
            pipeline.back();
        }
    }

    @FXML
    private void next() {
        if (pipeline.lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            new Thread(() -> {
                final FilteredList<FXRelationBuilder> unanchored =
                        relationsListView.getItems().filtered(rel -> rel.words.isEmpty());
                pipeline.setReportRelations(words, unanchored);
            }, "FromRelationsState").start();
        }
    }

    @FXML
    private void remove() {
        if (selectedRelation != null) {
            pipeline.resetStepsBack();
            final int index = table.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                final RelationInfo remove = selectedRelation.getData().remove(index);
                //remove selection background
                final Object obj = remove.getObject();
                final List<Text> texts = objectWords.get(obj);
                if (texts != null) {
                    texts.forEach(Utils::unstyleTextBackground);
                }
            }
        }
    }

    @FXML
    private void removeRelation() {
        if (selectedRelation != null) {
            pipeline.resetStepsBack();
            clearSelectedRelationBackground();
            for (Word w : selectedRelation.words) {
                w.setRelation(null);
                final Text t = texts.get(w.getIndex());
                Utils.unstyleText(t);
            }
            relationsListView.getItems().remove(selectedRelation);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty().add(-20));
        slider.addEventFilter(EventType.ROOT, e -> e.consume());
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
                pipeline.resetStepsBack();
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
        //
        relationsListView.setItems(FXCollections.observableArrayList(
                (FXRelationBuilder p) -> new Observable[] { p.stringRepresentation }));
        relationsListView.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldVal, newVal) -> { selectRelation(newVal); });
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        texts = new ArrayList<>();
        words = pipeline.getReportWords();
        for (RelationBuilder r : pipeline.getReportRelations()) {
            final FXRelationBuilder relation = (FXRelationBuilder) r;
            relation.list = relationsListView.getItems();
            relationsListView.getItems().add(relation);
        }
        for (final Word word: words) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                final long entityId = word.getEntity().getType().getId();
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
            if (word.getRelation() != null) {
                final RelationType type = word.getRelation().getType();
                Utils.styleText(text, "RELATION", ~type.getId());
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
                } else if (word.getRelation() != null) {
                    final String newTip = word.getRelation().getType().toString();
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
            text.setOnMousePressed(e -> {
                clearSelectedRelationBackground();
                selectedRelation = null;
                relationsListView.getSelectionModel().select(-1);
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
                    final FXRelationBuilder relation = (FXRelationBuilder) word.getRelation();
                    selectRelation(relation);
                    relationsListView.getSelectionModel().select(relation);
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
                    boolean overEntity = false;
                    for (int i = firstSelectedIndex; i <= word.getIndex(); ++i) {
                        if (words.get(i).getEntity() != null) {
                            overEntity = true;
                            break;
                        }
                    }
                    if (overEntity) {
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

        final ObservableList<Object> candidates = FXCollections.observableArrayList(
                pipeline.getReportEntities().stream()
                        .map(ent -> ent.getCandidate())
                        .distinct()
                        .collect(Collectors.toList())
        );
        final Callback<TableColumn<RelationInfo, Object>, TableCell<RelationInfo, Object>> cellFactory =
                (TableColumn<RelationInfo, Object> param) -> new ObjectTableCell(new StringConverter<Object>() {
                    @Override
                    public String toString(Object o) {
                        if (o == null) {
                            return "";
                        }
                        return String.join(", ", o.getAliases()) + " (" + o.getId() + ") - " + o.getType().getName();
                    }
                    @Override
                    public Object fromString(String string) {
                        throw new UnsupportedOperationException("This should not be needed!");
                    }
                }, candidates);
        objectColumn.setCellFactory(cellFactory);
    }

    /**
     * Assigns relation to selected texts.
     * @param relation RelationType to assign
     */
    protected void assignRelationToSelectedTexts(final RelationType relation) {
        if (settings.getProperty(CLEAR_FILTERS, "false").equals("true")) {
            filterField.clear();
        }
        try {
            pipeline.resetStepsBack();
            final IClearer clearer = i -> Utils.unstyleText(texts.get(i));
            if (relation == null) {
                RelationBuilder.clear(words, firstSelectedIndex, lastSelectedIndex, clearer);
                selectRelation(selectedRelation);
                return;
            }
            final FXRelationBuilder builder = new FXRelationBuilder(relation, relationsListView.getItems());
            final Pair<Integer, Integer> bounds =
                    builder.add(words, firstSelectedIndex, lastSelectedIndex, clearer);
            for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
                final Text t = texts.get(i);
                Utils.unstyleText(t);
                Utils.styleText(t, "RELATION", ~relation.getId());
            }
            selectRelation(builder);
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

    /**
     * Does all necessary steps to select given relation.
     * Clears backgrounds and old selection, selects relation's words,
     * prepares backgrounds, sets table content.
     * @param relation FXRelationBuilder to select
     */
    protected void selectRelation(final FXRelationBuilder relation) {
        clearSelectedRelationBackground();
        removeSelectedClass(texts);
        selectedRelation = relation;
        if (relation == null) {
            relationsListView.getSelectionModel().select(-1);
            table.setItems(null);
            return;
        }
        final List<Text> relTexts = relation.words.stream()
                .map(Word::getIndex)
                .map(texts::get)
                .collect(Collectors.toList());
        addSelectedClass(relTexts);
        final RelationType type = selectedRelation.getType();
        final long id = type.getId();
        selectedRelation.getData().stream()
                .flatMap(relInfo -> objectWords.get(relInfo.object.get()).stream())
                .forEach(t -> Utils.styleTextBackground(t, id));
        table.setItems(selectedRelation.getData());
    }

    /**
     * Hacky class to add background to objects being selected.
     */
    public class ObjectTableCell extends ComboBoxTableCell<FXRelationBuilder.RelationInfo, Object> {

        /**
         * Only constructor. Converter is used if reflection fails.
         * @param converter A {@link StringConverter} that can convert an item of type T
         *      into a user-readable string so that it may then be shown in the
         *      ComboBox popup menu.
         * @param items The items to show in the ComboBox popup menu when selected
         *      by the user.
         */
        public ObjectTableCell(StringConverter<Object> converter, ObservableList<Object> items) {
            super(converter, items);
        }

        @Override
        public void startEdit() {
            try {
                final Field comboBoxField = ComboBoxTableCell.class.getDeclaredField("comboBox");
                comboBoxField.setAccessible(true);
                java.lang.Object cb1 = comboBoxField.get(this);
                super.startEdit();
                java.lang.Object cb2 = comboBoxField.get(this);
                if (cb2 != null && !cb2.equals(cb1) && cb2 instanceof ComboBox) {
                    @SuppressWarnings("unchecked")
                    final ComboBox<Object> cb = (ComboBox<Object>) cb2;
                    cb.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
                        @Override
                        public ListCell<Object> call(ListView<Object> param) {
                            final ListCell<Object> cell = new ListCell<Object>() {
                                @Override
                                public void updateItem(Object item, boolean empty) {
                                        super.updateItem(item, empty);
                                        String text;
                                        if (getConverter() != null) {
                                            text = getConverter().toString(item);
                                        } else {
                                            text = item == null ? "" : item.toString();
                                        }
                                        setText(text);
                                    }
                            };
                            cell.setOnMouseEntered(e -> {
                                final RelationType type = selectedRelation.getType();
                                final long id = type.getId();
                                Object item = cell.getItem();
                                if (item != null) {
                                    objectWords.get(item).stream()
                                        .forEach(t -> Utils.styleTextBackground(t, id));
                                }
                            });
                            cell.setOnMouseExited(e -> {
                                final Object obj = cell.getItem();
                                if (obj == null) {
                                    return;
                                }
                                boolean found = selectedRelation.getData().stream()
                                        .anyMatch(rel -> rel.getObject() == obj);
                                if (!found) {
                                    objectWords.get(cell.getItem()).stream()
                                        .forEach(t -> Utils.unstyleTextBackground(t));
                                }
                            });
                            return cell;
                        }
                    });
                }
            } catch (NoSuchFieldException | IllegalAccessException | SecurityException e) {
                super.startEdit(); //we failed, lets behave normally
            }
        }
    }
}
