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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
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

    /** ScrollPane's tooltip. */
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
    ContextMenu contextMenu;

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Words with assigned EntitityBuilders. */
    List<Word> words;

    /** Currently selected relation. */
    FXRelationBuilder selectedRelation;

    Map<Object, List<Text>> objectWords = new HashMap<>();

    @FXML
    private void add() {
        if (selectedRelation != null) {
            selectedRelation.getData().add(new RelationInfo(0, null));
        }
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
                final String clazz = "OBJECT_" + type.getId();
                final Object obj = remove.getObject();
                final List<Text> texts = objectWords.get(obj);
                if (texts != null) {
                    removeClass(clazz, texts);
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
        orderColumn.setCellValueFactory(new Callback<CellDataFeatures<RelationInfo, Number>, ObservableValue<Number>>() {
            @Override
            public ObservableValue<Number> call(CellDataFeatures<RelationInfo, Number> p) {
                return p.getValue().order;
            }
        });
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
            new EventHandler<CellEditEvent<RelationInfo, Number>>() {
                @Override
                public void handle(CellEditEvent<RelationInfo, Number> t) {
                    ((RelationInfo) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).order.setValue(t.getNewValue());
                }
            }
        );
        objectColumn.setCellValueFactory(new Callback<CellDataFeatures<RelationInfo, Object>, ObservableValue<Object>>() {
            @Override
            public ObservableValue<Object> call(CellDataFeatures<RelationInfo, Object> p) {
                return p.getValue().object;
            }
         });
        objectColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<RelationInfo, Object>>() {
                @Override
                public void handle(CellEditEvent<RelationInfo, Object> t) {
                    ((RelationInfo) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).object.setValue(t.getNewValue());
                    //TODO add selection background, remove selection background
                    final RelationType type = selectedRelation.getType();
                    final String clazz = "OBJECT_" + type.getId();
                    final Object oldObj = t.getOldValue();
                    final List<Text> oldTexts = objectWords.get(oldObj);
                    if (oldTexts != null) {
                        removeClass(clazz, oldTexts);
                    }
                    final Object newObj = t.getNewValue();
                    final List<Text> newTexts = objectWords.get(newObj);
                    if (newTexts != null) {
                        addClass(clazz, newTexts);
                    }
                }
            }
        );
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        final List<Text> texts = new ArrayList<>();
        words = pipeline.getReportWords();
        for (final Word word: words) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                final int entityId = word.getEntity().getId();
                text.getStyleClass().add("ENTITY_" + entityId);
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
                    scrollPane.setTooltip(tooltip);
                    final String oldTip = scrollPane.getTooltip().getText();
                    final int entityIndex = word.getEntity().getIndex();
                    final Object obj = pipeline.getReportEntities().get(entityIndex).getCandidate();
                    if (obj != null) {
                        final String newTip = obj.toString();
                        if (!newTip.equals(oldTip)) {
                            scrollPane.getTooltip().setText(newTip);
                        }
                    }
                } else {
                    scrollPane.setTooltip(null);
                }
            });
            text.setOnMouseExited((MouseEvent t) -> {
                scrollPane.setTooltip(null);
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
                    final String clazz = "OBJECT_" + type.getId();
                    selectedRelation.getData().stream()
                            .map(relInfo -> relInfo.object.get())
                            .forEach(obj -> {
                                final List<Text> list = objectWords.get(obj);
                                if (list != null) {
                                    addClass(clazz, list);
                                }
                            });
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
                }
            });
            texts.add(text);
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(texts);

        final EventHandler<ActionEvent> eh = (ActionEvent t) -> {
            //relation type id in userdata
            final RelationType type = (RelationType)((MenuItem) t.getSource()).getUserData();
            if (type == null) {
                for (int i = firstSelectedIndex; i <= lastSelectedIndex; ++i) {
                    words.get(i).setRelation(null);
                    texts.get(i).getStyleClass().clear();
                }
                return;
            }
            final FXRelationBuilder builder = new FXRelationBuilder(type);
            try {
                Pair<Integer, Integer> bounds = builder.add(words, firstSelectedIndex, lastSelectedIndex, i -> texts.get(i).getStyleClass().clear());
                for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
                    texts.get(i).getStyleClass().clear();
                    texts.get(i).getStyleClass().add("RELATION_" + type.getId());
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
        };

        contextMenu = new ContextMenu();
        MenuItem noRelation = new MenuItem(Utils.localize(resourceBundle, "relation.none"));
        noRelation.setOnAction(eh);
        contextMenu.getItems().add(noRelation);
        for (RelationType relType : pipeline.getClient().getRelationTypesList()) {
            final MenuItem mi = new MenuItem(relType.getName());
            mi.setOnAction(eh);
            mi.setUserData(relType); //relation type id to userdata
            contextMenu.getItems().add(mi);
        }

        objectColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(
                pipeline.getReportEntities().stream()
                        .map(ent -> ent.getCandidate())
                        .distinct()
                        .collect(Collectors.toList())
        )));
    }

    protected void clearSelectedRelationBackground() {
        if (selectedRelation != null) {
             final RelationType type = selectedRelation.getType();
             final String clazz = "OBJECT_" + type.getId();
             selectedRelation.getData().stream()
                     .map(relInfo -> relInfo.object.get())
                     .forEach(obj -> {
                         final List<Text> list = objectWords.get(obj);
                         if (list != null) {
                             removeClass(clazz, list);
                         }
                     });
         }
    }
}
