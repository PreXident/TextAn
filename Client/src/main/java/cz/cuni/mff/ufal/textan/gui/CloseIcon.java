package cz.cuni.mff.ufal.textan.gui;

import jfxtras.labs.scene.control.window.WindowIcon;

/**
 * Icon for closing Windows.
 */
public class CloseIcon extends WindowIcon {

    /** CSS style for the icons. */
    public static final String DEFAULT_STYLE_CLASS = "window-close-icon";

    /**
     * Only constructor.
     * @param w ReportWizardWindow to be minimized on action
     */
    public CloseIcon(final Window w) {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        setOnAction(e -> w.closeContainer());
    }
}
