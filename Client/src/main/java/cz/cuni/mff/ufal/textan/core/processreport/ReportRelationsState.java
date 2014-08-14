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
    protected java.lang.Object readResolve() {
        return getInstance();
    }

    @Override
    public void back(final ProcessReportPipeline pipeline) {
        pipeline.incStepsBack();
        pipeline.setState(ReportObjectsState.getInstance());
    }

    @Override
    public void setReportRelations(final ProcessReportPipeline pipeline,
            final List<Word> words,
            final List<? extends RelationBuilder> unanchoredRelations)
            throws DocumentChangedException, DocumentAlreadyProcessedException {
        if (pipeline.getStepsBack() <= 0) {
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
            //set ids of the unanchored relations
            for (RelationBuilder rel : unanchoredRelations) {
                rel.index = ++counter;
            }
            rels.addAll(unanchoredRelations);
            pipeline.reportRelations = rels;
            saveReport(pipeline, false);
        } else {
            pipeline.decStepsBack();
        }
        if (pipeline.result) {
            pipeline.setState(DoneState.getInstance());
        } else {
            pipeline.problems = pipeline.client.getProblems(pipeline.ticket);
            pipeline.setState(ReportErrorState.getInstance());
        }
    }

    /**
     * Saves report.
     * @param pipeline pipeline delegating the request
     * @param force indicator of force save
     * @throws DocumentChangedException
     * @throws DocumentAlreadyProcessedException
     */
    protected void saveReport(final ProcessReportPipeline pipeline,
            final boolean force) throws DocumentChangedException,
            DocumentAlreadyProcessedException {
        try {
            if (pipeline.reportId > 0) {
                pipeline.result = pipeline.client.saveProcessedDocument(
                        pipeline.ticket,
                        pipeline.reportId,
                        pipeline.reportEntities,
                        pipeline.reportRelations,
                        force);
            } else if (pipeline.replacingReportId > 0) {
                pipeline.result = pipeline.client.saveProcessedDocument(
                        pipeline.ticket,
                        pipeline.reportId,
                        pipeline.getReportText(),
                        pipeline.reportEntities,
                        pipeline.reportRelations,
                        force);
            } else {
                pipeline.result = pipeline.client.saveProcessedDocument(
                        pipeline.ticket,
                        pipeline.getReportText(),
                        pipeline.reportEntities,
                        pipeline.reportRelations,
                        force);
            }
        } catch (IdNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("This should never happen!", e);
        }
    }
}
