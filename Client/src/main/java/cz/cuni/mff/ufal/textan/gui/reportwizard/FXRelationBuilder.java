package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import cz.cuni.mff.ufal.textan.core.processreport.Word;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Descendant of {@link RelationBuilder} for JavaFX.
 */
public class FXRelationBuilder extends RelationBuilder {

    List<FXRelationBuilder> list;

    final List<Word> words = new ArrayList<>();

    public FXRelationBuilder(final RelationType type, final List<FXRelationBuilder> list) {
        super(type);
        this.list = list;
        list.add(this);
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
        final RelationBuilder old = word.getRelation();
        if (old != null && old instanceof FXRelationBuilder) {
            ((FXRelationBuilder) old).unregister(word);
        }
        words.add(word);
        super.register(word);
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    protected void unregister(Word word) {
        if (words.remove(word) && words.isEmpty()) {
            list.remove(this);
        }
        super.unregister(word);
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
