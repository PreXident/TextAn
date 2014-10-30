package cz.cuni.mff.ufal.textan.gui.settings;

import cz.cuni.mff.ufal.textan.gui.ExposingVBox;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import static cz.cuni.mff.ufal.textan.gui.TextAnController.CLEAR_FILTERS;
import static cz.cuni.mff.ufal.textan.gui.TextAnController.HYPER_GRAPHS;
import static cz.cuni.mff.ufal.textan.gui.TextAnController.INDEPENDENT_WINDOW;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.BigDecimalField;
import org.controlsfx.dialog.Dialogs;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class SettingsController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Settings";

    /** Identifier used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "settings.view";

    /** Pref height of the join window. */
    static protected final int PREF_HEIGHT = 225;

    /** Pref width of the join window. */
    static protected final int PREF_WIDTH = 265;

    @FXML
    private ExposingVBox root;

    @FXML
    private CheckBox independentWindowsCheckBox;

    @FXML
    private CheckBox hypergraphsCheckBox;

    @FXML
    private CheckBox clearFiltersCheckBox;

    @FXML
    protected TextField loginTextField;

    @FXML
    private ComboBox<String> localizationCombo;

    @FXML
    private BigDecimalField distanceField;

    /** Localization controller. */
    private ResourceBundle resourceBundle;

    /** Application controller. */
    private TextAnController textAnController;

    @FXML
    private void clearFilters() {
        settings.setProperty(CLEAR_FILTERS, clearFiltersCheckBox.isSelected() ? "true" : "false");
    }

    @FXML
    private void hypergraphs() {
        settings.setProperty(HYPER_GRAPHS, hypergraphsCheckBox.isSelected() ? "true" : "false");
    }

    @FXML
    private void independentWindows() {
        settings.setProperty(INDEPENDENT_WINDOW, independentWindowsCheckBox.isSelected() ? "true" : "false");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
        distanceField.numberProperty().addListener((ov, oldVal, newVal) -> {
            settings.setProperty("graph.distance", newVal.toString());
        });
    }

    @Override
    public void setSettings(final Properties settings) {
        super.setSettings(settings);
        independentWindowsCheckBox.setSelected(
                settings.getProperty(INDEPENDENT_WINDOW, "false").equals("true"));
        hypergraphsCheckBox.setSelected(
                settings.getProperty(HYPER_GRAPHS, "false").equals("true"));
        clearFiltersCheckBox.setSelected(
                settings.getProperty(CLEAR_FILTERS, "false").equals("true"));
        loginTextField.setText(settings.getProperty("username", System.getProperty("user.name")));
        loginTextField.focusedProperty().addListener((ov, oldVal, newVal) -> {
            if (oldVal) {
                final String login = loginTextField.getText();
                if (login == null || login.isEmpty() || login.trim().isEmpty()) {
                    loginTextField.setText(settings.getProperty("username"));
                    Platform.runLater(() -> {
                        final Dialogs dialog = prepareDialog(Dialogs.create()
                                .owner(getDialogOwner(root))
                                .title(Utils.localize(resourceBundle, PROPERTY_ID))
                                .masthead(Utils.localize(resourceBundle, "username.error.title"))
                                .message(Utils.localize(resourceBundle, "username.error.text")));
                        if (dialog != null) {
                            dialog.showError();
                        }
                    });
                } else if (!login.equals(settings.getProperty("username"))) {
                    settings.setProperty("username", login);
                    Platform.runLater(() -> {
                        final Dialogs dialog = prepareDialog(Dialogs.create()
                                .message(Utils.localize(resourceBundle,"restart.change")));
                        if (dialog != null) {
                            dialog.showWarning();
                        }
                    });
                }
            }
        });
        localizationCombo.getSelectionModel().select(settings.getProperty("locale.language", "cs"));
        localizationCombo.valueProperty().addListener(
            (ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
                Platform.runLater(
                        () -> {
                            Dialogs.create()
                                .owner(getDialogOwner(root))
                                .lightweight()
                                .message(Utils.localize(resourceBundle,"restart.change"))
                                .showWarning();
                            });
                settings.setProperty("locale.language", newVal);
        });
        distanceField.setNumber(new BigDecimal(settings.getProperty("graph.distance", "5")));
    }

    /**
     * Sets application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
    }

    @Override
    public void setStage(final OuterStage stage) {
        super.setStage(stage);
        Utils.runFXlater(() -> {
            stage.getInnerWindow().setPrefWidth(root.computePrefWidth(0));
            stage.getInnerWindow().setPrefHeight(root.computePrefHeight(0) + 40); //guessed titlebar height
        });
    }

    @Override
    public void setWindow(final InnerWindow window) {
        super.setWindow(window);
        Utils.runFXlater(() -> {
            window.setPrefWidth(root.computePrefWidth(0));
            window.setPrefHeight(root.computePrefHeight(0) + 60); //guessed titlebar height
        });
    }

    protected Dialogs prepareDialog(final Dialogs dialog) {
        final Stage mainStage = textAnController.getStage();
        if (!mainStage.isShowing()) {
            return null;
        }
        if (window != null) {
            final boolean alreadyClosed = window.getParent() == null;
            dialog.owner(alreadyClosed ? mainStage : root).lightweight();
        } else {
            final boolean stillOpen = stage.isShowing();
            if (stillOpen) {
                dialog.owner(stage);
            }
        }
        return dialog;
    }
}
