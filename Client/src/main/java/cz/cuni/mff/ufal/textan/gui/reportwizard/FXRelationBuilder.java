package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Descendant of {@link RelationBuilder} for JavaFX.
 */
public class FXRelationBuilder extends RelationBuilder {

    /**
     * List to remove builder from if words count hits zero.
     * This needs to be updated on return to relation edit!
     */
    List<FXRelationBuilder> list;

    /** List of words assigned to the builder. */
    final List<Word> words = new ArrayList<>();

    /** Holds string representation of the builder. */
    final StringProperty stringRepresentation = new SimpleStringProperty("");

    /**
     * Only constructor.
     * @param type relation type
     * @param list {@link #list}
     */
    public FXRelationBuilder(final RelationType type, final List<FXRelationBuilder> list) {
        super(type);
        this.list = list;
        list.add(this);
        updateStringRepresentation();
    }

    @Override
    protected List<? extends IRelationInfo> createRelationInfos() {
        return FXCollections.observableArrayList();
    }

    @SuppressWarnings("unchecked")
    ObservableList<RelationInfo> getData() {
        return (ObservableList<RelationInfo>) data;
    }

    @Override
    protected void register(Word word) {
        unregisterOldBuilder(word);
        words.add(word);
        super.register(word);
        updateStringRepresentation();
    }

    @Override
    public String toString() {
        return stringRepresentation.get();
    }

    @Override
    protected void unregister(Word word) {
        if (words.remove(word) && words.isEmpty()) {
            list.remove(this);
        }
        super.unregister(word);
        updateStringRepresentation();
    }

    /**
     * Updates {@link #stringRepresentation}.
     */
    protected void updateStringRepresentation() {
        if (words.isEmpty()) {
            stringRepresentation.setValue(type.toString());
            return;
        }
        final StringBuilder repre = new StringBuilder(type.toString() + " (");
        for (Word w : words) {
            repre.append(w.getWord());
        }
        repre.append(")");
        stringRepresentation.setValue(repre.toString());
    }

    /**
     * Holds information about object assigned to relation in JavaFX way.
     */
    public static class RelationInfo implements IRelationInfo {

        /** Object's order. */
        public SimpleIntegerProperty order = new SimpleIntegerProperty();

        /** Object itself. */
        public SimpleObjectProperty<Object> object =
                new SimpleObjectProperty<>();

        /** Object's role. */
        public SimpleStringProperty role = new SimpleStringProperty();

        /**
         * Only constructor.
         * @param order object's order
         * @param object object itself
         */
        public RelationInfo(int order, Object object) {
            this.order.set(order);
            this.object.set(object);
        }

        @Override
        public int getOrder() {
            return order.get();
        }

        @Override
        public Object getObject() {
            return object.get();
        }

        @Override
        public String getRole() {
            return role.get();
        }
    }
}
