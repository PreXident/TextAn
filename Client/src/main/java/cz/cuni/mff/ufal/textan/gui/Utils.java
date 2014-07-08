package cz.cuni.mff.ufal.textan.gui;

import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A few useful static methods.
 */
public class Utils {

    /** Style for Object context menus. */
    public final static String OBJECT_CONTEXT_MENU = "-fx-font-size: 12";

    /**
     * Converts id to Color.
     * @param id long to covert
     * @return Color created from id hash
     */
    static public int idToColor(long id) {
        id += 0x892405;
        int result = (int) id; //(int) (id >> 32 ^ id);
        result = ((result >>> 16) ^ result) * 0x45d9f3b;
        result = ((result >>> 16) ^ result) * 0x45d9f3b;
        result = ((result >>> 16) ^ result);
        return result;
    }

    /**
     * Converts id to Color.
     * @param id long to covert
     * @return Color created from id hash
     */
    static public Color idToFXColor(long id) {
        final int color = idToColor(id);
        return Color.rgb((color & 0xFF0000) >> 16, (color & 0x00FF00) >> 8, color & 0x0000FF);
    }

    /**
     * Converts id to Color.
     * @param id long to covert
     * @return Color created from id hash
     */
    static public java.awt.Color idToAWTColor(long id) {
        final int color = idToColor(id);
        return new java.awt.Color(color);
    }

    /**
     * Uses resourceBundle to localize key.
     * If not found, logs it and returns key back.
     * @param resourceBundle localization container
     * @param key localization key
     * @return localization or key if not found
     */
    static public String localize(final ResourceBundle resourceBundle, final String key) {
        try {
            return resourceBundle.getString(key);
        } catch(NullPointerException | MissingResourceException e) {
            System.err.printf("Localization string \"%1$s\" not found!\n", key);
            return key;
        }
    }

    /**
     * Sets the position and size of the stage from the settings and creates
     * listeners to keep the settings updated.
     * @param stage stage to prepate
     * @param id identification in settings
     * @param settings application settings
     * @param addListeners flag indicating whether size/position listeners should be added
     */
    static public void prepareStage(final Stage stage, final String id,
            final Properties settings, final boolean addListeners) {
        stage.setWidth(Double.parseDouble(settings.getProperty(id + ".width", "300")));
        stage.setHeight(Double.parseDouble(settings.getProperty(id + ".height", "200")));
        stage.setX(Double.parseDouble(settings.getProperty(id + ".x", "0")));
        stage.setY(Double.parseDouble(settings.getProperty(id + ".y", "0")));
        //add listeners
        if (addListeners) {
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

    /**
     * Runs finalizer in FX thread after 100 ms sleep in other thread.
     * @param finalizer runnable to be run
     */
    static public void runFXlater(final Runnable finalizer) {
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (Exception e) { }
            Platform.runLater(finalizer);
        }).start();
    }

    /**
     * If string is too long, returns its shortened variant that ends with ...
     * @param string string to shorter
     * @return shorter string
     */
    static public String shortString(final String string) {
        return shortString(string, 35, "...");
    }

    /**
     * If string is too long, returns its shortened variant that ends with
     * ellipse.
     * @param string string to shorter
     * @param maxLength maximal string length
     * @param ellipse ellipse to mark missing content
     * @return shorter string
     */
    static public String shortString(final String string, final int maxLength, final String ellipse) {
        return string.length() > maxLength ?
                string.substring(0, maxLength - ellipse.length()) + ellipse
                : string;
    }

    /**
     * Replaces text's styleclasses by clazz, stores fill color to user data and
     * fills text with color created from id.
     * @param text text to style
     * @param clazz class to add
     * @param id id to color
     */
    static public void styleText(final Text text, final String clazz, final long id) {
        text.getStyleClass().clear();
        text.getStyleClass().add(clazz);
        text.setUserData(text.getFill());
        text.setFill(idToFXColor(id));
    }

    /**
     * Adds background to text created from id.
     * @param text text to style
     * @param id id to color
     */
    static public void styleTextBackground(final Text text, final long id) {
        final DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(4d);
        dropShadow.setSpread(4d);
        dropShadow.setOffsetX(0d);
        dropShadow.setOffsetY(0d);
        dropShadow.setColor(idToFXColor(~id));
        text.setEffect(dropShadow);
    }

    /**
     * Clears text's style classes and tries to restore fill color from user
     * data.
     * @param text text to unstyle
     */
    static public void unstyleText(final Text text) {
        text.getStyleClass().clear();
        Object color = text.getUserData();
        if (color instanceof Paint) {
            text.setFill((Paint) color);
        } else {
            text.setFill(Color.BLACK); //wild guess
        }
    }

    /**
     * Clears text's background.
     * @param text text to unstyle
     */
    static public void unstyleTextBackground(final Text text) {
        text.setEffect(null);
    }

    /** Utility class, no instantiation. */
    private Utils() { }
}
