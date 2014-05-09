package cz.cuni.mff.ufal.textan.gui;

import static cz.cuni.mff.ufal.textan.gui.InnerWindow.MIN_HEIGHT;
import static cz.cuni.mff.ufal.textan.gui.InnerWindow.MIN_WIDTH;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizardStage;
import java.util.Properties;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.labs.scene.control.window.Window;

/**
 * Class for displaying Window in independent OS window.
 */
public class OuterStage extends Stage {

    /**
     * Settings of the application.
     * Handle with care, they're shared!
     */
    protected Properties settings;

    /** Identifier used to store properties in {@link #settings}. */
    final protected String propertyID;

    /** Inner window displaying content. */
    final private Window innerWindow;

    /** Cursor X position while dragging. */
    private double cursorX;

    /** Cursor Y position while dragging. */
    private double cursorY;

    /** Flag indicating whether dragging is NW. */
    private boolean draggingNW = false;

    /**
     * Only constructor.
     * @param title Stage's title
     * @param propertyID identifier used to store properties
     * @param settings properties with settings
     */
    public OuterStage(final String title, final String propertyID, final Properties settings) {
        super(StageStyle.TRANSPARENT);
        setTitle(title);
        this.propertyID = propertyID;
        this.settings = settings;
        setOnCloseRequest(e -> close());
        //init from settings
        Utils.prepareStage(this, propertyID, settings);
        //add listeners
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> this.toFront());
        //
        final Pane pane = new Pane();
        pane.setBackground(Background.EMPTY);
        setScene(new Scene(pane));
        getScene().setFill(null);
        innerWindow = new Window(title);
        innerWindow.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        innerWindow.setPrefWidth(Double.parseDouble(settings.getProperty(propertyID + ".width", String.valueOf(MIN_WIDTH))));
        innerWindow.setPrefHeight(Double.parseDouble(settings.getProperty(propertyID + ".height", String.valueOf(MIN_HEIGHT))));
        innerWindow.setLayoutX(Double.parseDouble(settings.getProperty(propertyID + ".x", "0")));
        innerWindow.setLayoutY(Double.parseDouble(settings.getProperty(propertyID + ".y", "0")));
        innerWindow.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent t) -> {
            if (t.isPrimaryButtonDown()
                    && innerWindow.getCursor() != Cursor.E_RESIZE
                    && innerWindow.getCursor() != Cursor.W_RESIZE
                    && innerWindow.getCursor() != Cursor.N_RESIZE
                    && innerWindow.getCursor() != Cursor.S_RESIZE
                    && innerWindow.getCursor() != Cursor.NE_RESIZE
                    && innerWindow.getCursor() != Cursor.SE_RESIZE
                    && innerWindow.getCursor() != Cursor.NW_RESIZE
                    && innerWindow.getCursor() != Cursor.SW_RESIZE) {
                cursorX = getX() - t.getScreenX();
                cursorY = getY() - t.getScreenY();
            }
        });
        innerWindow.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent t) -> {
            draggingNW = false;
            if (t.isPrimaryButtonDown()) {
                final Cursor cursor = innerWindow.getCursor();
                if (cursor != Cursor.E_RESIZE
                        && cursor != Cursor.W_RESIZE
                        && cursor != Cursor.N_RESIZE
                        && cursor != Cursor.S_RESIZE
                        && cursor != Cursor.NE_RESIZE
                        && cursor != Cursor.SE_RESIZE
                        && cursor != Cursor.NW_RESIZE
                        && cursor != Cursor.SW_RESIZE) {
                    setX(t.getScreenX() + cursorX);
                    setY(t.getScreenY() + cursorY);
                } else {
                    draggingNW = cursor == Cursor.W_RESIZE
                            || cursor == Cursor.NW_RESIZE
                            || cursor == Cursor.N_RESIZE;
                }
            }
        });
        innerWindow.setMovable(false);
        innerWindow.setTitleBarStyleClass("my-window-titlebar");
        getScene().getStylesheets().add(ReportWizardStage.class.getResource("/cz/cuni/mff/ufal/textan/gui/window.css").toExternalForm());
        getScene().getStylesheets().add(TextAn.class.getResource("CustomMenuItem.css").toExternalForm()); //styles for context menus must be set on scene
        innerWindow.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            double delta = getWidth() - t1.doubleValue();
            setWidth(t1.doubleValue());
            if (draggingNW) {
                setX(getX()+delta);
            }
            innerWindow.setLayoutX(0);
        });
        innerWindow.heightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            double delta = getHeight()- t1.doubleValue();
            setHeight(t1.doubleValue());
            if (draggingNW) {
                setY(getY()+delta);
            }
            innerWindow.setLayoutY(0);
        });
        pane.getChildren().add(innerWindow);
    }

    /**
     * Returns {@link #innerWindow}.
     * @return innerWindow
     */
    public Window getInnerWindow() {
        return innerWindow;
    }
}
