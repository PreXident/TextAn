package cz.cuni.mff.ufal.textan.gui;

import cz.cuni.mff.ufal.textan.commons.utils.EmptyResourceBundle;
import cz.cuni.mff.ufal.textan.commons.utils.UnclosableStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * TextAn application.
 */
public class TextAn extends Application {

    /** Path to default property file. The file should be inside the jar. */
    private static final String DEFAULT_JAR_PROPERTIES = "TextAn.defprops";

    /** Default path to property file. */
    private static final String DEFAULT_PROPERTIES = "TextAn.properties";

    /** Bundle containing resources for TextAn. */
    static final String RESOURCE_BUNDLE = "cz.cuni.mff.ufal.textan.gui.TextAn";

    /** Resource containing application icon. */
    static final String ICON_RES = "/cz/cuni/mff/ufal/textan/gui/logo.jpg";

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /** Settings of the application. */
    Properties settings;

    /** File containing properties. Used to save properties. */
    String propertyFile;

    /** Application controller. */
    TextAnController controller;

    /** Localizator container for the main window. */
    ResourceBundle resourceBundle = null;

    @Override
    public void start(final Stage stage) throws Exception {
        reloadResourceBundle();
        System.out.printf(localize("starting"));
        //load default properties
        settings = new Properties(loadDefaultJarProperties());
        resetDefaultLocale(settings.getProperty("locale.language"));

        //parse arguments from command line, ie. load property file
        final List<String> args = getParameters().getRaw();
        if (helpNeeded(args)) {
            System.out.printf(localize("usage"), DEFAULT_PROPERTIES);
            System.exit(0);
        }
        if (args.size() == 1) {
            propertyFile = args.get(0);
            System.out.printf(localize("properties.load"), propertyFile);
            try (final InputStream is =
                    propertyFile.equals("-") ? new UnclosableStream(System.in)
                        : new FileInputStream(propertyFile)) {
            	 settings.load(is);
            } catch(FileNotFoundException e) {
                System.out.printf(localize("properties.new"), propertyFile);
            } catch(Exception e) {
                System.err.printf(localize("properties.error"), propertyFile);
                e.printStackTrace();
            }
        } else /*if (args.length == 0)*/ { //no property file provided
            propertyFile = DEFAULT_PROPERTIES;
            System.out.printf(localize("properties.default"), propertyFile);
            try (final InputStream is = new FileInputStream(DEFAULT_PROPERTIES)) {
                settings.load(is);
            } catch(FileNotFoundException e) {
                System.out.printf(localize("properties.default.new"), propertyFile);
            } catch(Exception e) {
                System.err.printf(localize("properties.default.error"), propertyFile);
                e.printStackTrace();
            }
        }

        resetDefaultLocale(settings.getProperty("locale.language"));

        //ask for login if needed
        if (settings.getProperty("username", "").isEmpty()) {
            final String login = Dialogs.create()
                    .title(TextAnController.TITLE)
                    .masthead(localize("username.prompt"))
                    .message(localize("username.login.label"))
                    .showTextInput(System.getProperty("user.name", ""));
            if (login == null) {
                Dialogs.create()
                        .title(TextAnController.TITLE)
                        .masthead(localize("username.error.title"))
                        .message(localize("username.error.text"))
                        .showError();
                Platform.exit();
            } else {
                settings.setProperty("username", login);
            }
        }

        //create javafx controls
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("TextAn.fxml"), resourceBundle);
        final Parent root = (Parent) loader.load();
        controller = loader.getController();
        controller.setSettings(settings);
        final Scene scene = new Scene(root);
        Utils.prepareStage(stage, "application", settings);
        //
        scene.getStylesheets().addAll(TextAn.class.getResource("/org/controlsfx/dialog/dialogs.css").toExternalForm()); //without this the first dialog in too small window could mess up its content
        scene.getStylesheets().addAll(TextAn.class.getResource("CustomMenuItem.css").toExternalForm()); //styles for context menus must be set on scene
        stage.setScene(scene);
        stage.titleProperty().bind(controller.titleProperty());
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON_RES)));
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.show();
        stage.toFront();
    }

    /**
     * Checks whether arguments suggest printing usage.
     * @param args parameters from command line
     * @return true if help needed, false otherwise
     */
    private boolean helpNeeded(final List<String> args) {
        if (args.size() > 1) {
            return true;
        }
        if (args.size() == 1) {
            final String param = args.get(0);
            return param.equals("--help") || param.equals("-h") || param.equals("/?") || param.equals("/H");
        }
        return false;
    }

    /**
     * Loads and returns default properties from {@link #DEFAULT_JAR_PROPERTIES}.
     * @return default properties
     */
    private Properties loadDefaultJarProperties() {
        System.out.printf(localize("defaults.load"), DEFAULT_JAR_PROPERTIES);
        final Properties res = new Properties();
        try {
            res.load(getClass().getClassLoader().getResourceAsStream(DEFAULT_JAR_PROPERTIES));
        } catch(Exception e) {
            //someone messed with our properties!
            System.err.printf(localize("defaults.error"), DEFAULT_JAR_PROPERTIES);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Uses {@link #resourceBundle} to localize the key.
     * @param key localization key
     * @return localization or key if not present
     */
    private String localize(final String key) {
        return Utils.localize(resourceBundle, key);
    }

    /**
     * Reloads {@link #resourceBundle} with respect to default locale.
     * If it fails and no previous resouce bundle is available,
     * {@link EmptyResourceBundle} is provided.
     */
    private void reloadResourceBundle() {
        try {
            resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
        } catch (MissingResourceException e) {
            System.err.printf("Localization resources \"%1$s\" not found!\n", RESOURCE_BUNDLE);
            e.printStackTrace();
            if (resourceBundle == null) {
                resourceBundle = new EmptyResourceBundle();
            }
        }
    }

    /**
     * Resets default locale if langCode is not null.
     * @param langCode new default locale language code
     */
    private void resetDefaultLocale(final String langCode) {
        if (langCode != null) {
            Locale.setDefault(new Locale(langCode));
            reloadResourceBundle();
        }
    }

    @Override
    public void stop() {
        System.out.printf(localize("closing"));
        controller.stop();
        if (!propertyFile.equals("-")) {
            try (final OutputStream os = new FileOutputStream(propertyFile)) {
                settings.store(os, null);
            } catch(IOException e) {
                System.err.printf(localize("closing.error"), propertyFile);
                e.printStackTrace();
            }
        }
    }
}
