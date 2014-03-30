package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Descendant of {@link RelationBuilder} for JavaFX.
 */
public class FXRelationBuilder extends RelationBuilder {

    public FXRelationBuilder(final RelationType type) {
        super(type);
    }

    @Override
    protected List<? extends IRelationInfo> createRelationInfos() {
        return FXCollections.observableArrayList();
    }

    @SuppressWarnings("unchecked")
    ObservableList<RelationInfo> getData() {
        return (ObservableList<RelationInfo>) data;
    }

    public static class RelationInfo implements IRelationInfo {
        public SimpleIntegerProperty order = new SimpleIntegerProperty();
        public SimpleObjectProperty<Object> object =
                new SimpleObjectProperty<>();

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
    }
}
