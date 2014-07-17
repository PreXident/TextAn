package cz.cuni.mff.ufal.textan.gui.relation;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.gui.ObjectContextMenu;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * TreeView for displaying relations.
 */
public class RelationTreeView extends TreeView<java.lang.Object> {

    /** Relation Object that is selected. */
    final ObjectProperty<Object> selectedRelationObject = new SimpleObjectProperty<>();

    /** Context menu for relation's objects. */
    ObjectContextMenu relationsContextMenu;

    /** Application controller. */
    TextAnController textAnController;

    /** Flag indicating whether the tree view should autosize. */
    boolean autosize = false;

    /**
     * Constructs RelationTreeView.
     */
    public RelationTreeView() {
        setShowRoot(false);
        final TreeItem<java.lang.Object> root = new TreeItem<>(null);
        root.expandedProperty().addListener(ov -> { autosizeTreeView(); });
        setRoot(root);
        getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            if (newVal == null || !(newVal.getValue() instanceof Triple)) {
                this.setContextMenu(null);
                selectedRelationObject.set(null);
            } else {
                setContextMenu(relationsContextMenu);
                @SuppressWarnings("unchecked")
                final Triple<Integer, String, cz.cuni.mff.ufal.textan.core.Object> triple =
                      (Triple<Integer, String, cz.cuni.mff.ufal.textan.core.Object>) newVal.getValue();
                selectedRelationObject.set(triple.getThird());
            }
        });
        setCellFactory(new Callback<TreeView<java.lang.Object>, TreeCell<java.lang.Object>>() {
            @Override
            public TreeCell<java.lang.Object> call(TreeView<java.lang.Object> p) {
                return new TreeCell<java.lang.Object>() {
                    @Override
                    protected void updateItem(java.lang.Object o, boolean empty) {
                        super.updateItem(o, empty);
                        if (!empty && o != null) {
                            if (o instanceof Triple) {
                                @SuppressWarnings("unchecked")
                                final Triple<Integer, String, Object> triple =
                                        (Triple<Integer, String, Object>) o;
                                this.setText(String.format("%s - %s - %s",
                                        triple.getFirst(), triple.getSecond(), triple.getThird()));
                            } else {
                                this.setText(o.toString());
                            }
                        } else {
                            this.setText("");
                        }
                    }
                };
            }
        });
    }

    /**
     * Contructs relation tree view and add the relations.
     * @param relations relations to add
     */
    public RelationTreeView(Collection<Relation> relations) {
        this();
        addRelations(relations);
    }

    /**
     * Sets whether the tree view should autosize.
     * @param autosize new value
     */
    public void setAutoSize(final boolean autosize) {
        this.autosize = autosize;
        autosizeTreeView();
    }

    /**
     * Sets the application controller.
     * @param textAnController new application controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
        relationsContextMenu = new ObjectContextMenu(textAnController);
        relationsContextMenu.objectProperty().bind(selectedRelationObject);
    }

    /**
     * Adds relations to the relation tree view.
     * @param relations relations to add
     */
    public final void addRelations(final Collection<Relation> relations) {
        for (Relation relation : relations) {
            final TreeItem<java.lang.Object> parent = new TreeItem<>(relation);
            parent.expandedProperty().addListener(ov -> autosizeTreeView());
            for (Triple<Integer, String, Object> triple : relation.getObjects()) {
                final TreeItem<java.lang.Object> child = new TreeItem<>(triple);
                parent.getChildren().add(child);
            }
            getRoot().getChildren().add(parent);
        }
    }

    /**
     * Autosizes treeview.
     */
    protected void autosizeTreeView() {
        if (!autosize) {
            return;
        }
        final double PER_ITEM = 35;
        double h = 20;
        Deque<TreeItem<java.lang.Object>> stack = new ArrayDeque<>();
        if (isShowRoot()) {
            stack.add(getRoot());
        } else {
            stack.addAll(getRoot().getChildren());
        }
        while(!stack.isEmpty()) {
            final TreeItem<java.lang.Object> item = stack.pop();
            h += PER_ITEM;
            if (item.isExpanded()) {
                stack.addAll(item.getChildren());
            }
        }
        setPrefHeight(h);
    }
}
