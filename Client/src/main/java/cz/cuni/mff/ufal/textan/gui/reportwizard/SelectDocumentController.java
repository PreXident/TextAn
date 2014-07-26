package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.Window;
import cz.cuni.mff.ufal.textan.gui.document.DocumentListController;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * Controls selecting the document to process.
 */
public class SelectDocumentController extends ReportWizardController {

    @FXML
    private BorderPane root;

    @FXML
    private GridPane filterPane;

    @FXML
    private CheckBox processedCheckBox;

    @FXML
    private TextField filterField;

    @FXML
    private TableView<Document> table;

    @FXML
    private TableColumn<Document, Number> idColumn;

    @FXML
    private TableColumn<Document, Date> addTimeColumn;

    @FXML
    private TableColumn<Document, Date> lastChangeTimeColumn;

    @FXML
    private TableColumn<Document, Boolean> processedColumn;

    @FXML
    private TableColumn<Document, Date> processTimeColumn;

    @FXML
    private TableColumn<Document, Number> countColumn;

    @FXML
    private TableColumn<Document, String> textColumn;

    @FXML
    private ComboBox<Integer> perPageComboBox;

    @FXML
    private Label paginationLabel;

    @FXML
    private Slider slider;

    /** Controller of the document list. */
    protected DocumentListController listController;

    @FXML
    private void fastForward() {
        listController.fastForward();
    }

    @FXML
    private void fastRewind() {
        listController.fastRewind();
    }

    @FXML
    private void filter() {
        listController.filter();
    }

    @FXML
    private void forward() {
        listController.forward();
    }

    @FXML
    private void rewind() {
        listController.rewind();
    }

    @FXML
    private void next() {
        final Document document = table.getSelectionModel().getSelectedItem();
        if (document == null) {
            return;
        }
        if (pipeline.lock.tryAcquire()) {
            getMainNode().setCursor(Cursor.WAIT);
            new Thread(() -> {
                handleDocumentChangedException(root, () -> {
                    pipeline.setReport(document);
                    return null;
                });
            }, "FromSelectDocumentState").start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        slider.addEventFilter(EventType.ROOT, e -> e.consume());
        slider.setLabelFormatter(new SliderLabelFormatter() {
            @Override
            public String toString(Double val) {
                if (val < 0.5) {
                    return Utils.localize(rb, "report.wizard.selectdocument.label");
                }
                return super.toString(val);
            }
        });
        filterPane.getChildren().remove(processedCheckBox);
        listController = new DocumentListController();
        listController.addTimeColumn = addTimeColumn;
        listController.countColumn = countColumn;
        listController.filterField = filterField;
        listController.filterPane = filterPane;
        listController.idColumn = idColumn;
        listController.lastChangeTimeColumn = lastChangeTimeColumn;
        listController.paginationLabel = paginationLabel;
        listController.perPageComboBox = perPageComboBox;
        listController.processTimeColumn = processTimeColumn;
        listController.processedCheckBox = processedCheckBox;
        listController.processedColumn = processedColumn;
        listController.root = root;
        listController.table = table;
        listController.textColumn = textColumn;
        processedCheckBox.setIndeterminate(false);
        processedCheckBox.setSelected(false);
        listController.initialize(null, ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.document.DocumentList"));
        listController.processMI.disableProperty().unbind();
        listController.processMI.setDisable(true);
    }

    @Override
    public void setPipeline(final ProcessReportPipeline pipeline) {
        super.setPipeline(pipeline);
    }

    @Override
    public void setSettings(final Properties settings) {
        super.setSettings(settings);
        listController.setSettings(settings);
    }

    @Override
    public void setStage(final OuterStage stage) {
        super.setStage(stage);
        listController.setStage(stage);
    }

    @Override
    public void setTextAnController(final TextAnController textAnController) {
        super.setTextAnController(textAnController);
        listController.setTextAnController(textAnController);
        listController.setClient(textAnController.getClient());
    }

    @Override
    public void setWindow(final InnerWindow window) {
        super.setWindow(window);
        listController.setWindow(window);
    }

    @Override
    public void nowInControl() {
        filter();
    }
}
