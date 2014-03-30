package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.RelationType;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportRelationsController.RelationInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Simple class representing marked Relation.
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list.
 */
public class RelationBuilder extends AbstractBuilder {

    /** Relation type. */
    protected final RelationType type;

    /**
     * Objects in relation.
     */
    public final ObservableList<RelationInfo> data = FXCollections.observableArrayList();

    /**
     * Only constructor.
     * @param type relation type
     */
    public RelationBuilder(final RelationType type) {
        this.type = type;
    }

    /**
     * Returns relation type.
     * @return relation type
     */
    public RelationType getType() {
        return type;
    }

    @Override
    protected RelationBuilder extract(final Word word) {
        return word.getRelation();
    }

    @Override
    protected void register(Word word) {
        word.setRelation(this);
    }

    @Override
    protected void unregister(Word word) {
        word.setRelation(null);
    }
}
