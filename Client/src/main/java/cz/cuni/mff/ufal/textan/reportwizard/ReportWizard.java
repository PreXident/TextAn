package cz.cuni.mff.ufal.autopolan.reportwizard;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

    static final String TEST_TEXT = "Ahoj, toto je testovaci zprava urcena pro vyzkouseni vsech moznosti oznacovani textu.";

    static final Set<Character> separators = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('\n', '\t', '\r', ' ', ',', '.', ';')));

    static final String SELECTED = "selected";

    static protected void addSelectedClass(Iterable<Node> list) {
        list.forEach(node -> node.getStyleClass().add(SELECTED));
    }

    static protected void removeSelectedClass(Iterable<Node> list) {
        list.forEach(node -> node.getStyleClass().remove(SELECTED));
    }

    /**
     * Settings of the application.
     * Handle with care, they're shared!
     */
    protected Properties settings;

    int startTextIndex = -1;

    /**
     * Only constructor.
     * @param settings properties with settings
     */
    public ReportWizard(final Properties settings) {
        super(TITLE);
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> this.toFront());
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
