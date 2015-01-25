package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.Utils;
import javafx.scene.Node;

/**
 * TextFlow that expose layoutChildren method.
 */
public class TextFlow extends javafx.scene.text.TextFlow {

    /**
     * Flag indicating whether the fixing children bounds is in progress.
     * This field should be accessed from FX thread only!
     */
    protected boolean fixing = false;

    /**
     * Fixes the children Text nodes bounds.
     */
    public void fixChildrenBounds() {
        for (Node n: getChildren()) {
            if (n instanceof Text) {
                ((Text) n).fixBounds();
            }
        }
    }

    /**
     * Fixes the children Text nodes bounds if not already in progress.
     * This method should be called from FX main thread only!
     */
    public void fixChildrenBoundsLater() {
        if (!fixing) {
            fixing = true;
            Utils.runFXlater(() -> {
                fixChildrenBounds();
                fixing = false;
            });
        }
    }

    @Override
    public void layoutChildren() {
        fixChildrenBounds();
        super.layoutChildren();
    }
}
