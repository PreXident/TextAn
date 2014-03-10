package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

/**
 * Controls GraphView.
 */
public class GraphViewController extends GraphController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

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
    @Override
    public void setGrapher(final Grapher grapher) {
        super.setGrapher(grapher);
        final Graph g = grapher.getGraph();
        final GraphView graphView = new GraphView(settings, g.getNodes(), g.getEdges());
        stackPane.getChildren().add(graphView);
    }
}
