package cz.cuni.mff.ufal.autopolan;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 */
public class NewReportController implements Initializable {

    static final String TEST_TEXT = "Ahoj, toto je testovaci zprava urcena pro vyzkouseni vsech moznosti oznacovani textu.";

    static final Set<Character> separators = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('\n', '\t', '\r', ' ', ',', '.', ';')));

    static void addSelectedClass(Iterable<Node> list) {
        list.forEach(node -> node.getStyleClass().add("selected"));
    }

    static void removeSelectedClass(Iterable<Node> list) {
        list.forEach(node -> node.getStyleClass().remove("selected"));
    }

    @FXML
    private TextFlow textFlow;

    int startTextIndex = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final List<String> words = new ArrayList<>();
        int start = 0;
        for(int i = 0; i < TEST_TEXT.length(); ++i) {
            if (separators.contains(TEST_TEXT.charAt(i))) {
                if (start < i) {
                    words.add(TEST_TEXT.substring(start, i));
                }
                words.add(TEST_TEXT.substring(i, i + 1));
                start = i + 1;
            }
        }
        //words.stream().forEach(word -> System.out.println(word));
        //System.out.println(words.stream().reduce((s1, s2) -> {return s1 + s2;}).orElse("nic!"));

        final List<Node> texts = textFlow.getChildren();
        for (String word: words) {
            final Text text = new Text(word);
            text.setOnMousePressed(e -> {
                if (!text.getStyleClass().contains("selected")) {
                    System.out.println("pressed");
                    removeSelectedClass(texts);
                    startTextIndex = texts.indexOf(text);
                    text.getStyleClass().add("selected");
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
