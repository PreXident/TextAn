package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.gui.Utils;
import java.util.Properties;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.labs.scene.control.window.Window;

/**
 * Class for displaying Report Wizard in independent window.
 */
public class ReportWizardStage extends Stage {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Report Wizard";

    /**
     * Settings of the application.
     * Handle with care, they're shared!
     */
    protected Properties settings;

    final private Window reportWizardWindow;

    private double cursorX;

    private double cursorY;

    private boolean draggingNW = false;

    /**
     * Only constructor.
     * @param settings properties with settings
     */
    public ReportWizardStage(final Properties settings) {
        super(StageStyle.TRANSPARENT);
        setTitle(TITLE);
        this.settings = settings;
        setOnCloseRequest(e -> close());
        //init from settings
        Utils.prepareStage(this, "report.wizard", settings);
        //add listeners
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> this.toFront());
        //
        final Pane pane = new Pane();
        pane.setBackground(Background.EMPTY);
        setScene(new Scene(pane));
        getScene().setFill(null);
        reportWizardWindow = new Window(ReportWizardWindow.TITLE);
        reportWizardWindow.setPrefWidth(Double.parseDouble(settings.getProperty("report.wizard.width", "300")));
        reportWizardWindow.setPrefHeight(Double.parseDouble(settings.getProperty("report.wizard.height", "200")));
        reportWizardWindow.setLayoutX(Double.parseDouble(settings.getProperty("report.wizard.x", "0")));
        reportWizardWindow.setLayoutY(Double.parseDouble(settings.getProperty("report.wizard.y", "0")));
        reportWizardWindow.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.isPrimaryButtonDown()
                        && reportWizardWindow.getCursor() != Cursor.E_RESIZE
                        && reportWizardWindow.getCursor() != Cursor.W_RESIZE
                        && reportWizardWindow.getCursor() != Cursor.N_RESIZE
                        && reportWizardWindow.getCursor() != Cursor.S_RESIZE
                        && reportWizardWindow.getCursor() != Cursor.NE_RESIZE
                        && reportWizardWindow.getCursor() != Cursor.SE_RESIZE
                        && reportWizardWindow.getCursor() != Cursor.NW_RESIZE
                        && reportWizardWindow.getCursor() != Cursor.SW_RESIZE) {
                    cursorX = getX() - t.getScreenX();
                    cursorY = getY() - t.getScreenY();
                }
            }
        });
        reportWizardWindow.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent t) -> {
            draggingNW = false;
            if (t.isPrimaryButtonDown()) {
                final Cursor cursor = reportWizardWindow.getCursor();
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
        reportWizardWindow.setMovable(false);
        System.out.println(reportWizardWindow.getTitleBarStyleClass());
        reportWizardWindow.setTitleBarStyleClass("my-window-titlebar");
        getScene().getStylesheets().add(ReportWizardStage.class.getResource("/cz/cuni/mff/ufal/textan/gui/reportwizard/window.css").toExternalForm());
        reportWizardWindow.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                double delta = getWidth() - t1.doubleValue();
                setWidth(t1.doubleValue());
                if (draggingNW) {
                    setX(getX()+delta);
                }
                reportWizardWindow.setLayoutX(0);
            }
        });
        reportWizardWindow.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                double delta = getHeight()- t1.doubleValue();
                setHeight(t1.doubleValue());
                if (draggingNW) {
                    setY(getY()+delta);
                }
                reportWizardWindow.setLayoutY(0);
            }
        });
        pane.getChildren().add(reportWizardWindow);
    }

    public Window getReportWizardWindow() {
        return reportWizardWindow;
    }

    @Override
    public void close() {
        super.close();
        //TODO save logic here
    }
}
