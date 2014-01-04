package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Controls editing entities.
 */
public class ReportEntitiesController extends ReportWizardController {

    /** Separators delimiting words. */
    static final Set<Character> separators = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('\n', '\t', '\r', ' ', ',', '.', ';', '!')));

    /** Style class for selected words. */
    static final String SELECTED = "selected";

    /**
     * Adds {@link #SELECTED} style class to all items in the list.
     * @param list items to which add the style class
     */
    static void addSelectedClass(Iterable<Node> list) {
        list.forEach(node -> node.getStyleClass().add(SELECTED));
    }

    /**
     * Removes {@link #SELECTED} style class from all items in the list.
     * @param list items from which remove the style class
     */
    static void removeSelectedClass(Iterable<Node> list) {
        list.forEach(node -> node.getStyleClass().remove(SELECTED));
    }

    @FXML
    BorderPane root;

    @FXML
    ScrollPane scrollPane;

    @FXML
    TextFlow textFlow;

    /**
     * Index of the first dragged {@link Text} node.
     * If no dragging is taking place, it is -1;
     */
    int startTextIndex = -1;

    @FXML
    private void cancel() {
        closeContainer();
    }

    @FXML
    private void next() {
        callWithContentBackup(() ->
            createDialog()
                    .owner(getDialogOwner(root))
                    .title("Hotovo!")
                    .message("Zpráva úspěšně vytvořena")
                    .showInformation());
        closeContainer();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFlow.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        final String report = this.pipeline.getReportText();
        final List<String> words = new ArrayList<>();
        int start = 0;
        for(int i = 0; i < report.length(); ++i) {
            if (separators.contains(report.charAt(i))) {
                if (start < i) {
                    words.add(report.substring(start, i));
                }
                words.add(report.substring(i, i + 1));
                start = i + 1;
            }
        }
        if (start < report.length()) {
            words.add(report.substring(start, report.length()));
        }

        final List<Node> texts = textFlow.getChildren();
        texts.clear();
        for (String word: words) {
            final Text text = new Text(word);
            text.setOnMousePressed(e -> {
                if (!text.getStyleClass().contains(SELECTED)) {
                    System.out.println("pressed");
                    removeSelectedClass(texts);
                    startTextIndex = texts.indexOf(text);
                    text.getStyleClass().add(SELECTED);
                    //text.setMouseTransparent(true);
                }
            });
            text.setOnDragDetected(e -> {
                text.startFullDrag();
            });
            text.setOnMouseDragEntered(e -> {
                if (startTextIndex != -1) {
                    System.out.println("dragged");
                    removeSelectedClass(texts);
                    final int myIndex = texts.indexOf(text);
                    final int min = Math.min(startTextIndex, myIndex);
                    final int max = Math.max(startTextIndex, myIndex);
                    addSelectedClass(texts.subList(min, max + 1));
                }
            });
            text.setOnMouseReleased(e -> {
                if (startTextIndex != -1) {
                    System.out.println("release");
                    startTextIndex = -1;
                    //text.setMouseTransparent(false);
                }
            });
            texts.add(text);
        }
    }
}
