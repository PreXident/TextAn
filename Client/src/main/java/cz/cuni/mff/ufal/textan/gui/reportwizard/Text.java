package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.Utils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Parent;

/**
 * Text that calls impl_geomChanged() on invalidating style class.
 */
public class Text extends javafx.scene.text.Text {

    /**
     * Only constructor.
     * @param text text value
     */
    public Text(final String text) {
        super(text);
        getStyleClass().addListener((Observable o) -> {
//            final Parent parent = getParent();
//            if (getParent() instanceof TextFlow) {
//                ((TextFlow) parent).invalidateChildrenGeomLater();
//            }
            Utils.runFXlater(() -> {
                invalidateGeom();
            });
        });
    }

    /**
     * Calls impl_geomChanged().
     */
    @SuppressWarnings("deprecation")
    public final void invalidateGeom() {
        impl_geomChanged();
    }
}
