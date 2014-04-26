package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Controls GraphView.
 */
public class GraphViewController extends GraphController {

    @FXML
    private BorderPane root;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    /** Localization container. */
    ResourceBundle resourceBundle;

    @FXML
    private void cancel() {
        closeContainer();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //localization
        this.resourceBundle = rb;
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
        try {
            final Graph g = grapher.getGraph();
            final GraphView graphView = new GraphView(settings,
                    g.getNodes(), g.getEdges(), grapher.getRootId());
            stackPane.getChildren().add(graphView);
        } catch (IdNotFoundException e) {
            e.printStackTrace();
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title("This should have never happened!")
                        .showException(e);
            });
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() ->
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "page.load.error"))
                        .showException(e);
            }));
        }
    }
}
