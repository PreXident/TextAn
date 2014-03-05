package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Controls editing the report relations.
 */
public class ReportRelationsController extends ReportWizardController {

    @FXML
    TextFlow textFlow;

    @FXML
    ScrollPane scrollPane;

    /** ScrollPane's tooltip. */
    Tooltip tooltip = new Tooltip("");

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
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        final List<Text> texts = new ArrayList<>();
        for (final Word word: pipeline.getReportWords()) {
            final Text text = new Text(word.getWord());
            if (word.getEntity() != null) {
                final int entityId = word.getEntity().getId();
                text.getStyleClass().add("ENTITY_" + entityId);
            }
            text.setOnMouseEntered((MouseEvent t) -> {
                if (word.getEntity() != null) {
                    scrollPane.setTooltip(tooltip);
                    final String oldTip = scrollPane.getTooltip().getText();
                    final cz.cuni.mff.ufal.textan.commons.models.Object obj = pipeline.getReportObjects()[word.getEntity().getIndex()];
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
