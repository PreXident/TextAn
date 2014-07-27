package cz.cuni.mff.ufal.textan.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Stage;
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
    protected InnerWindow window = null;

    /** Stage displaying the view. It can be null if in {@link Window}. */
    protected OuterStage stage = null;

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
     * Returns content Pane of window or stage's InnerWindow.
     * @return content Pane of window or stage's InnerWindow
     */
    protected Node getMainNode() {
        return stage == null ? window.getContentPane() : stage.getInnerWindow().getContentPane();
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
    public void setWindow(final InnerWindow window) {
        this.window = window;
        window.getContentPane().setCursor(Cursor.DEFAULT);
        window.setContainerCloser(getContainerCloser());
    }

    /**
     * Sets the stage to be controlled.
     * @param stage Stage to be controlled
     */
    public void setStage(final OuterStage stage) {
        this.stage = stage;
        stage.getInnerWindow().getContentPane().setCursor(Cursor.DEFAULT);
        stage.getInnerWindow().setContainerCloser(getContainerCloser());
    }

    /**
     * Closes the {@link #window} or {@link #stage}.
     * Gets called on close button click.
     */
    protected void closeContainer() {
        if (window != null) {
            window.close();
        } else /* if (stage != null) */ {
            stage.close();
        }
    }

    /**
     * Returns runnable that handles closing the container.
     * @return runnable that handles closing the container
     */
    public Runnable getContainerCloser() {
        return () -> closeContainer();
    }

    protected <T> T callWithContentBackup(final Callable<T> c) throws Exception {
        if (window != null) {
            window.setResizableWindow(false);
            final List<Node> backup = new ArrayList<>(window.getContentPane().getChildren());
            final T result = c.call();
            window.getContentPane().getChildren().clear();
            window.getContentPane().getChildren().addAll(backup);
            window.setResizableWindow(true);
            return result;
        } else {
            return c.call();
        }
    }

    /**
     * Calls the runnable; if in {@link Window} it is done with content backup
     * and restore. It is intended for displaying lightweight dialogs that mess
     * up the controls a bit in windows.
     * @param r code to run
     */
    protected void callWithContentBackup(final Runnable r) {
        try {
            callWithContentBackup(() -> {
                r.run();
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("This should never happen!");
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
