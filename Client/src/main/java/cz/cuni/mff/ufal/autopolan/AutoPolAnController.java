package cz.cuni.mff.ufal.autopolan;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;

/**
 * Controller for the AutoPolAn application.
 */
public class AutoPolAnController implements Initializable {

    /** Original title. */
    static final String TITLE = "AutoPolAn";

    Properties settings = null;

    /** Property binded to stage titleProperty. */
    StringProperty titleProperty = new SimpleStringProperty(TITLE);

    @FXML
    private BorderPane root;

    @FXML
    private AnchorPane content;

    jfxtras.labs.scene.control.window.Window w;
    TextFlow t;

    @FXML
    private void close() {
        Platform.exit();
    }

    @FXML
    private void newReport() throws IOException {
        final Stage stage = new Stage();
        //create javafx controls
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("NewReport.fxml"));
        final Parent root = (Parent) loader.load();
        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @FXML
    private void newReport2() throws IOException {
        w = new jfxtras.labs.scene.control.window.Window("New Report");
        w.setPrefSize(500, 500);
        w.getRightIcons().add(new MinimizeIcon(w));
        w.getRightIcons().add(new CloseIcon(w));
        Group g = new Group();
        t = new TextFlow() {
            final String TEST_TEXT = "Ahoj, toto je testovaci zprava urcena pro vyzkouseni vsech moznosti oznacovani textu.";

            final Set<Character> separators = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('\n', '\t', '\r', ' ', ',', '.', ';')));

            void addSelectedClass(Iterable<Node> list) {
                list.forEach(node -> node.getStyleClass().add("selected"));
            }

            void removeSelectedClass(Iterable<Node> list) {
                list.forEach(node -> node.getStyleClass().remove("selected"));
            }

            int startTextIndex = -1;

            {
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

                final List<Node> texts = getChildren();
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
        };
        t.getStyleClass().add("text-flow");
        //
        ScrollPane s = new ScrollPane();
        t.prefWidthProperty().bind(s.widthProperty());
        s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.setContent(t);
        w.getContentPane().getChildren().add(s);
        g.getChildren().add(w);
        content.getChildren().add(g);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        System.out.printf("Initializing...\n");
    }

    /**
     * Sets AutoPolAn settings.
     * @param settings new settings
     */
    public void setSettings(final Properties settings) {
        this.settings = settings;
    }

    /**
     * Returns title property.
     * @return title property
     */
    public StringProperty titleProperty() {
        return titleProperty;
    }

    /**
     * Returns window of the root.
     * @return window of the root
     */
    private Window getWindow() {
//        return root.getScene().getWindow();
        return null;
    }

    /**
     * Called when application is stopped to store settings etc.
     */
    public void stop() {

    }
}
