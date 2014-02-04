package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.utils.Pair;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Controls editing entities.
 * TOOD refactor entity handling
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

    /** Index of the first selected {@link Text} node. */
    int firstDragged = -1;

    /** Index of the lasty dragged {@link Text} node. */
    int lastDragged = -1;

    /** Index of the first selected {@link Text} node. */
    int firstSelectedIndex = -1;

    /** Index of the last selected {@link Text} node. */
    int lastSelectedIndex = -1;

    /** Flag indicating whether dragging is taking place. */
    boolean dragging = false;

    /** Context menu with entity selection. */
    ContextMenu contextMenu;

    /** List of words. Iterate to get marked entities. */
    List<Word> words = new ArrayList<>();

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
        EventHandler<ActionEvent> eh = (ActionEvent t) -> {
            final Entity e = new Entity(((MenuItem) t.getSource()).getText());
            try {
                e.add(words, firstSelectedIndex, lastSelectedIndex);
            } catch (Exception ex) {
                ex.printStackTrace();
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title("Došlo k chybě!")
                            .showException(ex);
                });
            }
        };
        final MenuItem mi = new MenuItem("Red");
        mi.setOnAction(eh);
        final MenuItem mi2 = new MenuItem("Green");
        mi2.setOnAction(eh);
        final MenuItem mi3 = new MenuItem("Blue");
        mi3.setOnAction(eh);
        contextMenu = new ContextMenu();
        contextMenu.getItems().add(mi);
        contextMenu.getItems().add(mi2);
        contextMenu.getItems().add(mi3);
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
        final String report = this.pipeline.getReportText();
        int start = 0;
        for(int i = 0; i < report.length(); ++i) {
            if (separators.contains(report.charAt(i))) {
                if (start < i) {
                    final String s = report.substring(start, i);
                    final Word word  = new Word(words.size(), start, i - 1, s);
                    words.add(word);
                }
                final String s = report.substring(i, i + 1);
                final Word word  = new Word(words.size(), i, i, s);
                words.add(word);
                start = i + 1;
            }
        }
        if (start < report.length()) {
            final String s = report.substring(start, report.length());
            final Word word  = new Word(words.size(), start, report.length(), s);
            words.add(word);
        }

        final List<Node> texts = textFlow.getChildren();
        texts.clear();
        for (final Word word: words) {
            final Text text = new Text(word.word);
            word.text = text;
            text.setOnMousePressed(e -> {
                if (!text.getStyleClass().contains(SELECTED)) {
                    //System.out.println("pressed");
                    removeSelectedClass(texts);
                    dragging = true;
                    firstDragged = texts.indexOf(text);
                    lastDragged = firstDragged;
                    firstSelectedIndex = firstDragged;
                    lastSelectedIndex = firstDragged;
                    text.getStyleClass().add(SELECTED);
                    //text.setMouseTransparent(true);
                }
            });
            text.setOnDragDetected(e -> {
                text.startFullDrag();
            });
            text.setOnMouseDragEntered(e -> {
                if (dragging) {
                    //System.out.println("dragged");
                    removeSelectedClass(texts);
                    final int myIndex = texts.indexOf(text);
                    final int min = Math.min(firstDragged, myIndex);
                    final int max = Math.max(firstDragged, myIndex);
                    addSelectedClass(texts.subList(min, max + 1));
                    firstSelectedIndex = min;
                    lastSelectedIndex = max;
                    if (!separators.contains(text.getText().charAt(0))) { //ignore separators in displaying the contextmenu
                        lastDragged = myIndex;
                    }
                }
            });
            text.setOnMouseReleased(e -> {
                if (dragging) {
                    //System.out.println("released");
                    dragging = false;
                    contextMenu.show(texts.get(lastDragged), Side.BOTTOM, 0, 0);
                    //text.setMouseTransparent(false);
                }
            });
            texts.add(text);
        }
    }

    /**
     * Simple class representing marked Entity.
     * Entities do not track their words, words track their entities.
     * To get marked entities, iterate word list.
     */
    private final class Entity {

        /** Entity name. Now used for css classsing. */
        final String name;

        /**
         * Only constructor.
         * @param name {@link #name} of the entity
         */
        Entity(final String name) {
            this.name = name.toUpperCase() + "_ENTITY";
        }

        /**
         * Checks whether this entity is nested in another one and throws exception if so.
         * @param words list of words
         * @param from starting index
         * @param to end index
         */
        private void checkAdding(final List<Word> words, final int from, final int to) {
            if (from == 0 || to == words.size() - 1) {
                return;
            }
            final Word first = words.get(from - 1);
            final Word last = words.get(to + 1);
            if (first.entity == last.entity && first.entity != null) {
                throw new IllegalArgumentException("Rozdělené entity nejsou podporovány!");
            }
        }

        /**
         * Adds words to the entity.
         * Checks input by {@link #checkAdding(java.util.List, int, int)}.
         * Also clears trailing and leading whites (as defined in
         * {@link #ignore(cz.cuni.mff.ufal.textan.gui.reportwizard.ReportEntitiesController.Word)}).
         * @param words list of words
         * @param from starting index
         * @param to final index (inclusive)
         * @see #trim(java.util.List, int, int)
         */
        void add(final List<Word> words, final int from, final int to) {
            checkAdding(words, from, to);
            Pair<Integer, Integer> bounds = trim(words, from, to);
            for (int i = bounds.getFirst(); i <= bounds.getSecond(); ++i) {
                final Word word = words.get(i);
                word.entity = this;
                word.text.getStyleClass().clear();
                word.text.getStyleClass().add(name);
            }
        }

        /**
         * Return true whether word should be ignored.
         * @param w word to assess
         * @return true if word should be ignored false otherwise.
         */
        private boolean ignore(Word w) {
            return separators.contains(w.text.getText().charAt(0));
        }

        /**
         * Clear words entities from the list. Starting from and end to are just
         * recommendation, as there may be adjacent whites as well.
         * @param words list of words
         * @param from starting index
         * @param to end index (inclusive)
         * @return pair with new trimmed from and to indeces
         */
        private Pair<Integer, Integer> trim(final List<Word> words, int from, int to) {
            //clear whites before from
            for (int i = from - 1; i >= 0 && ignore(words.get(i)); --i) {
                final Word w = words.get(i);
                w.entity = null;
                w.text.getStyleClass().clear();
            }
            //clear leading whites
            for (; from <= to && ignore(words.get(from)); ++from) {
                final Word w = words.get(from);
                w.entity = null;
                w.text.getStyleClass().clear();
            }
            //clear whites after to
            for (int i = to + 1; i < words.size() && ignore(words.get(i)); ++i) {
                final Word w = words.get(i);
                w.entity = null;
                w.text.getStyleClass().clear();
            }
            //clear trailing whites
            for (; to > from && ignore(words.get(to)); --to) {
                final Word w = words.get(to);
                w.entity = null;
                w.text.getStyleClass().clear();
            }
            return new Pair<>(from, to);
        }
    }

    /**
     * Simple holder for information about words.
     */
    private static final class Word {

        /** Word itself. */
        final String word;

        /** Index in the list of words. */
        final int index;

        /** Index of the first charater in the report. */
        final int start;

        /** Index of the last character in the report. */
        final int end;

        /** Text node displaying the word. */
        Text text;

        /** Entity that has this word assigned. */
        Entity entity;

        /**
         * Only constructor
         * @param index {@link #index}
         * @param start {@link #start}
         * @param end {@link #end}
         * @param word {@link #word}
         */
        Word(final int index, final int start, final int end, final String word) {
            this.index = index;
            this.start = start;
            this.end = end;
            this.word = word;
        }
    }
}
