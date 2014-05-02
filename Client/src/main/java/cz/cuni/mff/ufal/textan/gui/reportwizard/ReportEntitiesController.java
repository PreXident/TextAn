package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.processreport.AbstractBuilder.SplitException;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
    ScrollPane scrollPane;

    @FXML
    TextFlow textFlow;

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
        pipeline.setReportWords(words);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty());
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
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        texts = new ArrayList<>();
        words = pipeline.getReportWords();
        for (Word word: words) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                Utils.styleText(text, "ENTITY", word.getEntity().getId());
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
        if (ot == null) {
            for (int i = firstSelectedIndex; i <= lastSelectedIndex; ++i) {
                words.get(i).setEntity(null);
                Utils.unstyleText(texts.get(i));
            }
            return;
        }
        final long id = ot.getId();
        final EntityBuilder e = new EntityBuilder(id);
        try {
            Pair<Integer, Integer> bounds = e.add(words, firstSelectedIndex, lastSelectedIndex, i -> Utils.unstyleText(texts.get(i)));
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
