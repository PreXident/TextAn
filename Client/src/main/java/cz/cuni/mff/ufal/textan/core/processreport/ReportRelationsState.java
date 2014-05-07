package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.IdNotFoundException;

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

    @Override
    public void back(final ProcessReportPipeline pipeline) {
        pipeline.setState(ReportObjectsState.getInstance());
    }

    @Override
    public void setReportRelations(final ProcessReportPipeline pipeline,
            final List<Word> words,
            final List<? extends RelationBuilder> unanchoredRelations) {
        pipeline.reportWords = words;
        final List<RelationBuilder> rels = pipeline.reportRelations;
        rels.clear();
        RelationBuilder builder = null;
        int start = 0;
        int counter = 0;
        StringBuilder alias = new StringBuilder();
        for (Word word : words) {
            if (word.getRelation() != builder) {
                if (builder != null) {
                    builder.index = ++counter;
                    builder.position = start;
                    builder.alias = alias.toString();
                    rels.add(builder);
                }
                start = word.getStart();
                alias.setLength(0);
                builder = word.getRelation();
            }
            alias.append(word.getWord());
        }
        if (builder != null) {
            builder.index = ++counter;
            rels.add(builder);
        }
        rels.addAll(unanchoredRelations);
        pipeline.reportRelations = rels;

        //TODO: handle exception correctly!
        try {
            pipeline.client.saveProcessedDocument(
                    pipeline.ticket,
                    pipeline.getReportText(),
                    pipeline.reportEntities,
                    pipeline.reportRelations);
        } catch (IdNotFoundException e) {
            e.printStackTrace();
        }

        pipeline.setState(DoneState.getInstance());
    }
}
