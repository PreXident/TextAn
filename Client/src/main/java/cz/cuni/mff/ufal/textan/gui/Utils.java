package cz.cuni.mff.ufal.textan.gui;

import java.util.Properties;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

/**
 * A few useful static methods.
 */
public class Utils {

    /**
     * Sets the position and size of the stage from the settings and creates
     * listeners to keep the settings updated.
     * @param stage stage to prepate
     * @param id identification in settings
     * @param settings application settings
     */
    static public void prepareStage(final Stage stage, final String id, final Properties settings) {
        stage.setWidth(Double.parseDouble(settings.getProperty(id + ".width", "300")));
        stage.setHeight(Double.parseDouble(settings.getProperty(id + ".height", "200")));
        stage.setX(Double.parseDouble(settings.getProperty(id + ".x", "0")));
        stage.setY(Double.parseDouble(settings.getProperty(id + ".y", "0")));
        //add listeners
        final ChangeListener<Number> sizeListener = (e, newVal, oldVal) -> {
            settings.setProperty(id + ".width", Double.toString(stage.getWidth()));
            settings.setProperty(id + ".height", Double.toString(stage.getHeight()));
            settings.setProperty(id + ".x", Double.toString(stage.getX()));
            settings.setProperty(id + ".y", Double.toString(stage.getY()));
        };
        stage.widthProperty().addListener(sizeListener);
        stage.heightProperty().addListener(sizeListener);
        stage.xProperty().addListener(sizeListener);
        stage.yProperty().addListener(sizeListener);
    }
}
