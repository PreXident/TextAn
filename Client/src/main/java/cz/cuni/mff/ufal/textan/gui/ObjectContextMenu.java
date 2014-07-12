package cz.cuni.mff.ufal.textan.gui;

import cz.cuni.mff.ufal.textan.core.Object;
import static cz.cuni.mff.ufal.textan.gui.Utils.CONTEXT_MENU_STYLE;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * Context menu for Objects in various places in the gui.
 */
public class ObjectContextMenu extends ContextMenu {

    /** Selected object property. */
    final ObjectProperty<Object> object = new SimpleObjectProperty<>();

    /** Distace for graph. */
    final IntegerProperty distance = new SimpleIntegerProperty(-1);

    /**
     * Only constructor.
     * @param controller application controller
     */
    public ObjectContextMenu(final TextAnController controller) {
        setStyle(CONTEXT_MENU_STYLE);
        setConsumeAutoHidingEvents(false);
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("cz.cuni.mff.ufal.textan.gui.TextAn");
        //
        final MenuItem graphMI = new MenuItem(Utils.localize(resourceBundle, "graph.show"));
        graphMI.setOnAction(e -> {
            if (object.get() != null) {
                if (distance.get() != -1) {
                    controller.displayGraph(object.get().getId(), distance.get());
                } else {
                    controller.displayGraph(object.get().getId());
                }
            }
        });
        graphMI.disableProperty().bind(object.isNull());
        getItems().add(graphMI);
        //
        final MenuItem documentMI = new MenuItem(Utils.localize(resourceBundle, "document.show"));
        documentMI.setOnAction(e -> {
            if (object.get() != null) {
                controller.displayDocuments(object.get().getId());
            }
        });
        documentMI.disableProperty().bind(object.isNull());
        getItems().add(documentMI);
    }

    /**
     * Returns graph distance property.
     * @return graph distance property
     */
    public int getDistance() {
        return distance.get();
    }

    /**
     * Sets graph distance property.
     * @param distance new graph distance
     */
    public void setDistance(final int distance) {
        this.distance.set(distance);
    }

    /**
     * Returns graph distance property.
     * @return graph distance property
     */
    public IntegerProperty distanceProperty() {
        return distance;
    }

    /**
     * Returns object property.
     * @return object property
     */
    public Object getObject() {
        return object.get();
    }

    /**
     * Sets new object property.
     * @param object new object
     */
    public void setObject(final Object object) {
        this.object.set(object);
    }

    /**
     * Returns object property.
     * @return object property
     */
    public ObjectProperty<Object> objectProperty() {
        return object;
    }
}
