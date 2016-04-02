package org.controlsfx.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Wrapper for {@link javafx.scene.control.Alert} and
 * {@link javafx.scene.control.TextInputDialog} mimicking interface of missing
 * org.controlsfx.dialog.Dialogs.
 */
public class Dialogs {

    protected String masthead = "";
    protected String message = null;
    protected Window owner = null;
    protected String title = "";
    protected List<Dialog.Action> actions;
    
    /**
     * Creates new Dialogs.
     * @return new Dialogs
     */
    public static Dialogs create() {
        return new Dialogs();
    }
    
    public Dialogs actions(final Dialog.Action... actions) {
        this.actions = new ArrayList<>(Arrays.asList(actions));
        return this;
    }
    
    private void initDialog(final javafx.scene.control.Dialog<?> dialog) {
        dialog.initOwner(owner);
        dialog.setTitle(title);
        dialog.setHeaderText(masthead);
        if (message != null) dialog.setContentText(message);
        resizingHack(dialog);
    }

    /**
     * Ugly hack for linux specific resizing bug.
     * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8087981">JavaFX jira</a>
     * @see <a href="http://stackoverflow.com/questions/28958164/javafxjava-8-update-40-alert-dialog-cant-redraw-and-resize-successfully">Stack overflow</a>
     */
    private void resizingHack(final javafx.scene.control.Dialog<?> dialog) {
        dialog.getDialogPane().expandedProperty().addListener((l) -> {
            Platform.runLater(() -> {
                dialog.getDialogPane().requestLayout();
                Stage stage = (Stage)dialog.getDialogPane().getScene().getWindow();
                stage.sizeToScene();
            });
        });
    }
    
    public Dialogs lightweight() {
        //for now nothing
        return this;
    }
    
    public Dialogs masthead(final String masthead) {
        this.masthead = masthead;
        return this;
    }
    
    public Dialogs message(final String message) {
        this.message = message;
        return this;
    }

    public Dialogs owner(final Object owner) {
        if (owner instanceof Window) {
            this.owner = (Window) owner;
        } else if (owner instanceof Node) {
            this.owner = ((Node) owner).getScene().getWindow();
        }
        return this;
    }
    
    public Dialogs owner(final Window owner) {
        this.owner = owner;
        return this;
    }
    
    private void show(final Alert.AlertType alertType) {
        final Alert alert = new Alert(alertType);
        initDialog(alert);
        alert.showAndWait();
    }
    
    public <T> T showChoices(final List<? extends T> choices) {
        final ChoiceDialog<T> dialog = new ChoiceDialog<>();
        initDialog(dialog);
        dialog.getItems().addAll(choices);
        final Optional<T> result = dialog.showAndWait();
        return result.isPresent() ? result.get() : null;
    }
    
    public Dialog.Action showConfirm() {
        final ButtonType[] types =
                actions.stream().map(Dialog.Action::toButtonType).toArray(ButtonType[]::new);
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", types);
        initDialog(alert);
        final Optional<ButtonType> clicked = alert.showAndWait();
        final ButtonType buttonType = clicked.isPresent() ? clicked.get() : ButtonType.CANCEL;
        return Dialog.Action.valueOf(buttonType);
    }
    
    public void showError() {
        show(Alert.AlertType.ERROR);
    }
    
    public void showException(final Throwable exception) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(exception.getMessage());
        initDialog(alert);
        if (message == null) alert.setContentText(exception.getMessage());
        
        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label(ResourceBundle.getBundle("org/controlsfx/dialog/dialogs").getString("exception.stacktrace"));

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
    
    public void showInformation() {
        show(Alert.AlertType.INFORMATION);
    }
    
    public String showTextInput(final String defaultValue) {
        final TextInputDialog loginDialog = new TextInputDialog(defaultValue);
        initDialog(loginDialog);
        final Optional<String> login = loginDialog.showAndWait();
        return login.isPresent() ? login.get() : null;
    }
    
    public void showWarning() {
        show(Alert.AlertType.WARNING);
    }
    
    public Dialogs title(final String title) {
        this.title = title;
        return this;
    }
    
    /**
     * Using factory method, so private constructor.
     */
    private Dialogs() {
        //nothing
    }
}
