package cz.cuni.mff.ufal.textan.gui;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.graph.GraphStage;
import cz.cuni.mff.ufal.textan.gui.graph.GraphWindow;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardStage;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardWindow;
import cz.cuni.mff.ufal.textan.gui.reportwizard.StateChangedListener;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.xml.ws.WebServiceException;
import jfxtras.labs.scene.control.BigDecimalField;
import jfxtras.labs.scene.control.window.Window;
import org.controlsfx.dialog.Dialogs;

/**
 * Controller for the TextAn application.
 */
public class TextAnController implements Initializable {

    /** Name of property controlling independence of windows. */
    static public final String INDEPENDENT_WINDOW = "windows.independent";

    /** Name of property controlling displaying hypergraphs. */
    static public final String HYPER_GRAPHS = "hypergraphs";

    /** Name of property controlling clearing filters. */
    static public final String CLEAR_FILTERS = "clear.filters";

    /** Original title. */
    static protected final String TITLE = "TextAn";

    @FXML
    private BorderPane appRoot;

    @FXML
    private Pane content;

    @FXML
    private CheckMenuItem menuItemIndependentWindows;

    @FXML
    private CheckMenuItem menuItemHypergraphs;

    @FXML
    private CheckMenuItem menuItemClearFilters;

    @FXML
    protected TextField loginTextField;

    @FXML
    private ComboBox<String> localizationCombo;

    @FXML
    private Menu settingsMenu;

    @FXML
    private BigDecimalField distanceField;

    /** Properties with application settings. */
    protected Properties settings = null;

    /** Stage controlled by this controller. */
    protected Stage stage;

    /** Property binded to stage titleProperty. */
    StringProperty titleProperty = new SimpleStringProperty(TITLE);

    /** List of children stages. */
    protected List<Stage> children = new ArrayList<>();

    /** Core client for the application.
     * It is created when settings are provided.
     */
    protected Client client = null;

    /** Bundle containing localization. */
    protected ResourceBundle resourceBundle;

    @FXML
    private void clearFilters() {
        settings.setProperty(CLEAR_FILTERS, menuItemClearFilters.isSelected() ? "true" : "false");
    }

    @FXML
    private void close() {
        Platform.exit();
    }

    @FXML
    private void graph() {
        displayGraph(-1, -1);
    }

    @FXML
    private void hypergraphs() {
        settings.setProperty(HYPER_GRAPHS, menuItemHypergraphs.isSelected() ? "true" : "false");
    }

    @FXML
    private void independentWindows() {
        settings.setProperty(INDEPENDENT_WINDOW, menuItemIndependentWindows.isSelected() ? "true" : "false");
    }

    @FXML
    private void reportWizard() {
        try {
            final ProcessReportPipeline pipeline = client.createNewReportPipeline();
            StateChangedListener listener;
            if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
                final ReportWizardWindow wizard = new ReportWizardWindow(settings);
                content.getChildren().add(wizard);
                listener = new StateChangedListener(this, settings, pipeline, wizard);
            } else {
                final ReportWizardStage stage = new ReportWizardStage(settings);
                children.add(stage);
                stage.showingProperty().addListener((ov, oldVal, newVal) -> {
                    if (!newVal) {
                        children.remove(stage);
                    }
                });
                listener = new StateChangedListener(this, settings, pipeline, stage);
                stage.show();
            }
            pipeline.addStateChangedListener(listener);
        } catch (WebServiceException e) {
            e.printStackTrace();
            Dialogs.create()
                    .owner(stage)
                    .lightweight()
                    .title(Utils.localize(resourceBundle, "webservice.error"))
                    .showException(e);
        }
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        content.addEventFilter(MouseEvent.ANY, (MouseEvent t) -> {
            if (t.getX()< 0 || t.getY() < 0
                    || t.getX() > content.getWidth()
                    || t.getY() > content.getHeight()) {
                t.consume();
            }
        });
        resourceBundle = rb;
        distanceField.numberProperty().addListener((ov, oldVal, newVal) -> {
            settings.setProperty("graph.distance", newVal.toString());
        });
    }

    /**
     * Sets TextAn settings.
     * @param settings new settings
     */
    public void setSettings(final Properties settings) {
        this.settings = settings;
        menuItemIndependentWindows.setSelected(
                settings.getProperty(INDEPENDENT_WINDOW, "false").equals("true"));
        menuItemHypergraphs.setSelected(
                settings.getProperty(HYPER_GRAPHS, "false").equals("true"));
        menuItemClearFilters.setSelected(
                settings.getProperty(CLEAR_FILTERS, "false").equals("true"));
        loginTextField.setText(settings.getProperty("username", System.getProperty("user.name")));
        loginTextField.focusedProperty().addListener((ov, oldVal, newVal) -> {
            if (oldVal) {
                final String login = loginTextField.getText();
                if (login == null || login.isEmpty() || login.trim().isEmpty()) {
                    loginTextField.setText(settings.getProperty("username"));
                } else {
                    settings.setProperty("username", login);
                }
            }
        });
        localizationCombo.getSelectionModel().select(settings.getProperty("locale.language", "cs"));
        localizationCombo.valueProperty().addListener(
            (ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
                Platform.runLater(
                        () -> {
                            settingsMenu.hide();
                            Dialogs.create()
                                .owner(stage)
                                .lightweight()
                                .message(Utils.localize(resourceBundle,"locale.changed"))
                                .showWarning();
                            });
                settings.setProperty("locale.language", newVal);
        });
        distanceField.setNumber(new BigDecimal(settings.getProperty("graph.distance", "5")));
        client = new Client(settings);
    }

    /**
     * Sets stage controlled by this controller.
     * @param stage controlled stage
     */
    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Returns title property.
     * @return title property
     */
    public StringProperty titleProperty() {
        return titleProperty;
    }

    /**
     * Creates and displays graph with default distance.
     * @param centerId root object id
     */
    public void displayGraph(final long centerId) {
        int distance;
        try {
            distance = Integer.parseInt(settings.getProperty("graph.distance", "5"));
        } catch (NumberFormatException e) {
            distance = 5;
        }
        displayGraph(centerId, distance);
    }

    /**
     * Creates and displays graph.
     * @param centerId root object id
     * @param distance graph distance
     */
    public void displayGraph(final long centerId, final int distance) {
        final Grapher grapher = client.createGrapher();
        grapher.setRootId(centerId);
        grapher.setDistance(distance);
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final GraphWindow graphWindow = new GraphWindow(this, settings, grapher);
            content.getChildren().add(graphWindow);
        } else {
            final GraphStage stage = new GraphStage(this, settings, grapher);
            children.add(stage);
            stage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(stage);
                }
            });
            stage.show();
        }
    }

    /**
     * Called when application is stopped to store settings etc.
     */
    public void stop() {
        content.getChildren().stream()
                .filter(n -> n instanceof Window)
                .map(n -> (Window) n)
                .forEach(w -> w.close());
        //copy needed as closing removes stages from children
        for (Stage stage : new ArrayList<>(children)) {
            stage.close();
        }
    }
}
