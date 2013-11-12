package cz.cuni.mff.ufal.textan.reportwizard;

import java.io.IOException;
import java.util.Properties;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import jfxtras.labs.scene.control.window.Window;
import org.controlsfx.dialog.Dialogs;

/**
 * Wizard for handling reports.
 */
public class ReportWizard extends Window {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Report Wizard";

    /**
     * Settings of the application.
     * Handle with care, they're shared!
     */
    protected Properties settings;

    /**
     * Only constructor.
     * @param settings properties with settings
     */
    public ReportWizard(final Properties settings) {
        super(TITLE);
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> this.toFront());
        layoutXProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                if (newVal.doubleValue() < 0) {
                    layoutXProperty().set(0);
                } else {
                    final Parent p = getParent();
                    if (p != null ) {
                        final Bounds b = p.getLayoutBounds();
                        if (b.getWidth() < newVal.doubleValue() + getWidth()) {
                            layoutXProperty().set(b.getWidth() - getWidth());
                        }
                    }
                }
            }
        );
        layoutYProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                if (newVal.doubleValue() < 0) {
                    layoutYProperty().set(0);
                } else {
                    final Parent p = getParent();
                    if (p != null ) {
                        final Bounds b = p.getLayoutBounds();
                        if (b.getHeight() < newVal.doubleValue() + getHeight()) {
                            layoutYProperty().set(b.getHeight()- getHeight());
                        }
                    }
                }
            }
        );
        prefWidthProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                final Parent p = getParent();
                if (p != null ) {
                    final Bounds b = p.getLayoutBounds();
                    if (b.getWidth() < getLayoutX() + newVal.doubleValue()) {
                        setPrefWidth(b.getWidth() - getLayoutX());
                    }
                }
            }
        );
        prefHeightProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                final Parent p = getParent();
                if (p != null ) {
                    final Bounds b = p.getLayoutBounds();
                    if (b.getHeight() < getLayoutY() + newVal.doubleValue()) {
                        setPrefHeight(b.getHeight() - getLayoutY());
                    }
                }
            }
        );
        this.settings = settings;
        //TODO init from settings
        setPrefSize(300, 200);
        //
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("01_ReportLoad.fxml"));
            final Parent root = (Parent) loader.load();
            final ReportLoadController controller = loader.getController();
            controller.setSettings(settings);
            controller.setWindow(this);
            getContentPane().getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            Dialogs.create()
                    .title("Problém při načítání wizardu!")
                    .lightweight()
                    .showException(e);
        }
    }

    @Override
    public void close() {
        super.close();
        //TODO save logic here
    }
}
