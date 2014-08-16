package cz.cuni.mff.ufal.textan.gui.about;

import cz.cuni.mff.ufal.textan.gui.ExposingVBox;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class AboutController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "About";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "about.view";

    /** Pref height of the join window. */
    static protected final int PREF_HEIGHT = 225;

    /** Pref width of the join window. */
    static protected final int PREF_WIDTH = 265;

    @FXML
    private ExposingVBox root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //nothing
    }

    @Override
    public void setStage(final OuterStage stage) {
        super.setStage(stage);
        Utils.runFXlater(() -> {
            stage.getInnerWindow().setPrefWidth(root.computePrefWidth(0) + 10);
            stage.getInnerWindow().setPrefHeight(root.computePrefHeight(0) + 40); //guessed titlebar height
        });
    }

    @Override
    public void setWindow(final InnerWindow window) {
        super.setWindow(window);
        Utils.runFXlater(() -> {
            window.setPrefWidth(root.computePrefWidth(0) + 10);
            window.setPrefHeight(root.computePrefHeight(0) + 40); //guessed titlebar height
        });
    }
}
