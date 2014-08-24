package cz.cuni.mff.ufal.textan.gui;

import javafx.scene.layout.VBox;

/**
 * Simply extends VBox to get to computePref* methods.
 */
public class ExposingVBox extends VBox {

    @Override
    public double computePrefHeight(final double d) {
        return super.computePrefHeight(d);
    }
    
    @Override
    public double computePrefWidth(final double d) {
        return super.computePrefWidth(d);
    }
}
