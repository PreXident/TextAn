package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.commons.models.Object;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class ObjectListController extends GraphController {

    @FXML
    private BorderPane root;

    @FXML
    private TextField distanceTextField;

    @FXML
    private ListView<Object> listView;

    @FXML
    private void cancel() {
        closeContainer();
    }

    @FXML
    private void next() {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            try {
                final int distance = Integer.parseInt(distanceTextField.getText());
                final Object selected = listView.getSelectionModel().getSelectedItem();
                grapher.setRootId(selected.getId());
                grapher.setDistance(distance);

                final FXMLLoader loader = new FXMLLoader(getClass().getResource("GraphView.fxml"));
                final Parent loadedRoot = (Parent) loader.load();
                final GraphViewController controller = loader.getController();
                if (window != null) {
                    controller.setWindow(window);
                    window.getContentPane().getChildren().clear();
                    window.getContentPane().getChildren().add(loadedRoot);
                } else {
                    controller.setStage(stage);
                    stage.getInnerWindow().getContentPane().getChildren().add(loadedRoot);
                }
                controller.setSettings(settings);
                controller.setGrapher(grapher);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title("Zadejte celé číslo!")
                            .showException(e);
                });
            } catch (Exception e) {
                e.printStackTrace();
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title("Došlo k chybě!")
                            .showException(e);
                });
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listView.setCellFactory((ListView<Object> p) -> {
            return new ListCell<Object>() {
                @Override
                protected void updateItem(Object obj, boolean bln) {
                    super.updateItem(obj, bln);
                    if (obj != null) {
                        setText(obj.getId() + " - " + String.join(",", obj.getAliases()));
                    }
                }
            };
        });
    }

    @Override
    public void setGrapher(final Grapher grapher) {
        super.setGrapher(grapher);
        listView.setItems(FXCollections.observableList(Arrays.asList(grapher.getObjects())));
    }
}
