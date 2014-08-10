package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.ObjectContextMenu;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.Utils.IdType;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.Callback;

/**
 * Controls editing objects.
 */
public class ReportObjectsController extends ReportWizardController {

    static final String TEXT_HIGHLIGHT_CLASS = "text-no-object";

    @FXML
    BorderPane root;

    @FXML
    ScrollPane scrollPane;

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

    /** Context menu for objects. */
    ObjectContextMenu objectContextMenu;

    /** Object to display graph for. */
    ObjectProperty<Object> objectForGraph = new SimpleObjectProperty<>();

    @FXML
    public void back() {
        if (pipeline.lock.tryAcquire()) {
            pipeline.back();
        }
    }

    @FXML
    private void next() {
        StringBuilder builder = new StringBuilder();
        for (Entity ent : pipeline.getReportEntities()) {
            if (ent.getCandidate() == null) {
                builder.append(ent.getValue()).append('\n');
            }
        }
        if (pipeline.lock.tryAcquire()) {
            if (builder.length() != 0) {
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title(Utils.localize(resourceBundle, "error.objects.unassigned"))
                            .showException(new Throwable() {
                                @Override
                                public String getMessage() {
                                    return Utils.localize(resourceBundle, "error.objects.unassigned.detail");
                                }

                                @Override
                                public void printStackTrace(PrintWriter s) {
                                    s.write(builder.toString());
                                }
                            });});
                pipeline.lock.release();
            } else {
                getMainNode().setCursor(Cursor.WAIT);
                new Thread(() -> {
                    pipeline.setReportObjects(pipeline.getReportEntities());
                }, "FromObjectsState").start();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty().add(-20));
        slider.setLabelFormatter(new SliderLabelFormatter());
        scrollPane.vvalueProperty().addListener(e -> {
            textFlow.layoutChildren();
        });
        //create popup
        BorderPane border = new BorderPane();
        final SplitPane splitVert = new SplitPane();
        splitVert.setOrientation(Orientation.HORIZONTAL);
        dbListView = new ListView<>();
        dbListView.setPrefHeight(100);
        newListView = new ListView<>();
        newListView.setPrefHeight(100);
        newListView.setMinWidth(30);
        final Button add = new Button("+");
        add.setOnAction(e -> {
            contextMenu.hide();
            final Entity ent =
                    pipeline.getReportEntities().get(selectedEntity.index);
            final NewObject newObject = new NewObject(ent.getType(), Collections.emptyList());
            newObjects.add(newObject);
            setNewObjectAsSelectedEntityCandidate(newObject);
        });
        allObjectsCheckBox = new CheckBox();
        allObjectsCheckBox.setText(Utils.localize(resourceBundle, "include.all.objects"));
        allObjectsCheckBox.selectedProperty().addListener(e -> {
            filterObjects(selectedEntity);
        });
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
        contextMenu.setConsumeAutoHidingEvents(false);
    }

    @Override
    public Runnable getContainerCloser() {
        return getSavePrompter(root);
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
                final Entity ent = pipeline.getReportEntities().get(entityIndex);
                Utils.styleText(settings, text, "ENTITY", IdType.ENTITY, entityId);

                EntityInfo entityInfo = entityLists.get(word.getEntity());
                if (entityInfo == null) {
                    entityInfo = new EntityInfo();
                    entityInfo.index = entityIndex;
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
                if (ent.getCandidate() == null) {
                    text.getStyleClass().add(TEXT_HIGHLIGHT_CLASS);
                    entityInfo.texts.add(text);
                }
                final EntityInfo ei = entityInfo;

                text.setOnMousePressed(e -> {
                    selectedEntity = ei;
                    if (e.isPrimaryButtonDown()) {
                        allObjectsCheckBox.setSelected(false);
                        filterObjects(ei);
                        contextMenu.show(text, Side.BOTTOM, 0, 0);
                        filterField.requestFocus();
                    } else {
                        if (ent.getCandidate() != null) {
                            objectForGraph.set(ent.getCandidate());
                            objectContextMenu.show(text, Side.BOTTOM, 0, 0);
                        }
                    }
                });
            }
            text.setOnMouseEntered((MouseEvent t) -> {
                if (word.getEntity() != null) {
                    final int entityIndex = word.getEntity().getIndex();
                    final Object obj = pipeline.getReportEntities().get(entityIndex).getCandidate();
                    String newTip = word.getEntity().getType().getName();
                    if (obj != null) {
                        newTip = newTip + " - " + obj.toString();
                    }
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
        dbListView.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                contextMenu.hide();
                final Pair<Double, Object> p = dbListView.getSelectionModel().getSelectedItem();
                final Object obj = p != null ? p.getSecond() : null;
                setObjectAsSelectedEntityCandidate(obj);
            }
        });
        dbListView.setCellFactory(new Callback<ListView<Pair<Double, Object>>, ListCell<Pair<Double, Object>>>() {
            @Override
            public ListCell<Pair<Double, Object>> call(ListView<Pair<Double, Object>> p) {
                return new ListCell<Pair<Double, Object>>() {
                    {
                        this.setOnMousePressed((MouseEvent e) -> {
                            final Pair<Double, Object> item = this.getItem();
                            final Object obj = item != null ? item.getSecond() : null;
                            if (e.isPrimaryButtonDown()) {
                                contextMenu.hide();
                                setObjectAsSelectedEntityCandidate(obj);
                            } else {
                                objectForGraph.set(obj);
                            }
                        });
                        this.setOnMouseEntered(e -> {
                            final Pair<Double, Object> p = this.getItem();
                            if (p != null) {
                                dbTooltip.setText(p.getSecond().toString());
                                dbListView.setTooltip(dbTooltip);
                            } else {
                                dbListView.setTooltip(null);
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
                            setText(Utils.shortString(prefix + p.getSecond().toString()));
                            setContextMenu(objectContextMenu);
                        } else {
                            setContextMenu(null);
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
                            final Object obj = this.getItem();
                            setNewObjectAsSelectedEntityCandidate(obj);
                        });
                        this.setOnMouseEntered(e -> {
                            final Object o = this.getItem();
                            if (o != null) {
                                newTooltip.setText(o.toString());
                                newListView.setTooltip(newTooltip);
                            } else {
                                newListView.setTooltip(null);
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
                            setText(Utils.shortString(o.toString()));
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
        if (object != null && object instanceof NewObject) {
            ++((NewObject) object).refCount;
        }
        setObjectAsSelectedEntityCandidate(object);
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
        if (prev != null) {
            if (prev instanceof NewObject && --((NewObject) prev).refCount == 0) {
                newListView.setItems(null); //we need to get rid of the filtered list, or we get exception on removing
                newObjects.remove(prev);
            }
        }
        for (Text text : selectedEntity.texts) {
            text.getStyleClass().remove(TEXT_HIGHLIGHT_CLASS);
        }
        entity.setCandidate(object);
        resetStepsBack();
        return entity;
    }

    @Override
    public void setTextAnController(final TextAnController textAnController) {
        objectContextMenu = new ObjectContextMenu(textAnController);
        objectContextMenu.setOnAction(e -> contextMenu.hide());
        objectContextMenu.objectProperty().bind(objectForGraph);
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
            if (candidate.isNew() && candidate instanceof NewObject) {
                newListView.getSelectionModel().select((NewObject) candidate);
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

    @Override
    public void setWindow(final InnerWindow window) {
        super.setWindow(window);
        window.getScene().getWindow().widthProperty().addListener(e -> {
            Utils.runFXlater(() -> { textFlow.layoutChildren(); });
        });
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

        /**
         * List of texts that must be unhighlighted when object is assigned to
         * the entity.
         */
        List<Text> texts = new ArrayList<>();
    }

    /**
     * Simple extension of Object to count references.
     */
    static class NewObject extends Object {

        /** Sequence generating object ids. */
        static long id = 0;

        /**
         * Number of entities that have this object assigned.
         * When hits 0, the object should be removed fromm {@link #newObjects}.
         */
        int refCount = 0;

        /**
         * Only constructor
         * @param type object type
         * @param aliases object aliases
         */
        public NewObject(ObjectType type, Collection<String> aliases) {
            super(--id, type, aliases);
        }
    }
}
