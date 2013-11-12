package cz.cuni.mff.ufal.textan.gui.reportwizard;

import java.io.IOException;
import java.util.Properties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 *
 */
public class ReportWizardStage extends Stage {

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
    public ReportWizardStage(final Properties settings) {
        setTitle(TITLE);
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> this.toFront());
        this.settings = settings;
        //TODO init from settings
        setWidth(300);
        setHeight(200);
        //
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("01_ReportLoad.fxml"));
            final Parent root = (Parent) loader.load();
            final ReportLoadController controller = loader.getController();
            controller.setSettings(settings);
            controller.setStage(this);
            setScene(new Scene(root));
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
