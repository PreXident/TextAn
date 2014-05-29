package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

/**
 * Controls editing objects.
 */
public class ReportObjectsController extends ReportWizardController {

    @FXML
    BorderPane root;

    @FXML
    ScrollPane scrollPane;

    @FXML
    TextFlow textFlow;

    @FXML
    Slider slider;

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Context menu with object selection. */
    ContextMenu contextMenu;

    /** Texts' tooltip. */
    final Tooltip tooltip = new Tooltip("");

    /** Index of selected entity. */
    EntityInfo selectedEntity = null;

    /** Mapping of ObjectType->Objects. */
    Map<Long, ObservableList<Object>> typeObjects = new HashMap<>();

    /** Lists for given Entity. */
    Map<EntityBuilder, EntityInfo> entityLists = new HashMap<>();

    /** ListView with objects from db. */
    ListView<Pair<Double, Object>> dbListView;

    /** ListView with new objects of given type. */
    ListView<Object> newListView;

    /** TextField in ContextMenu with filter. */
    TextField filterField;

    /** Tooltip for {@link #dbListView}. */
    Tooltip dbTooltip = new Tooltip();

    /** Tooltip for {@link #newListView}. */
    Tooltip newTooltip = new Tooltip();

    /** List of all new objects. */
    ObservableList<Object> newObjects = FXCollections.observableArrayList();

    /** CheckBox for adding all objects from db to {@link #dbListView}. */
    CheckBox allObjectsCheckBox;

    @FXML
    public void back() {
        if (pipeline.lock.tryAcquire()) {
            pipeline.back();
        }
    }

    @FXML
    private void next() {
        if (pipeline.lock.tryAcquire()) {
            for (Entity ent : pipeline.getReportEntities()) {
                if (ent.getCandidate() == null) {
                    callWithContentBackup(() ->
                        createDialog()
                                .owner(getDialogOwner(root))
                                .title(Utils.localize(resourceBundle, "error.objects.unassigned"))
                                .message(Utils.localize(resourceBundle, "error.objects.unassigned.detail"))
                                .showInformation());
                    pipeline.lock.release();
                    return;
                }
            }
            getMainNode().setCursor(Cursor.WAIT);
            new Thread(() -> {
                pipeline.setReportObjects(pipeline.getReportEntities());
            }, "FromObjectsState").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty());
        slider.addEventFilter(EventType.ROOT, e -> e.consume());
        //create popup
        BorderPane border = new BorderPane();
        final SplitPane splitVert = new SplitPane();
        splitVert.setOrientation(Orientation.HORIZONTAL);
        dbListView = new ListView<>();
        dbListView.setPrefHeight(100);
        dbListView.setTooltip(new Tooltip());
        newListView = new ListView<>();
        newListView.setPrefHeight(100);
        final Button add = new Button("+");
        add.setOnAction(e -> {
            contextMenu.hide();
            final Entity ent =
                    pipeline.getReportEntities().get(selectedEntity.index);
            final Object newObject = new Object(-newObjects.size() - 1, ent.getType(), Arrays.asList(ent.getValue()));
            newObjects.add(newObject);
            setNewObjectAsSelectedEntityCandidate(newObject);
        });
        allObjectsCheckBox = new CheckBox();
        allObjectsCheckBox.setText(Utils.localize(resourceBundle, "include.all.objects"));
        allObjectsCheckBox.selectedProperty().addListener(e -> filterObjects(selectedEntity));
        final VBox vbox = new VBox();
        vbox.getChildren().addAll(allObjectsCheckBox, dbListView);
        splitVert.getItems().addAll(vbox, newListView);
        border.setCenter(splitVert);
        filterField = new TextField();
        filterField.textProperty().addListener(e -> filterObjects(selectedEntity));
        filterField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN && dbListView.getItems().size() > 0) {
                dbListView.getSelectionModel().select(0);
                dbListView.requestFocus();
            }
        });
        final HBox top =new HBox();
        top.getChildren().addAll(filterField, add);
        HBox.setHgrow(filterField, Priority.ALWAYS);
        border.setTop(top);
        contextMenu = new ContextMenu(new CustomMenuItem(border, true));
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        final List<Text> texts = new ArrayList<>();
        for (final Word word: pipeline.getReportWords()) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                final EntityBuilder entity = word.getEntity();
                final long entityId = entity.getType().getId();
                final int entityIndex = entity.getIndex();
                Utils.styleText(text, "ENTITY", entityId);

                EntityInfo entityInfo = entityLists.get(word.getEntity());
                if (entityInfo == null) {
                    entityInfo = new EntityInfo();
                    entityInfo.index = entityIndex;
                    final Entity ent = pipeline.getReportEntities().get(entityIndex);
                    entityInfo.type = ent.getType().getId();
                    final List<Pair<Double, Object>> candidates = ent.getCandidates();
                    Collections.sort(candidates, Entity.COMPARATOR);
                    entityInfo.ranked = FXCollections.observableArrayList(candidates);
                    entityLists.put(entity, entityInfo);
                    final Object cand = ent.getCandidate();
                    if (cand != null && cand.isNew() && !newObjects.contains(cand)) {
                        newObjects.add(cand);
                    }
                }
                final EntityInfo ei = entityInfo;

                text.setOnMousePressed(e -> {
                    selectedEntity = ei;
                    filterObjects(ei);
                    contextMenu.show(text, Side.BOTTOM, 0, 0);
                    filterField.requestFocus();
                });
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
            texts.add(text);
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(texts);
        //
        dbListView.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                contextMenu.hide();
                final Pair<Double, Object> p = dbListView.getSelectionModel().getSelectedItem();
                setObjectAsSelectedEntityCandidate(p.getSecond());
            }
        });
        dbListView.setCellFactory(new Callback<ListView<Pair<Double, Object>>, ListCell<Pair<Double, Object>>>() {
            @Override
            public ListCell<Pair<Double, Object>> call(ListView<Pair<Double, Object>> p) {
                return new ListCell<Pair<Double, Object>>() {
                    {
                        this.setOnMouseClicked((MouseEvent e) -> {
                            contextMenu.hide();
                            @SuppressWarnings("unchecked")
                            final Pair<Double, Object> p = ((ListCell<Pair<Double, Object>>) e.getSource()).getItem();
                            setObjectAsSelectedEntityCandidate(p.getSecond());
                        });
                        this.setOnMouseEntered(e -> {
                            @SuppressWarnings("unchecked")
                            final Pair<Double, Object> p = ((ListCell<Pair<Double, Object>>) e.getSource()).getItem();
                            if (p != null) {
                                dbTooltip.setText(p.getSecond().toString());
                                dbListView.setTooltip(dbTooltip);
                            }
                        });
                        this.setOnMouseExited(e -> {
                            dbListView.setTooltip(null);
                        });
                    }
                    @Override
                    protected void updateItem(Pair<Double, Object> p, boolean empty) {
                        super.updateItem(p, empty);
                        if (empty) {
                            setText("");
                            return;
                        }
                        if (p != null) {
                            String prefix = "";
                            if (p.getFirst() != null) {
                                prefix = p.getFirst().toString() + ": ";
                            }
                            setText(shorter(prefix + p.getSecond().toString()));
                        }
                    }
                };
            }
        });
        newListView.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell<Object> call(ListView<Object> p) {
                return new ListCell<Object>() {
                    {
                        this.setOnMouseClicked((MouseEvent t) -> {
                            contextMenu.hide();
                            @SuppressWarnings("unchecked")
                            final Object obj = ((ListCell<Object>) t.getSource()).getItem();
                            setNewObjectAsSelectedEntityCandidate(obj);
                        });
                        this.setOnMouseEntered(e -> {
                            @SuppressWarnings("unchecked")
                            final Object o = ((ListCell<Object>) e.getSource()).getItem();
                            if (o != null) {
                                newTooltip.setText(o.toString());
                                newListView.setTooltip(newTooltip);
                            }
                        });
                        this.setOnMouseExited(e -> {
                            newListView.setTooltip(null);
                        });
                    }
                    @Override
                    protected void updateItem(Object o, boolean empty) {
                        super.updateItem(o, empty);
                        if (empty) {
                            setText("");
                            return;
                        }
                        if (o != null) {
                            setText(shorter(o.toString()));
                        }
                    }
                };
            }
        });
    }

    /**
     * Sets the entity candidate to new object.
     * Removes previous object's alias if needed, adds new alias to the new object.
     * @param object new candidate
     */
    private void setNewObjectAsSelectedEntityCandidate(final Object object) {
        final Entity ent = setObjectAsSelectedEntityCandidate(object);
        object.getAliases().add(ent.getValue());
    }

    /**
     * Sets the selected entity candidate to object.
     * Removes previous object's alias if needed.
     * @param object new candidate
     * @return Entity behind selectedEntity
     */
    private Entity setObjectAsSelectedEntityCandidate(final Object object) {
        final Entity entity =
                pipeline.getReportEntities().get(selectedEntity.index);
        final Object prev = entity.getCandidate();
        final String alias = entity.getValue();
        if (prev != null && prev.isNew()) {
            prev.getAliases().remove(alias);
        }
        entity.setCandidate(object);
        pipeline.resetStepsBack();
        return entity;
    }

    /**
     * If string is too long, returns its shortened variant that ends with ...
     * @param string string to shorter
     * @return shorter string
     */
    private String shorter(final String string) {
        return string.length() > 35 ?
                string.substring(0, 32) + "..."
                : string;
    }

    /**
     * Updates content of list views by filtering entityInfo's lists.
     * @param entityInfo selected entity
     */
    private void filterObjects(final EntityInfo entityInfo) {
        dbListView.getItems().clear();
        dbListView.getItems().addAll(entityInfo.ranked.filtered(p -> {
            final String filter = filterField.getText();
            if (filter == null || filter.isEmpty()) {
                return true;
            }
            if (p == null) {
                return false;
            }
            final String aliases = String.join(", ", p.getSecond().getAliases());
            return aliases.toLowerCase().contains(filter.toLowerCase());
        }));
        final Set<Object> filteredSet = dbListView.getItems().stream()
                .map(Pair::getSecond)
                .collect(Collectors.toCollection(HashSet::new));

        if (allObjectsCheckBox.isSelected()) {
            if (entityInfo.all == null) {
                ObservableList<Object> all = typeObjects.get(entityInfo.type);
                if (all == null) {
                    try {
                        all = FXCollections.observableArrayList(
                                pipeline.getClient().getObjectsListByTypeId(entityInfo.type));
                        typeObjects.put(entityInfo.type, all);
                        Collections.sort(all, (o1, o2) -> Long.compare(o1.getId(), o2.getId()));
                    } catch (IdNotFoundException e) {
                        e.printStackTrace();
                        all = FXCollections.observableArrayList(all);
                        typeObjects.put(entityInfo.type, all);
                    }
                }
                entityInfo.all = all;
            }

            entityInfo.all.stream()
                    .filter(obj ->{
                        final String filter = filterField.getText();
                        if (filter == null || filter.isEmpty()) {
                            return true;
                        }
                        final String aliases = String.join(", ", obj.getAliases());
                        return aliases.toLowerCase().contains(filter.toLowerCase());
                    })
                    .filter(obj -> !filteredSet.contains(obj))
                    .map(obj -> new Pair<Double, Object>(null, obj))
                    .forEach(dbListView.getItems()::add);
        }
        newListView.setItems(newObjects.filtered((Object o) -> {
            return o.getType().getId() == selectedEntity.type;
        }));
        final Entity ent = pipeline.getReportEntities().get(entityInfo.index);
        if (ent.getCandidate() != null) {
            final Object candidate = ent.getCandidate();
            if (candidate.isNew()) {
                newListView.getSelectionModel().select(candidate);
                dbListView.getSelectionModel().select(-1);
            } else {
                newListView.getSelectionModel().select(-1);
                for (Pair<Double, Object> p : dbListView.getItems()) {
                    if (p.getSecond() == candidate) {
                        dbListView.getSelectionModel().select(p);
                    }
                }
            }
        }
    }

    /**
     * Simple holder of information about entity to provide the right
     * set of object to chose from.
     */
    static class EntityInfo {
        /** Entity index. */
        int index;

        /** Entity type. */
        long type;

        /** List of ranked objects candidating for this entity. */
        ObservableList<Pair<Double, Object>> ranked;

        /** List of all suitable candidates fot this entity. */
        ObservableList<Object> all;
    }
}
