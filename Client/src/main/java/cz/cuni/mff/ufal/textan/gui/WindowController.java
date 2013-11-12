package cz.cuni.mff.ufal.textan.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;
import org.controlsfx.dialog.Dialogs;

/**
 *
 */
public abstract class WindowController implements Initializable {

    /** Properties containing application settings. */
    protected Properties settings = null;

    /** Window displaying the view. It can be null if in Stage. */
    protected Window window = null;

    /** Stage displaying the view. It can be null if in Window. */
    protected Stage stage = null;

    /**
     * Returns suitable owner of a lightweight dialog. Eg. {@link #stage} if it is not
     * null, parameter root otherwise.
     * @param root return value if stage is null
     * @return suitable owner of a lightweight dialog
     */
    protected Object getDialogOwner(final Object root) {
        return stage == null ? root : stage;
    }

    public void setSettings(final Properties settings) {
        this.settings = settings;
    }

    public void setWindow(final Window window) {
        this.window = window;
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Closes the {@link #window} or {@link #stage}.
     */
    protected void closeContainer() {
        if (window != null) {
            window.close();
        } else /* if (stage != null) */ {
            stage.close();
        }
    }

    /**
     * Calls the runnable; if in window it is done with content backup
     * and restore. It is intended for displaying lightweight dialogs that mess
     * up the controls a bit in windows.
     * @param r code to run
     */
    protected void callWithContentBackup(final Runnable r) {
        if (window != null) {
            final List<Node> backup = new ArrayList<>(window.getContentPane().getChildren());
            r.run();
            window.getContentPane().getChildren().clear();
            window.getContentPane().getChildren().addAll(backup);
        } else {
            r.run();
        }
    }

    /**
     * Loads the next frame of the wizard.
     * @param <T> type of controller of the next frame
     * @param fxml file containing the next frame
     * @return controller of the next frame
     */
    protected <T extends WindowController> T nextFrame(final String fxml) {
        T controller = null;
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            final Parent loadedRoot = (Parent) loader.load();
            controller = loader.getController();
            controller.setSettings(settings);
            if (window != null) {
                window.getContentPane().getChildren().clear();
                controller.setWindow(window);
                window.getContentPane().getChildren().add(loadedRoot);
            } else /* if (stage != null) */ {
                controller.setStage(stage);
                stage.getScene().setRoot(loadedRoot);
            }
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
