package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.net.URL;
import java.util.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
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

    /** Localization container. */
    protected ResourceBundle resourceBundle;

    @FXML
    private void next() {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            try {
                final int distance = Integer.parseInt(distanceTextField.getText());
                final Object selected = listView.getSelectionModel().getSelectedItem();
                grapher.setRootId(selected.getId());
                grapher.setDistance(distance);

                final ResourceBundle rb = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.graph.GraphView");
                final FXMLLoader loader = new FXMLLoader(getClass().getResource("GraphView.fxml"), rb);
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
                            .title(Utils.localize(resourceBundle, "invalid.distance"))
                            .showException(e);
                });
            } catch (Exception e) {
                e.printStackTrace();
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title(Utils.localize(resourceBundle, "page.load.error"))
                            .showException(e);
                });
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
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
        final Node node = stage == null ? window.getContentPane() : stage.getInnerWindow();
        node.setCursor(Cursor.WAIT);
        final Task<List<Object>> task = new Task<List<Object>>() {
            @Override
            protected List<Object> call() throws Exception {
                final List<Object> list = grapher.getObjectsList();
                //FIXME: is the comparison right?
                list.sort((obj1, obj2) -> Long.compare(obj1.getId(), obj2.getId()));
                return list;
            }
        };
        task.setOnSucceeded(e -> {
            listView.setItems(FXCollections.observableList(task.getValue()));
            node.setCursor(Cursor.DEFAULT);
        });
        task.setOnFailed(e -> {
            node.setCursor(Cursor.DEFAULT);
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "page.load.error"))
                        .showException(task.getException());
            });
        });
        new Thread(task, "Grapher").start();
    }
}
