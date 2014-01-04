package cz.cuni.mff.ufal.textan.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;
import org.controlsfx.dialog.Dialogs;

/**
 * Abstract ancestor for window controllers.
 */
public abstract class WindowController implements Initializable {

    /**
     * Properties containing application settings.
     * Handle with care, they are shared!
     */
    protected Properties settings = null;

    /** Window displaying the view. It can be null if in {@link Stage}. */
    protected Window window = null;

    /** Stage displaying the view. It can be null if in {@link Window}. */
    protected Stage stage = null;

    /**
     * Returns suitable owner of a lightweight dialog.
     * Eg. {@link #stage} if it is not null, parameter root otherwise.
     * @param root return value if stage is null
     * @return suitable owner of a lightweight dialog
     */
    protected Object getDialogOwner(final Object root) {
        return stage == null ? root : stage;
    }

    /**
     * Set settings.
     * @param settings new settings
     */
    public void setSettings(final Properties settings) {
        this.settings = settings;
    }

    /**
     * Sets the window to be controlled.
     * @param window Window to be controlled
     */
    public void setWindow(final Window window) {
        this.window = window;
    }

    /**
     * Sets the stage to be controlled.
     * @param stage Stage to be controlled
     */
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
     * Calls the runnable; if in {@link Window} it is done with content backup
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
     * Creates new Dialogs. Result is lightweight if in {@link Window}.
     * @return newly created Dialogs, lightweight if in Window
     */
    protected Dialogs createDialog() {
        final Dialogs result = Dialogs.create();
        return window != null ? result.lightweight() : result;
    }
}
