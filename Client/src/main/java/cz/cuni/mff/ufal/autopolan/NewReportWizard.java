package cz.cuni.mff.ufal.autopolan;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Wizard for new reports.
 */
public class NewReportWizard {
    public NewReportWizard() throws IOException {
        final Stage stage = new Stage();
        //create javafx controls
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("NewReport.fxml"));
        final Parent root = (Parent) loader.load();
        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }
}
