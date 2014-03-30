package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.gui.reportwizard.ReportRelationsController.RelationInfo;
import java.util.List;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's relations.
 */
final class ReportRelationsState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportRelationsState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportRelationsState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportRelationsState.class) {
                if (instance == null) {
                    instance = new ReportRelationsState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportRelationsState() { }

    @Override
    public State.StateType getType() {
        return State.StateType.EDIT_RELATIONS;
    }

    private Relation createRelation(final RelationBuilder builder) {
        final Relation relation = new Relation(-1, builder.getType());
        for (RelationInfo relInfo : builder.data) {
            relation.getObjects().add(new Pair<>(relInfo.object.get(), relInfo.order.get()));
        }
        return relation;
    }

    @Override
    public void setReportRelations(final ProcessReportPipeline pipeline, final List<Word> words) {
        pipeline.reportWords = words;
        final List<Relation> rels = pipeline.reportRelations;
        rels.clear();
        RelationBuilder builder = null;
        for (Word word : words) {
            if (word.getRelation() != builder) {
                if (builder != null) {
                    builder.index = rels.size();
                    rels.add(createRelation(builder));
                }
                builder = word.getRelation();
            }
        }
        if (builder != null) {
            builder.index = rels.size();
            rels.add(createRelation(builder));
        }
        pipeline.reportRelations = rels;
        pipeline.client.saveProcessedDocument(
                pipeline.ticket,
                pipeline.getReportText(),
                pipeline.reportEntities,
                pipeline.reportRelations);
        pipeline.setState(DoneState.getInstance());
    }
}
