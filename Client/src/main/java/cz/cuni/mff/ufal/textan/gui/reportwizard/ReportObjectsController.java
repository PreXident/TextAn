package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.commons_old.models.Object;
import cz.cuni.mff.ufal.textan.core.processreport.EntityBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
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

    /** Index of selected entity. */
    int selectedEntity = 0;

    @FXML
    private void cancel() {
        closeContainer();
    }

    @FXML
    private void next() {
        callWithContentBackup(() ->
            createDialog()
                    .owner(getDialogOwner(root))
                    .title(Utils.localize(resourceBundle, "done"))
                    .message(Utils.localize(resourceBundle, "done.detail"))
                    .showInformation());
        closeContainer();
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
                text.getStyleClass().add("ENTITY_" + entityId);

                ContextMenu cm = menus.get(word.getEntity());
                if (cm == null) {
                    cm = new ContextMenu();
                    Object[] cands = pipeline.getClient().getDataProvider().getObjectsByTypeId(entityId);
                    for (final Object cand : cands) {
                        final MenuItem mi = new MenuItem(cand.toString());
                        mi.setOnAction((ActionEvent t) -> {
                            pipeline.getReportObjects()[entityId] = cand;
                        });
                        cm.getItems().add(mi);
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
                    final String oldTip = scrollPane.getTooltip().getText();
                    final Object obj = pipeline.getReportObjects()[word.getEntity().getIndex()];
                    if (obj != null) {
                        final String newTip = obj.toString();
                        if (!newTip.equals(oldTip)) {
                            scrollPane.getTooltip().setText(newTip);
                        }
                    }
                } else {
                    scrollPane.getTooltip().setText("");
                }
            });
            texts.add(text);
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(texts);
    }
}
