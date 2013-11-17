package cz.cuni.mff.ufal.textan.gui.reportwizard;

import javafx.event.ActionEvent;
import jfxtras.labs.scene.control.window.WindowIcon;

/**
 * Icon for maximizing inner windows.
 */
public class MaximizeIcon extends WindowIcon {

    public static final String DEFAULT_STYLE_CLASS = "window-minimize-icon";

    public MaximizeIcon(final ReportWizard w) {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        setOnAction((ActionEvent t) -> { w.toggleMaximize(); });
    }
}
