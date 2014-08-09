package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.load.ImportManager;
import cz.cuni.mff.ufal.textan.core.processreport.load.Importer;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

/**
 * Controls editing the report.
 */
public class SelectFileController extends ReportWizardController {

    @FXML
    BorderPane root;

    @FXML
    TextArea textArea;

    @FXML
    ScrollPane scrollPane;

    @FXML
    ComboBox<Importer> typeComboBox;

    /** File content. */
    byte[] data;

    /** Selected file extension. */
    String extension;

    @FXML
    private void next() {
        if (pipeline.lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            new Thread(() -> {
                final String t = textArea.getText();
                handleDocumentChangedException(root, () -> {
                    pipeline.setReportTextAndParse(t);
                    return null;
                });
            }, "FromSelectFileState").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        slider.setLabelFormatter(new SliderLabelFormatter() {
            @Override
            public String toString(Double val) {
                if (val < 0.5) {
                    return Utils.localize(rb, "report.wizard.selectfile.label");
                }
                return super.toString(val);
            }
        });
        typeComboBox.setConverter(new StringConverter<Importer>() {
            @Override
            public String toString(final Importer t) {
                return Utils.localize(resourceBundle, "type." + t.getId());
            }
            @Override
            public Importer fromString(final String string) {
                throw new RuntimeException("This should never happen!");
            }
        });
        typeComboBox.getItems().addAll(ImportManager.getImporters());
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal == null) {
                textArea.setText("");
                return;
            }
            textArea.setText(pipeline.extractText(data, newVal));
            settings.setProperty("selectfile.extension." + extension + ".type", newVal.toString());
        });
    }

    /**
     * Extracts extension from file name.
     * @param name file name
     * @return extension from file name
     */
    protected String extractExtension(final String name) {
        final int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return "";
        }
        return name.substring(dot + 1);
    }

    @Override
    public void nowInControl() {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle(Utils.localize(resourceBundle, "select.file.prompt"));
        final String dir = settings.getProperty("selectfile.dir");
        if (dir != null && !dir.isEmpty()) {
            chooser.setInitialDirectory(new File(dir));
        } else {
            chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }
        final Window w = window != null ? window.getScene().getWindow() : stage;
        final File file = chooser.showOpenDialog(w);
        if (file == null || !file.isFile()) {
            closeContainer();
            return;
        }
        settings.setProperty("selectfile.dir", file.getParent());
        extension = extractExtension(file.getName());
        try {
            data = Files.readAllBytes(file.toPath());
            final String lastType = settings.getProperty("selectfile.extension." + extension + ".type");
            if (lastType != null) {
                try {
                    final Importer type = ImportManager.getImporter(lastType);
                    typeComboBox.getSelectionModel().select(type);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            if (typeComboBox.getSelectionModel().getSelectedItem() == null) {
                typeComboBox.getSelectionModel().select(ImportManager.getDefaultForExtension(extension));
            }
        } catch (IOException e) {
            e.printStackTrace();
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "error"))
                        .showException(e);
            });
            closeContainer();
        }
    }
}
