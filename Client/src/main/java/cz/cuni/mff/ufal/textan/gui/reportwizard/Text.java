package cz.cuni.mff.ufal.textan.gui.reportwizard;

import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import java.lang.reflect.Method;
import javafx.beans.Observable;
import javafx.scene.Node;
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
            final Parent parent = getParent();
            if (getParent() instanceof TextFlow) {
                ((TextFlow) parent).fixChildrenBoundsLater();
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void impl_pickNodeLocal(PickRay localPickRay, PickResultChooser result) {
        fixBounds();
        super.impl_pickNodeLocal(localPickRay, result);
    }

    /**
     * Invalidates geometry by impl_geomChanged and calls package private
     * updateBounds method.
     */
    @SuppressWarnings("deprecation")
    protected void fixBounds() {
        impl_geomChanged();
        try {
            final Method method = Node.class.getDeclaredMethod("updateBounds");
            method.setAccessible(true);
            method.invoke(this);
        } catch (Exception e) { }
    }
}
