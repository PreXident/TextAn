package cz.cuni.mff.ufal.textan.gui.reportwizard;

import javafx.event.ActionEvent;
import jfxtras.labs.scene.control.window.WindowIcon;

/**
 * Icon for maximizing inner windows.
 */
public class MaximizeIcon extends WindowIcon {

    /** CSS style for the icons. */
    public static final String DEFAULT_STYLE_CLASS = "window-minimize-icon";

    /**
     * Only constructor.
     * @param w ReportWizardWindow to be minimized on action
     */
    public MaximizeIcon(final ReportWizardWindow w) {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        setOnAction((ActionEvent t) -> { if (w.isResizableWindow()) w.toggleMaximize(); });
    }
}
