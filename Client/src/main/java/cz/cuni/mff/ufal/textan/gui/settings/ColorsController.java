package cz.cuni.mff.ufal.textan.gui.settings;

import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.Utils.IdType;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.net.URL;
import java.text.Collator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/**
 * Controls selecting object to be displayed in the graph.
 */
public class ColorsController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Colors";

    /** Identifier used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "colors.view";

    /** Minimal height of the join window. */
    static protected final int MIN_HEIGHT = 450;

    /** Minimal width of the join window. */
    static protected final int MIN_WIDTH = 525;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<ObjectType> objectTable;

    @FXML
    private TableColumn<ObjectType, Number> objectIdColumn;

    @FXML
    private TableColumn<ObjectType, String> objectNameColumn;

    @FXML
    private TableColumn<ObjectType, Color> objectColorColumn;

    @FXML
    private TableView<RelationType> relationTable;

    @FXML
    private TableColumn<RelationType, Number> relationIdColumn;

    @FXML
    private TableColumn<RelationType, String> relationNameColumn;

    @FXML
    private TableColumn<RelationType, Color> relationColorColumn;

    /** Localization controller. */
    private ResourceBundle resourceBundle;

    /** Application controller. */
    private TextAnController textAnController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
        objectIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectType, Number> p) -> new ReadOnlyLongWrapper(p.getValue().getId()));
        objectIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return t != null ? t.toString() : "";
            }
            @Override
            public Number fromString(String string) {
                return Long.parseLong(string);
            }
        }));
        objectNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectType, String> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getName()));
        objectColorColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectType, Color> p) -> new SimpleObjectProperty<>(Utils.resolveEntityColorFX(settings, p.getValue().getId(), true)));
        objectColorColumn.setCellFactory(p -> new TextFieldTableCell<ObjectType, Color>() {
            @Override
            public void updateItem(Color color, boolean empty) {
                super.updateItem(color, empty);
                setText("");
                if (empty) {
                    setGraphic(null);
                } else {
                    final ColorPicker picker = new ColorPicker(color);
                    picker.valueProperty().addListener((ov, oldVal, newVal) -> {
                        updateItem(newVal, false);
                        final ObjectType item = (ObjectType) getTableRow().getItem();
                        storeObjectColor(item.getId(), newVal);
                    });
                    setGraphic(picker);
                }
            }
        });
        //
        relationIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<RelationType, Number> p) -> new ReadOnlyLongWrapper(p.getValue().getId()));
        relationIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return t != null ? t.toString() : "";
            }
            @Override
            public Number fromString(String string) {
                return Long.parseLong(string);
            }
        }));
        relationNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<RelationType, String> p) -> new ReadOnlyObjectWrapper<>(p.getValue().getName()));
        relationColorColumn.setCellValueFactory((TableColumn.CellDataFeatures<RelationType, Color> p) -> new SimpleObjectProperty<>(Utils.resolveRelationColorFX(settings, p.getValue().getId(), true)));
        relationColorColumn.setCellFactory(p -> new TextFieldTableCell<RelationType, Color>() {
            @Override
            public void updateItem(Color color, boolean empty) {
                super.updateItem(color, empty);
                setText("");
                if (empty) {
                    setGraphic(null);
                } else {
                    final ColorPicker picker = new ColorPicker(color);
                    picker.valueProperty().addListener((ov, oldVal, newVal) -> {
                        updateItem(newVal, false);
                        final RelationType item = (RelationType) getTableRow().getItem();
                        storeRelationColor(item.getId(), newVal);
                    });
                    setGraphic(picker);
                }
            }
        });
    }

    @Override
    public void setSettings(final Properties settings) {
        super.setSettings(settings);
    }

    /**
     * Sets application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
        final Typer typer = new Typer();
        typer.setOnSucceeded(e -> {
            objectTable.getItems().addAll(typer.objectTypes);
            relationTable.getItems().addAll(typer.relationTypes);
        });
        typer.setOnFailed(e -> {
            getMainNode().setCursor(Cursor.DEFAULT);
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(resourceBundle, "page.load.error"))
                        .showException(typer.getException());
                closeContainer();
            });
        });
        new Thread(typer, "Typer").start();
    }

    /**
     * Stores color for given id in settings.
     * @param type id type
     * @param id id
     * @param color color
     */
    protected void storeColor(final IdType type, final long id, final Color color) {
        final String s = Utils.colorToString(color);
        settings.put(type + ".color." + id, s);
    }

    /**
     * Stores color for given object id in settings.
     * @param id id
     * @param color color
     */
    protected void storeObjectColor(final long id, final Color color) {
        storeColor(IdType.ENTITY, id, color);
    }

    /**
     * Stores color for given relation id in settings.
     * @param id id
     * @param color color
     */
    public void storeRelationColor(final long id, final Color color) {
        storeColor(IdType.RELATION, id, color);
    }

    /**
     * Simple task to get object and relation types from server.
     */
    class Typer extends Task<Void> {

        /** Object types from db. */
        public List<ObjectType> objectTypes;

        /** Relation types from db. */
        public List<RelationType> relationTypes;

        @Override
        protected Void call() throws Exception {
            objectTypes = textAnController.getClient().getObjectTypesList();
            final Collator collator = Collator.getInstance();
            objectTypes.sort((type1, type2) -> collator.compare(type1.getName(), type2.getName()));
            relationTypes = textAnController.getClient().getRelationTypesList();
            relationTypes.sort((type1, type2) -> collator.compare(type1.getName(), type2.getName()));
            return null;
        }
    };
}
