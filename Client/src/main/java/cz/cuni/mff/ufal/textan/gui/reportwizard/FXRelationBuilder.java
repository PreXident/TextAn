package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Descendant of {@link RelationBuilder} for JavaFX.
 * Contains a list of assigned words for better textual representation.
 */
public class FXRelationBuilder extends RelationBuilder {

    /**
     * List to remove builder from if words count hits zero.
     * This needs to be updated on return to relation edit!
     */
    transient List<FXRelationBuilder> list;

    /** List of words assigned to the builder. */
    final transient List<Word> words = new ArrayList<>();

    /** Holds string representation of the builder. */
    final transient StringProperty stringRepresentation = new SimpleStringProperty("");

    /**
     * Constructs builder from scratch.
     * @param type relation type
     * @param list {@link #list}
     * @param roles most common roles
     */
    public FXRelationBuilder(final RelationType type,
            final List<FXRelationBuilder> list, final List<String> roles) {
        super(type);
        this.list = list;
        list.add(this);
        updateStringRepresentation();
        for (String role : roles) {
            final FXRelationInfo relationInfo = new FXRelationInfo(0, role, null);
            getData().add(relationInfo);
        }
    }

    /**
     * Constructs builder from deserialized proxy.
     * @param proxy deserialized proxy
     */
    @SuppressWarnings("unchecked")
     public FXRelationBuilder(final RelationBuilderProxy proxy) {
         super(proxy.getType());
         updateStringRepresentation();
         ((List)data).addAll(proxy.getData());
     }

    @Override
    protected List<? extends IRelationInfo> createRelationInfos() {
        return FXCollections.observableArrayList();
    }

    @SuppressWarnings("unchecked")
    ObservableList<FXRelationInfo> getData() {
        return (ObservableList<FXRelationInfo>) data;
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
    public static class FXRelationInfo extends RelationInfo {

        /** Object's order. */
        private final transient IntegerProperty order =
                new SimpleIntegerProperty();

        /** Object itself. */
        private final transient ObjectProperty<Object> object =
                new SimpleObjectProperty<>();

        /** Object's role. */
        private final transient StringProperty role =
                new SimpleStringProperty();

        /**
         * Only constructor.
         * @param order object's order
         * @param role object's role
         * @param object object itself
         */
        public FXRelationInfo(final int order, final String role, final Object object) {
            this.order.set(order);
            this.role.set(role);
            this.object.set(object);
        }

        @Override
        public Object getObject() {
            return object.get();
        }

        /**
         * Sets assigned object.
         * @param object new object
         */
        public void setObject(final Object object) {
            this.object.set(object);
        }

        /**
         * Returns assigned object property.
         * @return assigned object property
         */
        public ObjectProperty<Object> objectProperty() {
            return object;
        }

        @Override
        public int getOrder() {
            return order.get();
        }

        /**
         * Sets assigned object's order
         * @param order new order
         */
        public void setOrder(final int order) {
            this.order.set(order);
        }

        /**
         * Returns assigned object's order property.
         * @return assigned object's order property
         */
        public IntegerProperty orderProperty() {
            return order;
        }

        @Override
        public String getRole() {
            return role.get();
        }

        /**
         * Sets assigned object's role.
         * @param role new role
         */
        public void setRole(final String role) {
            this.role.set(role);
        }

        /**
         * Returns assigned object's role property.
         * @return assigned object's role property
         */
        public StringProperty roleProperty() {
            return role;
        }
    }
}
