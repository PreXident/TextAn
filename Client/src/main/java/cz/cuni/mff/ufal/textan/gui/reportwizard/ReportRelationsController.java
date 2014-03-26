package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

/**
 * Controls editing the report relations.
 */
public class ReportRelationsController extends ReportWizardController {

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

    @FXML
    private void cancel() {
        closeContainer();
    }

    @FXML
    private void next() {
        pipeline.setReportRelations(pipeline.getReportRelations());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resourceBundle = rb;
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty());
        objectColumn.prefWidthProperty().bind(table.widthProperty().add(orderColumn.prefWidthProperty().multiply(-1)));
        orderColumn.setCellValueFactory(new Callback<CellDataFeatures<RelationInfo, Number>, ObservableValue<Number>>() {
            @Override
            public ObservableValue<Number> call(CellDataFeatures<RelationInfo, Number> p) {
                return p.getValue().order;
            }
         });
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        final List<Text> texts = new ArrayList<>();
        final List<Word> words = pipeline.getReportWords();
        for (final Word word: words) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                final int entityId = word.getEntity().getId();
                text.getStyleClass().add("ENTITY_" + entityId);
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
                    table.setItems(word.getRelation().data);
                } else {
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
            final Integer ID = (Integer)((MenuItem) t.getSource()).getUserData();
            if (ID == null) {
                for (int i = firstSelectedIndex; i <= lastSelectedIndex; ++i) {
                    words.get(i).setRelation(null);
                    texts.get(i).getStyleClass().clear();
                }
                return;
            }
            final int id = ID;
            final RelationBuilder e = new RelationBuilder(id);
            try {
                Pair<Integer, Integer> bounds = e.add(words, firstSelectedIndex, lastSelectedIndex, i -> texts.get(i).getStyleClass().clear());
                for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
                    texts.get(i).getStyleClass().clear();
                    texts.get(i).getStyleClass().add("RELATION_" + id);
                }
                table.setItems(e.data);
            } catch (RelationBuilder.SplitEntitiesException ex) {
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
            mi.setUserData(relType.getId()); //relation type id to userdata
            contextMenu.getItems().add(mi);
        }
    }

    public static class RelationInfo {
        public SimpleIntegerProperty order = new SimpleIntegerProperty();
        public SimpleObjectProperty<cz.cuni.mff.ufal.textan.core.Object> object =
                new SimpleObjectProperty<>();

        public RelationInfo(int order, cz.cuni.mff.ufal.textan.core.Object object) {
            this.order.set(order);
            this.object.set(object);
        }
    }
}
