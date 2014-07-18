package cz.cuni.mff.ufal.textan.gui;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.graph.DocumentGrapher;
import cz.cuni.mff.ufal.textan.core.graph.IGrapher;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.document.DocumentStage;
import cz.cuni.mff.ufal.textan.gui.document.DocumentWindow;
import cz.cuni.mff.ufal.textan.gui.document.DocumentsStage;
import cz.cuni.mff.ufal.textan.gui.document.DocumentsWindow;
import cz.cuni.mff.ufal.textan.gui.graph.GraphStage;
import cz.cuni.mff.ufal.textan.gui.graph.GraphWindow;
import cz.cuni.mff.ufal.textan.gui.join.JoinStage;
import cz.cuni.mff.ufal.textan.gui.join.JoinWindow;
import cz.cuni.mff.ufal.textan.gui.relation.RelationListStage;
import cz.cuni.mff.ufal.textan.gui.relation.RelationListWindow;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardStage;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardWindow;
import cz.cuni.mff.ufal.textan.gui.reportwizard.StateChangedListener;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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
    private Menu windowsMenu;

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
    protected ObservableList<OuterStage> children = FXCollections.observableArrayList();

    /** Core client for the application.
     * It is created when settings are provided.
     */
    protected Client client = null;

    /** Bundle containing localization. */
    protected ResourceBundle resourceBundle;

    /**
     * Flag indicating whether moving to front is in progress.
     * This is needed as moving to front removes and adds to the
     * {@link #content} which reorders items in {@link #windowsMenu}.
     */
    protected boolean movingToFront = false;

    /** Handler to move windows to front. */
    protected final EventHandler<ActionEvent> toFrontHandler = (ActionEvent t) -> {
        if (!(t.getSource() instanceof MenuItem)) {
            return;
        }
        final java.lang.Object window = ((MenuItem) t.getSource()).getUserData();
        if (window instanceof InnerWindow) {
            final InnerWindow w = (InnerWindow) window;
            //Utils.runFXlater(() -> w.toFront());
            movingToFront = true;
            w.toFront();
            movingToFront = false;
        } else if (window instanceof OuterStage) {
            final OuterStage s = (OuterStage) window;
            s.toFront();
        }
    };

    @FXML
    private void clearFilters() {
        settings.setProperty(CLEAR_FILTERS, menuItemClearFilters.isSelected() ? "true" : "false");
    }

    @FXML
    private void close() {
        Platform.exit();
    }

    @FXML
    private void displayDocuments() {
        displayDocuments((Object) null);
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
    private void join() {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final JoinWindow joinWindow = new JoinWindow(this, settings);
            content.getChildren().add(joinWindow);
        } else {
            final JoinStage joinStage = new JoinStage(this, settings);
            children.add(joinStage);
            joinStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(joinStage);
                }
            });
            joinStage.show();
        }
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

    @FXML
    private void relations() {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final RelationListWindow relationListWindow = new RelationListWindow(this, settings);
            content.getChildren().add(relationListWindow);
        } else {
            final RelationListStage relationListStage = new RelationListStage(this, settings);
            children.add(relationListStage);
            relationListStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(relationListStage);
                }
            });
            relationListStage.show();
        }
    }

    @FXML
    private void resetSizePos() {
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(800);
        stage.setHeight(600);
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
        content.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            if (movingToFront) {
                return;
            }
            while (c.next()) {
                for (Node item : c.getRemoved()) {
                    windowsMenu.getItems().remove((MenuItem) item.getUserData());
                }
                for (Node item : c.getAddedSubList()) {
                    if (item instanceof InnerWindow) {
                        final MenuItem mi = new MenuItem();
                        mi.setUserData(item);
                        mi.setOnAction(toFrontHandler);
                        final InnerWindow w = (InnerWindow) item;
                        item.setUserData(mi);
                        mi.textProperty().bind(w.titleProperty());
                        windowsMenu.getItems().add(mi);
                    }
                }
            }
        });
        children.addListener((ListChangeListener.Change<? extends OuterStage> c) -> {
            while (c.next()) {
                for (OuterStage item : c.getRemoved()) {
                    windowsMenu.getItems().remove((MenuItem) item.getUserData());
                }
                for (OuterStage item : c.getAddedSubList()) {
                    final MenuItem mi = new MenuItem();
                    mi.setUserData(item);
                    mi.setOnAction(toFrontHandler);
                    item.setUserData(mi);
                    mi.textProperty().bind(item.getInnerWindow().titleProperty());
                    windowsMenu.getItems().add(mi);
                }
            }
        });
    }

    /**
     * Returns client for communicating with the server.
     * @return client for communicating with the server
     */
    public Client getClient() {
        return client;
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
                    settingsMenu.hide();
                    Dialogs.create()
                            .owner(stage)
                            .title(TextAnController.TITLE)
                            .masthead(Utils.localize(resourceBundle, "username.error.title"))
                            .message(Utils.localize(resourceBundle, "username.error.text"))
                            .lightweight()
                            .showError();
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
        stage.setMaximized(settings.getProperty("application.max", "false").equals("true"));
    }

    /**
     * Returns title property.
     * @return title property
     */
    public StringProperty titleProperty() {
        return titleProperty;
    }

    /**
     * Displays given document.
     * @param document document to display
     */
    public void displayDocument(final Document document) {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final DocumentWindow docWindow = new DocumentWindow(this, settings, client, document);
            content.getChildren().add(docWindow);
        } else {
            final DocumentStage docStage = new DocumentStage(this, settings, client, document);
            children.add(docStage);
            docStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(docStage);
                }
            });
            docStage.show();
        }
    }

    /**
     * Creates and displays graph.
     * @param object object whose documents should be displayes
     */
    public void displayDocuments(final Object object) {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final DocumentsWindow docWindow =
                    new DocumentsWindow(this, settings, client, object, null);
            content.getChildren().add(docWindow);
        } else {
            final DocumentsStage docStage =
                    new DocumentsStage(this, settings, client, object, null);
            children.add(docStage);
            docStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(docStage);
                }
            });
            docStage.show();
        }
    }

    /**
     * Creates and displays graph.
     * @param relation relation whose documents should be displayes
     */
    public void displayDocuments(final Relation relation) {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final DocumentsWindow docWindow =
                    new DocumentsWindow(this, settings, client, null, relation);
            content.getChildren().add(docWindow);
        } else {
            final DocumentsStage docStage =
                    new DocumentsStage(this, settings, client, null, relation);
            children.add(docStage);
            docStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(docStage);
                }
            });
            docStage.show();
        }
    }

    /**
     * Displays graph from given grapher.
     * @param grapher grapher with graph
     */
    private void displayGraph(final IGrapher grapher) {
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
     * Creates and displays graph with default distance.
     * @param centerId root object id
     */
    public void displayGraph(final long centerId) {
        displayGraph(centerId, defaultDistance());
    }

    /**
     * Creates and displays graph.
     * @param centerId root object id
     * @param distance graph distance
     */
    public void displayGraph(final long centerId, final int distance) {
        final IGrapher grapher = client.createObjectGrapher();
        grapher.setRootId(centerId);
        grapher.setDistance(distance);
        displayGraph(grapher);
    }

    /**
     * Creates and displays graph with default distance.
     * @param document document to display
     */
    public void displayGraph(final Document document) {
        final IGrapher grapher = new DocumentGrapher(client, document);
        displayGraph(grapher);
    }

    /**
     * Creates and displays graph with default distance.
     * @param relation relation to display
     */
    public void displayGraph(final Relation relation) {
        displayGraph(relation, defaultDistance());
    }

    /**
     * Creates and displays graph with default distance.
     * @param relation relation to display
     * @param distance graph distance
     */
    public void displayGraph(final Relation relation, final int distance) {
        final IGrapher grapher = client.createRelationGrapher();
        grapher.setRootId(relation.getId());
        grapher.setDistance(distance);
        displayGraph(grapher);
    }

    /**
     * Returns default graph distance.
     * @return default graph distance
     */
    public int defaultDistance() {
        try {
            return Integer.parseInt(settings.getProperty("graph.distance", "5"));
        } catch (NumberFormatException e) {
            return 5;
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
        //main stage's size/pos changes are not listened to because of
        //maximalization issues (x/y notified before isMaximized is set)
        //next time we will use values from this run's start ignoring later changes...
        settings.setProperty("application.max", stage.isMaximized() ? "true" : "false");
        if (!stage.isMaximized()) {
            settings.setProperty("application.width", Double.toString(stage.getWidth()));
            settings.setProperty("application.height", Double.toString(stage.getHeight()));
            settings.setProperty("application.x", Double.toString(stage.getX()));
            settings.setProperty("application.y", Double.toString(stage.getY()));
        }
        //copy needed as closing removes stages from children
        for (Stage stage : new ArrayList<>(children)) {
            stage.close();
        }
    }
}
