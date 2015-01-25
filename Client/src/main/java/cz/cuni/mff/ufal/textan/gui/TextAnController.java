package cz.cuni.mff.ufal.textan.gui;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.graph.DocumentGrapher;
import cz.cuni.mff.ufal.textan.core.graph.IGrapher;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentAlreadyProcessedException;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentChangedException;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.about.AboutStage;
import cz.cuni.mff.ufal.textan.gui.about.AboutWindow;
import cz.cuni.mff.ufal.textan.gui.document.DocumentStage;
import cz.cuni.mff.ufal.textan.gui.document.DocumentWindow;
import cz.cuni.mff.ufal.textan.gui.document.DocumentsStage;
import cz.cuni.mff.ufal.textan.gui.document.DocumentsWindow;
import cz.cuni.mff.ufal.textan.gui.document.EditDocumentStage;
import cz.cuni.mff.ufal.textan.gui.document.EditDocumentWindow;
import cz.cuni.mff.ufal.textan.gui.graph.GraphStage;
import cz.cuni.mff.ufal.textan.gui.graph.GraphWindow;
import cz.cuni.mff.ufal.textan.gui.join.JoinStage;
import cz.cuni.mff.ufal.textan.gui.join.JoinWindow;
import cz.cuni.mff.ufal.textan.gui.path.PathStage;
import cz.cuni.mff.ufal.textan.gui.path.PathWindow;
import cz.cuni.mff.ufal.textan.gui.relation.RelationListStage;
import cz.cuni.mff.ufal.textan.gui.relation.RelationListWindow;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportLoadController;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardStage;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardWindow;
import cz.cuni.mff.ufal.textan.gui.reportwizard.StateChangedListener;
import cz.cuni.mff.ufal.textan.gui.reportwizard.TextFlow;
import cz.cuni.mff.ufal.textan.gui.settings.ColorsStage;
import cz.cuni.mff.ufal.textan.gui.settings.ColorsWindow;
import cz.cuni.mff.ufal.textan.gui.settings.SettingsStage;
import cz.cuni.mff.ufal.textan.gui.settings.SettingsWindow;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.xml.ws.WebServiceException;
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
    private Menu windowsMenu;

    @FXML
    private Menu aboutMenu;

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
            w.requestFocus();
            movingToFront = false;
        } else if (window instanceof OuterStage) {
            final OuterStage s = (OuterStage) window;
            s.toFront();
            s.requestFocus();
        }
    };

    @FXML
    private void about() {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final AboutWindow aboutWindow = new AboutWindow(this, settings);
            content.getChildren().add(aboutWindow);
            aboutWindow.requestFocus();
        } else {
            final AboutStage aboutStage = new AboutStage(this, settings);
            children.add(aboutStage);
            aboutStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(aboutStage);
                }
            });
            aboutStage.show();
        }
    }

    @FXML
    private void colors() {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final ColorsWindow colorsWindow = new ColorsWindow(this, settings);
            content.getChildren().add(colorsWindow);
            colorsWindow.requestFocus();
        } else {
            final ColorsStage colorsStage = new ColorsStage(this, settings);
            children.add(colorsStage);
            colorsStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(colorsStage);
                }
            });
            colorsStage.show();
        }
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
    private void generalSettings() {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final SettingsWindow settingsWindow = new SettingsWindow(this, settings);
            content.getChildren().add(settingsWindow);
            settingsWindow.requestFocus();
        } else {
            final SettingsStage settingsStage = new SettingsStage(this, settings);
            children.add(settingsStage);
            settingsStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(settingsStage);
                }
            });
            settingsStage.show();
        }
    }

    @FXML
    private void graph() {
        displayGraph(-1, -1);
    }

    @FXML
    private void importReport() {
        try {
            final ProcessReportPipeline pipeline = client.createNewReportPipeline();
            pipeline.selectFileDatasource();
            displayPipeline(pipeline);
        } catch (WebServiceException e) {
            handleWebserviceException(e);
        }
    }

    @FXML
    private void join() {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final JoinWindow joinWindow = new JoinWindow(this, settings);
            content.getChildren().add(joinWindow);
            joinWindow.requestFocus();
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
    private void load() {
        try {
            final ProcessReportPipeline pipeline = client.createNewReportPipeline();
            final ResourceBundle rb = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.reportwizard.01_ReportLoad");
            final boolean loaded = ReportLoadController.loadReport(rb, settings, stage, pipeline);
            if (loaded) {
                displayPipeline(pipeline);
            }
        } catch (WebServiceException e) {
            handleWebserviceException(e);
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.create()
                    .owner(stage)
                    .lightweight()
                    .title(Utils.localize(resourceBundle, "error"))
                    .showException(e);
        }
    }

    @FXML
    private void newReport() {
        try {
            final ProcessReportPipeline pipeline = client.createNewReportPipeline();
            pipeline.selectEmptyDatasource();
            displayPipeline(pipeline);
        } catch (WebServiceException e) {
            handleWebserviceException(e);
        }
    }

    @FXML
    private void path() {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final PathWindow pathWindow = new PathWindow(this, settings);
            content.getChildren().add(pathWindow);
            pathWindow.requestFocus();
        } else {
            final PathStage pathStage = new PathStage(this, settings);
            children.add(pathStage);
            pathStage.showingProperty().addListener((ov, oldVal, newVal) -> {
                if (!newVal) {
                    children.remove(pathStage);
                }
            });
            pathStage.show();
        }
    }

    @FXML
    private void reportWizard() {
        try {
            final ProcessReportPipeline pipeline = client.createNewReportPipeline();
            displayPipeline(pipeline);
        } catch (WebServiceException e) {
            handleWebserviceException(e);
        }
    }

    @FXML
    private void relations() {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final RelationListWindow relationListWindow = new RelationListWindow(this, settings);
            content.getChildren().add(relationListWindow);
            relationListWindow.requestFocus();
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
        client = new Client(settings);
    }

    /**
     * Returns stage controlled by this controller.
     * @return stage controlled by this controller
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Sets stage controlled by this controller.
     * @param stage controlled stage
     */
    public void setStage(final Stage stage) {
        this.stage = stage;
        stage.setMaximized(settings.getProperty("application.max", "false").equals("true"));
        stage.maximizedProperty().addListener((ov, oldVal, newVal) -> {
            Utils.runFXlater(() -> {
                final Deque<Parent> stack = new ArrayDeque<>();
                for (Node node : content.getChildren()) {
                    if (node instanceof InnerWindow) {
                        stack.push(((InnerWindow) node).getContentPane());
                        while (!stack.isEmpty()) {
                            final Parent parent = stack.pop();
                            for (Node n : parent.getChildrenUnmodifiable()) {
                                if (n instanceof TextFlow) {
                                    ((TextFlow) n).layoutChildren();
                                } else if (n instanceof Parent) {
                                    stack.add((Parent) n);
                                }
                            }
                        }
                    }
                }
            });
        });
    }

    /**
     * Returns title property.
     * @return title property
     */
    public StringProperty titleProperty() {
        return titleProperty;
    }

    public void setUsername(final String username) {
        client.setUsername(username);
        settings.setProperty("username", username);
    }

    /**
     * Displays given document.
     * @param document document to display
     */
    public void displayDocument(final Document document) {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final DocumentWindow docWindow = new DocumentWindow(this, settings, client, document);
            content.getChildren().add(docWindow);
            docWindow.requestFocus();
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
            docWindow.requestFocus();
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
            docWindow.requestFocus();
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
    public void displayGraph(final IGrapher grapher) {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final GraphWindow graphWindow = new GraphWindow(this, settings, grapher);
            content.getChildren().add(graphWindow);
            graphWindow.requestFocus();
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
     *
     * @param pipeline
     */
    private void displayPipeline(final ProcessReportPipeline pipeline) {
        StateChangedListener listener;
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final ReportWizardWindow wizard = new ReportWizardWindow(settings);
            content.getChildren().add(wizard);
            wizard.requestFocus();
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
     * Opens dialog for document editing.
     * @param document document to edit, can be null
     */
    public void editDocument(final Document document) {
        if (settings.getProperty(INDEPENDENT_WINDOW, "false").equals("false")) {
            final EditDocumentWindow window = new EditDocumentWindow(this, settings, document);
            content.getChildren().add(window);
            window.requestFocus();
        } else {
            final EditDocumentStage stage = new EditDocumentStage(this, settings, document);
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
     * Opens dialog for adding new document.
     */
    public void newDocument() {
        editDocument(null);
    }

    /**
     * Starts processing of given document.
     * @param document document to process
     */
    public void processDocument(final Document document) {
        try {
            final ProcessReportPipeline pipeline = client.createNewReportPipeline();
            pipeline.selectDatabaseDatasource();
            try {
                pipeline.setReport(document);
            } catch (DocumentChangedException | DocumentAlreadyProcessedException e) { //this should be very rare
                e.printStackTrace(); //let the reportwizard package handle errors
            }
            displayPipeline(pipeline);
        } catch (WebServiceException e) {
            handleWebserviceException(e);
        }
    }

    /**
     * Prints the stack trace and shows error dialog.
     * @param exception exception to handle
     */
    protected void handleWebserviceException(final WebServiceException exception) {
        exception.printStackTrace();
        Dialogs.create()
                .owner(stage)
                .lightweight()
                .title(Utils.localize(resourceBundle, "webservice.error"))
                .showException(exception);
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
