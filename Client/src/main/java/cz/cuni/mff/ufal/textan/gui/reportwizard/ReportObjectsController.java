package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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

    /** ScrollPane's tooltip. */
    Tooltip tooltip = new Tooltip("");

    /** Index of selected entity. */
    int selectedEntity = 0;

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
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        final List<Text> texts = new ArrayList<>();
        final Map<EntityBuilder, ContextMenu> menus = new HashMap<>();
        for (final Word word: pipeline.getReportWords()) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                final int entityId = word.getEntity().getId();
                final int entityIndex = word.getEntity().getIndex();
                Utils.styleText(text, "ENTITY", entityId);

                ContextMenu cm = menus.get(word.getEntity());
                if (cm == null) {
                    cm = new ContextMenu();
                    Map<Double, Object> candidates = pipeline.getReportEntities().get(word.getEntity().getIndex()).getCandidates();
                    for (Entry<Double, Object> candidate : candidates.entrySet()) {
                        final Object cand = candidate.getValue();
                        final double rating = candidate.getKey();
                        final MenuItem mi = new MenuItem(rating + ": " + cand.toString());
                        mi.setOnAction((ActionEvent t) -> {
                            pipeline.getReportEntities().get(entityIndex).setCandidate(cand);
                        });
                        cm.getItems().add(mi);
                    }
                    cm.getItems().add(new SeparatorMenuItem());
                    try {
                        List<Object> cands = pipeline.getClient().getObjectsListByTypeId(entityId);
                        for (final Object cand : cands) {
                            final MenuItem mi = new MenuItem(cand.toString());
                            mi.setOnAction((ActionEvent t) -> {
                                pipeline.getReportEntities().get(entityIndex).setCandidate(cand);
                            });
                            cm.getItems().add(mi);
                        }
                    } catch (IdNotFoundException e) {
                        e.printStackTrace();
                    }
                    menus.put(word.getEntity(), cm);
                }
                final ContextMenu finalCM = cm;
                text.setOnMousePressed(e -> {
                    finalCM.show(text, Side.BOTTOM, 0, 0);
                });
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
            texts.add(text);
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(texts);
    }
}
