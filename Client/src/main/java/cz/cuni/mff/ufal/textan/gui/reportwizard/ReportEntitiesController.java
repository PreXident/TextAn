package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons.models.ObjectType;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder.SplitEntitiesException;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.utils.Pair;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
    Word[] words;

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
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        final List<Text> texts = new ArrayList<>();
        words = pipeline.getReportWords();
        for (Word word: words) {
            final Text text = new Text(word.getWord());
            text.setOnMousePressed(e -> {
                if (!text.getStyleClass().contains(SELECTED)) {
                    //System.out.println("pressed");
                    removeSelectedClass(texts);
                    dragging = true;
                    firstDragged = texts.indexOf(text);
                    lastDragged = firstDragged;
                    firstSelectedIndex = firstDragged;
                    lastSelectedIndex = firstDragged;
                    text.getStyleClass().add(SELECTED);
                    //text.setMouseTransparent(true);
                }
            });
            text.setOnDragDetected(e -> {
                text.startFullDrag();
            });
            text.setOnMouseDragEntered(e -> {
                if (dragging) {
                    //System.out.println("dragged");
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
                    //System.out.println("released");
                    dragging = false;
                    contextMenu.show(texts.get(lastDragged), Side.BOTTOM, 0, 0);
                    //text.setMouseTransparent(false);
                }
            });
            texts.add(text);
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(texts);
        //
        final EventHandler<ActionEvent> eh = (ActionEvent t) -> {
            final int id = (Integer)((MenuItem) t.getSource()).getUserData();
            final EntityBuilder e = new EntityBuilder(id);
            try {
                Pair<Integer, Integer> bounds = e.add(words, firstSelectedIndex, lastSelectedIndex, i -> texts.get(i).getStyleClass().clear());
                for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
                    texts.get(i).getStyleClass().clear();
                    texts.get(i).getStyleClass().add("ENTITY_" + id);
                }
            } catch (SplitEntitiesException ex) {
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
        for (ObjectType objType : pipeline.getClient().getDataProvider().getObjectTypes()) {
            final MenuItem mi = new MenuItem(objType.getName());
            mi.setOnAction(eh);
            mi.setUserData(objType.getId());
            contextMenu.getItems().add(mi);
        }
    }
}
