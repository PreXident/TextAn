package cz.cuni.mff.ufal.textan.gui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/**
 * Simple window extension to provide unified closing.
 */
public class Window extends jfxtras.labs.scene.control.window.Window {

    /** Key combination to close the window/container. */
    static protected final KeyCodeCombination CTRL_F4 =
            new KeyCodeCombination(KeyCode.F4, KeyCombination.CONTROL_DOWN);

    /**
     * Maximal length of titles considered short, thus not needing fixing.
     * @see #setTitleFixed(String)
     */
    protected final static int SHORT_ENOUGH = getShortEnough();

    /**
     * Returns system property "textan.window.title.short" if valid, otherwise
     * completely arbitrary number.
     * @return value for {@link #SHORT_ENOUGH}
     */
    static private int getShortEnough() {
        try {
            final String s = System.getProperty("textan.window.title.short", "20");
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 20;
        }
    }

    /** Runnable called on close button click. */
    protected Runnable containerCloser = null;

    /**
     * Only constructor.
     * @param title window title
     */
    public Window(final String title) {
        super(title);
        this.getRightIcons().add(new CloseIcon(this));
        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (CTRL_F4.match(e)) {
                this.closeContainer();
            }
        });
    }

    /**
     * Calls {@link #closeContainer} if set.
     */
    public void closeContainer() {
        if (containerCloser != null) {
            containerCloser.run();
        }
    }

    /**
     * Runnable called on close button click.
     * @return the {@link #containerCloser}
     */
    public Runnable getContainerCloser() {
        return containerCloser;
    }

    /**
     * Sets runnable called on close button click.
     * @param containerCloser the {@link #containerCloser} to set
     */
    public void setContainerCloser(Runnable containerCloser) {
        this.containerCloser = containerCloser;
    }

    /**
     * Uses hack to handle bad recognition of title length on Unix.
     * @param title new title
     */
    public void setTitleFixed(final String title) {
        final boolean shortEnough =
                title == null ? true : title.length() <= SHORT_ENOUGH;
        if (Utils.isWindows || shortEnough) {
            this.setTitle(title);
        } else {
            Utils.runFXlater(() -> {
                this.setTitle(title);
            });
        }
    }
}
