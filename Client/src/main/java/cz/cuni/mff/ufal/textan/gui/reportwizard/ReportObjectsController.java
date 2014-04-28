package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
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

    /** Localization controller. */
    ResourceBundle resourceBundle;

    /** Context menu with object selection. */
    ContextMenu contextMenu;

    /** Texts' tooltip. */
    final Tooltip tooltip = new Tooltip("");

    /** Index of selected entity. */
    EntityInfo selectedEntity = null;

    /** Mapping of ObjectType->Objects. */
    Map<Long, List<Object>> typeObjects = new HashMap<>();

    /** Lists for given Entity. */
    Map<EntityBuilder, EntityInfo> entityLists = new HashMap<>();

    /** ListView with ranked objects. */
    ListView<Pair<Double, Object>> rankedListView;

    /** ListView with all objects of given type. */
    ListView<Object> allListView;

    /** ListView with new objects of given type. */
    ListView<Object> newListView;

    /** TextField in ContextMenu with filter. */
    TextField filterField;

    /** Tooltip for {@link #rankedListView}. */
    Tooltip rankedTooltip = new Tooltip();

    /** Tooltip for {@link #allListView}. */
    Tooltip allTooltip = new Tooltip();

    /** Tooltip for {@link #newListView}. */
    Tooltip newTooltip = new Tooltip();

    /** List of all new objects. */
    ObservableList<Object> newObjects = FXCollections.observableArrayList();

    @FXML
    private void cancel() {
        closeContainer();
    }

    @FXML
    private void next() {
        for (Entity ent : pipeline.getReportEntities()) {
            if (ent.getCandidate() == null) {
                callWithContentBackup(() ->
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title(Utils.localize(resourceBundle, "error.objects.unassigned"))
                            .message(Utils.localize(resourceBundle, "error.objects.unassigned.detail"))
                            .showInformation());
                return;
            }
        }
        pipeline.setReportObjects(pipeline.getReportEntities());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty());
        //create popup
        BorderPane border = new BorderPane();
        final SplitPane splitVert = new SplitPane();
        splitVert.setOrientation(Orientation.HORIZONTAL);
        final SplitPane splitHor = new SplitPane();
        splitHor.setOrientation(Orientation.VERTICAL);
        rankedListView = new ListView<>();
        rankedListView.setPrefHeight(100);
        rankedListView.setTooltip(new Tooltip());
        allListView = new ListView<>();
        allListView.setPrefHeight(100);
        splitHor.getItems().addAll(rankedListView, allListView);
        newListView = new ListView<>();
        newListView.setPrefHeight(100);
        final BorderPane leftBorder = new BorderPane();
        leftBorder.setCenter(newListView);
        final Button add = new Button("+");
        add.setOnAction(e -> {
            contextMenu.hide();
            final Entity ent = pipeline.getReportEntities().get(selectedEntity.index);
            final Object newObject = new Object(-newObjects.size() - 1, new ObjectType(ent.getType(), ""), Arrays.asList(ent.getValue()));
            newObjects.add(newObject);
            pipeline.getReportEntities().get(selectedEntity.index).setCandidate(newObject);
        });
        leftBorder.setTop(add);
        splitVert.getItems().addAll(splitHor, leftBorder);
        border.setCenter(splitVert);
        filterField = new TextField();
        filterField.textProperty().addListener(e -> filterObjects(selectedEntity));
        border.setTop(filterField);
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
                final long entityId = entity.getId();
                final int entityIndex = entity.getIndex();
                Utils.styleText(text, "ENTITY", entityId);

                EntityInfo entityInfo = entityLists.get(word.getEntity());
                if (entityInfo == null) {
                    entityInfo = new EntityInfo();
                    entityInfo.index = entityIndex;
                    final Entity ent = pipeline.getReportEntities().get(entityIndex);
                    entityInfo.type = ent.getType();
                    final List<Pair<Double, Object>> candidates = ent.getCandidates();
                    Collections.sort(candidates, Entity.COMPARATOR);
                    entityInfo.ranked = FXCollections.observableArrayList(candidates);
                    List<Object> all = typeObjects.get(entityId);
                    if (all == null) {
                        try {
                            all = pipeline.getClient().getObjectsListByTypeId(entityId);
                            typeObjects.put(entityId, all);
                            Collections.sort(all, (o1, o2) -> Long.compare(o1.getId(), o2.getId()));
                        } catch (IdNotFoundException e) {
                            e.printStackTrace();
                            all = new ArrayList<>();
                            typeObjects.put(entityId, all);
                        }
                    }
                    entityInfo.all = FXCollections.observableArrayList(all);
                    entityLists.put(entity, entityInfo);
                }
                final EntityInfo ei = entityInfo;

                text.setOnMousePressed(e -> {
                    selectedEntity = ei;
                    filterObjects(ei);
                    contextMenu.show(text, Side.BOTTOM, 0, 0);
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
        rankedListView.setCellFactory(new Callback<ListView<Pair<Double, Object>>, ListCell<Pair<Double, Object>>>() {
            @Override
            public ListCell<Pair<Double, Object>> call(ListView<Pair<Double, Object>> p) {
                return new ListCell<Pair<Double, Object>>() {
                    {
                        this.setOnMouseClicked((MouseEvent e) -> {
                            contextMenu.hide();
                            @SuppressWarnings("unchecked")
                            final Pair<Double, Object> p = ((ListCell<Pair<Double, Object>>) e.getSource()).getItem();
                            pipeline.getReportEntities().get(selectedEntity.index).setCandidate(p.getSecond());
                        });
                        this.setOnMouseEntered(e -> {
                            @SuppressWarnings("unchecked")
                            final Pair<Double, Object> p = ((ListCell<Pair<Double, Object>>) e.getSource()).getItem();
                            if (p != null) {
                                rankedTooltip.setText(p.getSecond().toString());
                                rankedListView.setTooltip(rankedTooltip);
                            }
                        });
                        this.setOnMouseExited(e -> {
                            rankedListView.setTooltip(null);
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
                            setText(shorter(p.getFirst().toString() + ": " + p.getSecond().toString()));
                        }
                    }
                };
            }
        });
        allListView.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell<Object> call(ListView<Object> p) {
                return new ListCell<Object>() {
                    {
                        this.setOnMouseClicked((MouseEvent t) -> {
                            contextMenu.hide();
                            @SuppressWarnings("unchecked")
                            final Object o = ((ListCell<Object>) t.getSource()).getItem();
                            pipeline.getReportEntities().get(selectedEntity.index).setCandidate(o);
                        });
                        this.setOnMouseEntered(e -> {
                            @SuppressWarnings("unchecked")
                            final Object o = ((ListCell<Object>) e.getSource()).getItem();
                            if (o != null) {
                                allTooltip.setText(o.toString());
                                allListView.setTooltip(allTooltip);
                            }
                        });
                        this.setOnMouseExited(e -> {
                            allListView.setTooltip(null);
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
        newListView.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell<Object> call(ListView<Object> p) {
                return new ListCell<Object>() {
                    {
                        this.setOnMouseClicked((MouseEvent t) -> {
                            contextMenu.hide();
                            @SuppressWarnings("unchecked")
                            final Object o = ((ListCell<Object>) t.getSource()).getItem();
                            pipeline.getReportEntities().get(selectedEntity.index).setCandidate(o);
                        });
                        this.setOnMouseEntered(e -> {
                            @SuppressWarnings("unchecked")
                            final Object o = ((ListCell<Object>) e.getSource()).getItem();
                            if (o != null) {
                                newTooltip.setText(o.toString());
                                newListView.setTooltip(allTooltip);
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
        rankedListView.setItems(entityInfo.ranked.filtered(p -> {
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

        allListView.setItems(entityInfo.all.filtered((Object o) -> {
            final String filter = filterField.getText();
            if (filter == null || filter.isEmpty()) {
                return true;
            }
            if (o == null) {
                return false;
            }
            final String aliases = String.join(", ", o.getAliases());
            return aliases.toLowerCase().contains(filter.toLowerCase());
        }));
        newListView.setItems(entityInfo.all.filtered((Object o) -> {
            return o.getType().getId() == selectedEntity.type;
        }));
    }

    /**
     * Simple holder of information about entity to provide the right
     * set of object to chose from.
     */
    static class EntityInfo {
        int index;
        long type;
        ObservableList<Pair<Double, Object>> ranked;
        ObservableList<Object> all;
    }
}
