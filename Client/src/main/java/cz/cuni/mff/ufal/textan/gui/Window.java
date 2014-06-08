/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.gui;

/**
 * Simple window extension to provide unified closing.
 */
public class Window extends jfxtras.labs.scene.control.window.Window {

    /** Runnable called on close button click. */
    protected Runnable containerCloser = null;

    /**
     * Only constructor.
     * @param title window title
     */
    public Window(final String title) {
        super(title);
        this.getRightIcons().add(new CloseIcon(this));
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
}
