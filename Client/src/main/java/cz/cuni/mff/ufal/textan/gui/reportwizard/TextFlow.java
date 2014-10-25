package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.Utils;
import javafx.scene.Node;

/**
 * TextFlow that expose layoutChildren method.
 */
public class TextFlow extends javafx.scene.text.TextFlow {

    protected boolean invalidating = false;

    public void invalidateChildrenGeomLater() {
        if (!invalidating) {
            invalidating = true;
            Utils.runFXlater(() -> {
                for (Node n: getChildren()) {
                    if (n instanceof Text) {
                        ((Text) n).invalidateGeom();
                    }
                }
                invalidating = false;
            });
        }
    }

    @Override
    public void layoutChildren() {
        for (Node n: getChildren()) {
            if (n instanceof Text) {
                ((Text) n).invalidateGeom();
            }
        }
        super.layoutChildren();
    }
}
