package cz.cuni.mff.ufal.textan.tui;

import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.processreport.RelationBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple class extending RelationBuilder but adding none additional functionality.
 */
public class TxtRelationBuilder extends RelationBuilder {

    /**
     * Only constructor.
     * @param relation blue print
     */
    public TxtRelationBuilder(final Relation relation) {
        super(relation.getType());
    }

    @Override
    protected List<? extends IRelationInfo> createRelationInfos() {
        return new ArrayList<>();
    }
}
