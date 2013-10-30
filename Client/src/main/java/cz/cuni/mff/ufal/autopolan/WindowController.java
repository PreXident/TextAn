package cz.cuni.mff.ufal.autopolan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import jfxtras.labs.scene.control.window.Window;
import org.controlsfx.dialog.Dialogs;

/**
 *
 */
public abstract class WindowController implements Initializable {

    /** Properties containing application settings. */
    protected Properties settings = null;

    /** Window displaying the view. */
    protected Window window = null;

    public void setSettings(final Properties settings) {
        this.settings = settings;
    }

    public void setWindow(final Window window) {
        this.window = window;
    }

    protected void callWithContentBackup(final Runnable r) {
        final List<Node> backup = new ArrayList<>(window.getContentPane().getChildren());
        r.run();
        window.getContentPane().getChildren().clear();
        window.getContentPane().getChildren().addAll(backup);
    }

    protected <T extends WindowController> T nextFrame(final String fxml) {
        T controller = null;
        window.getContentPane().getChildren().clear();
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            final Parent loadedRoot = (Parent) loader.load();
            controller = loader.getController();
            controller.setSettings(settings);
            controller.setWindow(window);
            window.getContentPane().getChildren().add(loadedRoot);
        } catch (IOException e) {
            e.printStackTrace();
            Dialogs.create()
                    .title("Problém při načítání další stránky!")
                    .lightweight()
                    .showException(e);
        }
        return controller;
    }
}
