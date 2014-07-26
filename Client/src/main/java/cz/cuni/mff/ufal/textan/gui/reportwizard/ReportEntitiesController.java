package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.processreport.AbstractBuilder.IClearer;
import cz.cuni.mff.ufal.textan.core.processreport.AbstractBuilder.SplitException;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentChangedException;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import static cz.cuni.mff.ufal.textan.gui.TextAnController.CLEAR_FILTERS;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * Controls editing entities.
 * TOOD refactor entity handling
 */
public class ReportEntitiesController extends ReportWizardController {

    /** Style class for selected words. */
    static final String SELECTED = "selected";

    /**
     * Adds {@link #SELECTED} style class to all items in the list.
     * @param list items to which add the style class
     */
    static void addSelectedClass(Iterable<? extends Node> list) {
        list.forEach(node -> node.getStyleClass().add(SELECTED));
    }

    /**
     * Removes {@link #SELECTED} style class from all items in the list.
     * @param list items from which remove the style class
     */
    static void removeSelectedClass(Iterable<? extends Node> list) {
        list.forEach(node -> node.getStyleClass().remove(SELECTED));
    }

    @FXML
    BorderPane root;

    @FXML
    Button backButton;

    @FXML
    ScrollPane scrollPane;

    @FXML
    Slider slider;

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
    ContextMenu contextMenu;

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Words with assigned EntitityBuilders. */
    List<Word> words;

    /** ListView in ContextMenu with list of entity types. */
    ListView<ObjectType> listView;

    /** TextField in ContextMenu with filter. */
    TextField filterField;

    /** List with all entity types. */
    ObservableList<ObjectType> allTypes;

    /** Content of {@link #textFlow}. */
    List<Text> texts;

    /** Tooltip for assigned entities. */
    Tooltip tooltip = new Tooltip();

    @FXML
    private void back() {
        if (pipeline.lock.tryAcquire()) {
            pipeline.back();
        }
    }

    @FXML
    private void next() {
        if (pipeline.lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            new Thread(() -> {
                handleDocumentChangedException(root, () -> {
                    pipeline.setReportWords(words);
                    return null;
                });
            }, "FromEntitiesState").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty().add(-20));
        slider.addEventFilter(EventType.ROOT, e -> e.consume());
        slider.setLabelFormatter(new SliderLabelFormatter());
        scrollPane.vvalueProperty().addListener(e -> {
            textFlow.layoutChildren();
        });
        //create popup
        BorderPane border = new BorderPane();
        listView = new ListView<>();
        listView.setPrefHeight(100);
        border.setCenter(listView);
        filterField = new TextField();
        filterField.textProperty().addListener(e -> filterTypes());
        filterField.setOnAction(ev -> {
            if (listView.getItems().size() == 1) {
                final ObjectType ot = listView.getItems().get(0);
                assignEntityToSelectedTexts(ot);
            }
        });
        filterField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN && listView.getItems().size() > 0) {
                listView.getSelectionModel().select(0);
                listView.requestFocus();
            }
        });
        border.setTop(filterField);
        contextMenu = new ContextMenu(new CustomMenuItem(border, true));
        contextMenu.setConsumeAutoHidingEvents(false);
    }

    @Override
    public Runnable getContainerCloser() {
        return getSavePrompter(root);
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        backButton.setVisible(pipeline.getReportId() < 1);
        texts = new ArrayList<>();
        words = pipeline.getReportWords();
        for (Word word: words) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                Utils.styleText(text, "ENTITY", word.getEntity().getType().getId());
            }
            text.setOnMousePressed(e -> {
                if (!text.getStyleClass().contains(SELECTED)) {
                    removeSelectedClass(texts);
                    dragging = true;
                    firstDragged = texts.indexOf(text);
                    lastDragged = firstDragged;
                    firstSelectedIndex = firstDragged;
                    lastSelectedIndex = firstDragged;
                    text.getStyleClass().add(SELECTED);
                }
            });
            text.setOnDragDetected(e -> {
                text.startFullDrag();
            });
            text.setOnMouseDragEntered(e -> {
                if (dragging) {
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
            text.setOnMouseEntered((MouseEvent t) -> {
                if (word.getEntity() != null) {
                    final String newTip = word.getEntity().getType().toString();
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
            texts.add(text);
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(texts);
        //
        listView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                contextMenu.hide();
                final ObjectType item = listView.getSelectionModel().getSelectedItem();
                assignEntityToSelectedTexts(item);
            }
        });
        listView.setCellFactory(new Callback<ListView<ObjectType>, ListCell<ObjectType>>() {
            @Override
            public ListCell<ObjectType> call(ListView<ObjectType> p) {
                return new ListCell<ObjectType>() {
                    {
                        this.setOnMouseClicked((MouseEvent t) -> {
                            contextMenu.hide();
                            @SuppressWarnings("unchecked")
                            final ObjectType ot = ((ListCell<ObjectType>) t.getSource()).getItem();
                            assignEntityToSelectedTexts(ot);
                        });
                    }
                    @Override
                    protected void updateItem(ObjectType t, boolean empty) {
                        super.updateItem(t, empty);
                        if (empty) {
                            setText("");
                            return;
                        }
                        if (t != null) {
                            setText(t.getName());
                        } else {
                            setText(Utils.localize(resourceBundle, "entity.none"));
                        }
                    }
                };
            }
        });
        final List<ObjectType> types = pipeline.getClient().getObjectTypesList();
        final Collator collator = Collator.getInstance();
        Collections.sort(types, (o1, o2) -> collator.compare(o1.getName(), o2.getName()));
        types.add(0, null); //None entity
        allTypes = FXCollections.observableArrayList(types);
        filterTypes();
    }

    /**
     * Updates content of listView by filtering allTypes with filterField.
     */
    private void filterTypes() {
        listView.setItems(allTypes.filtered((ObjectType t) -> {
            final String filter = filterField.getText();
            if (filter == null || filter.isEmpty()) {
                return true;
            }
            if (t == null) {
                return false;
            }
            return t.getName().toLowerCase().contains(filter.toLowerCase());
        }));
    }

    private void assignEntityToSelectedTexts(final ObjectType ot) {
        contextMenu.hide();
        if (settings.getProperty(CLEAR_FILTERS, "false").equals("true")) {
            filterField.clear();
        }
        pipeline.resetStepsBack();
        try {
            final IClearer clearer = i -> Utils.unstyleText(texts.get(i));
            if (ot == null) {
                EntityBuilder.clear(words, firstSelectedIndex, lastSelectedIndex, clearer);
                return;
            }
            final long id = ot.getId();
            final EntityBuilder e = new EntityBuilder(ot);
            final Pair<Integer, Integer> bounds =
                    e.add(words, firstSelectedIndex, lastSelectedIndex, clearer);
            for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
                Utils.styleText(texts.get(i), "ENTITY", id);
            }
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
}
