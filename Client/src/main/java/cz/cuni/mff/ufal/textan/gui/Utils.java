package cz.cuni.mff.ufal.textan.gui;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
    public final static String CONTEXT_MENU_STYLE = "-fx-font-size: 12";

    /** Ids can be belong to objects or relations. */
    public enum IdType {
        ENTITY {
            @Override
            public long transformId(long id) {
                return id;
            }
        }, OBJECT {
            @Override
            public long transformId(long id) {
                return id;
            }
        }, RELATION {
            @Override
            public long transformId(long id) {
                return ~id;
            }
        };

        /**
         * Transforms id for color extracting.
         * @param id id to transform
         * @return transfored id
         */
        public abstract long transformId(long id);
    }

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
    static public Color idToFXColor(final long id) {
        final int color = idToColor(id);
        return Color.rgb((color & 0xFF0000) >> 16, (color & 0x00FF00) >> 8, color & 0x0000FF);
    }

    /**
     * Converts id to Color.
     * @param id long to covert
     * @return Color created from id hash
     */
    static public java.awt.Color idToAWTColor(final long id) {
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
     * Uses resourceBundle to localize key.
     * If not found, logs it and returns defaultVal.
     * @param resourceBundle localization container
     * @param key localization key
     * @param defaultVal default value
     * @return localization or key if not found
     */
    static public String localize(final ResourceBundle resourceBundle,
            final String key, final String defaultVal) {
        try {
            return resourceBundle.getString(key);
        } catch(NullPointerException | MissingResourceException e) {
            System.err.printf("Localization string \"%1$s\" not found!\n", key);
            return defaultVal;
        }
    }

    /**
     * Uses client to get filtered objects, fills outputList.
     * @param client client for communication with the server
     * @param controller caller, needed for displaying errors
     * @param lock synchronization lock
     * @param mainNode node to set cursor
     * @param root controller root for displaying errors
     * @param selectedType object type of filtered objects
     * @param filter alias filter
     * @param size maximal size of the output
     * @param pageNo current page number
     * @param resourceBundle resource bundle to localize pagination label or errors
     * @param paginationLabel label displaying pagination info
     * @param consumer gets notified with totalObjectCount and page count
     * @param outputList objects are stored here
     */
    static public void filterObjects(
            final Client client,
            final WindowController controller,
            final Semaphore lock,
            final Node mainNode,
            final Node root,
            final ObjectType selectedType,
            final String filter,
            final int size,
            final int pageNo,
            final ResourceBundle resourceBundle,
            final Label paginationLabel,
            final BiConsumer<Integer, Integer> consumer,
            final List<Object> outputList) {
        if (lock.tryAcquire()) {
            mainNode.setCursor(Cursor.WAIT);
            final int first = size * pageNo;
            final Task<Pair<List<Object>, Integer>> task = new Task<Pair<List<Object>, Integer>>() {
                @Override
                protected Pair<List<cz.cuni.mff.ufal.textan.core.Object>, Integer> call() throws Exception {
                    Pair<List<cz.cuni.mff.ufal.textan.core.Object>, Integer> pair =
                            client.getObjectsList(selectedType, filter, first, size);
                    pair.getFirst().sort((obj1, obj2) -> Long.compare(obj1.getId(), obj2.getId()));
                    return pair;
                }
            };
            task.setOnSucceeded(e -> {
                final Pair<List<Object>, Integer> pair =
                        task.getValue();
                outputList.clear();
                outputList.addAll(FXCollections.observableList(pair.getFirst()));
                final int objectCount = pair.getSecond();
                int pageCount = (int) Math.ceil(1.0 * pair.getSecond() / size);
                if (pageCount == 0) {
                    pageCount = 1;
                }
                final String format = Utils.localize(resourceBundle, "pagination.label");
                paginationLabel.setText(String.format(format, pageNo + 1, pageCount));
                consumer.accept(objectCount, pageCount);
                mainNode.setCursor(Cursor.DEFAULT);
                lock.release();
            });
            task.setOnFailed(e -> {
                mainNode.setCursor(Cursor.DEFAULT);
                lock.release();
                controller.callWithContentBackup(() -> {
                    controller.createDialog()
                            .owner(controller.getDialogOwner(root))
                            .title(Utils.localize(resourceBundle, "page.load.error"))
                            .showException(task.getException());
                });
                lock.release();
            });
            new Thread(task, "ObjectFilter").start();
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
     * Resolves color for type and id.
     * @param settings application settings
     * @param type id type
     * @param id id to resolve
     * @return color for type and id
     */
    static public java.awt.Color resolveColorAWT(final Properties settings,
            final IdType type, final long id) {
        final String c = settings.getProperty(type + ".color." + id);
        if (c == null) {
            return idToAWTColor(type.transformId(id));
        }
        try {
            return java.awt.Color.decode(c);
        } catch (Exception e) {
            return idToAWTColor(type.transformId(id));
        }
    }

    /**
     * Resolves color for object id
     * @param settings application settings
     * @param id id to resolve
     * @return color for id
     */
    static public java.awt.Color resolveEntityColorAWT(final Properties settings,
            final long id) {
        return resolveColorAWT(settings, IdType.ENTITY, id);
    }

    /**
     * Resolves color for object id
     * @param settings application settings
     * @param id id to resolve
     * @return color for id
     */
    static public java.awt.Color resolveRelationColorAWT(final Properties settings,
            final long id) {
        return resolveColorAWT(settings, IdType.RELATION, id);
    }

    /**
     * Resolves color for type and id.
     * @param settings application settings
     * @param type id type
     * @param id id to resolve
     * @return color for type and id
     */
    static public Color resolveColorFX(final Properties settings,
            final IdType type, final long id) {
        final String c = settings.getProperty(type + ".color." + id);
        if (c == null) {
            return idToFXColor(type.transformId(id));
        }
        try {
            return Color.valueOf(c);
        } catch (Exception e) {
            return idToFXColor(type.transformId(id));
        }
    }

    /**
     * Resolves color for object id
     * @param settings application settings
     * @param id id to resolve
     * @return color for id
     */
    static public Color resolveEntityColorFX(final Properties settings,
            final long id) {
        return resolveColorFX(settings, IdType.ENTITY, id);
    }

    /**
     * Resolves color for object id
     * @param settings application settings
     * @param id id to resolve
     * @return color for id
     */
    static public Color resolveRelationColorFX(final Properties settings,
            final long id) {
        return resolveColorFX(settings, IdType.RELATION, id);
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
     * @param settings application settings
     * @param text text to style
     * @param clazz class to add
     * @param type id type
     * @param id id to color
     */
    static public void styleText(final Properties settings, final Text text,
            final String clazz, final IdType type, final long id) {
        text.getStyleClass().clear();
        text.getStyleClass().add(clazz);
        text.setUserData(text.getFill());
        final Color color = type == IdType.OBJECT ? idToFXColor(id) : resolveColorFX(settings, type, id);
        text.setFill(color);
    }

    /**
     * Adds background to text created from id.
     * @param settings application settings
     * @param text text to style
     * @param id id to color
     */
    static public void styleTextBackground(final Properties settings,
            final Text text, final long id) {
        final DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(4d);
        dropShadow.setSpread(4d);
        dropShadow.setOffsetX(0d);
        dropShadow.setOffsetY(0d);
        dropShadow.setColor(resolveRelationColorFX(settings, id));
        text.setEffect(dropShadow);
    }

    /**
     * Clears text's style classes and tries to restore fill color from user
     * data.
     * @param text text to unstyle
     */
    static public void unstyleText(final Text text) {
        text.getStyleClass().clear();
        java.lang.Object color = text.getUserData();
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
