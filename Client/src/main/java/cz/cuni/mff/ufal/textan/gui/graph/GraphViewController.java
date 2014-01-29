package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Controls GraphView.
 */
public class GraphViewController extends WindowController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    /** Graph information provider. */
    protected Grapher grapher;

    @FXML
    private void cancel() {
        closeContainer();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //localization
        stackPane.prefWidthProperty().bind(scrollPane.widthProperty());
        stackPane.prefHeightProperty().bind(scrollPane.heightProperty());
    }

    /**
     * Sets graph information provider.
     * @param grapher new graph information provider
     */
    public void setGrapher(final Grapher grapher) {
        this.grapher = grapher;
        stackPane.getChildren().add(new GraphView());
        //scrollPane.setContent(new GraphView());
    }
}
