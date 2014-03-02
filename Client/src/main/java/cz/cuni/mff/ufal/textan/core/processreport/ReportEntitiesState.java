package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.models.Entity;
import cz.cuni.mff.ufal.textan.commons.models.Object;
import cz.cuni.mff.ufal.textan.commons.models.Rating;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's entities.
 */
final class ReportEntitiesState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportEntitiesState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportEntitiesState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportEditState.class) {
                if (instance == null) {
                    instance = new ReportEntitiesState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportEntitiesState() { }

    @Override
    public StateType getType() {
        return StateType.EDIT_ENTITIES;
    }

    @Override
    public void setReportWords(final ProcessReportPipeline pipeline, final Word[] words) {
        pipeline.reportWords = words;
        List<Entity> ents = new ArrayList<>();
        EntityBuilder builder = null;
        int start = 0;
        StringBuilder alias = new StringBuilder();
        for (Word word : words) {
            if (word.getEntity() != builder) {
                if (builder != null) {
                    builder.index = ents.size();
                    ents.add(new Entity(alias.toString(), start, word.getStart() - start, builder.getId()));
                }
                start = word.getStart();
                alias.setLength(0);
                builder = word.getEntity();
            }
            alias.append(word.getWord());
        }
        if (builder != null) {
            builder.index = ents.size();
            ents.add(new Entity(alias.toString(), start, pipeline.reportText.length() - start, builder.getId()));
        }
        pipeline.reportEntities = ents.toArray(new Entity[ents.size()]);
        pipeline.reportObjectCandidates = pipeline.client.getDocumentProcessor().getObjects(pipeline.reportText, pipeline.reportEntities);
        if (pipeline.reportObjects == null) {
            pipeline.reportObjects = new Object[pipeline.reportObjectCandidates.length];
        }
        for (int i = 0; i < pipeline.reportObjectCandidates.length; ++i) {
            final Rating candidates = pipeline.reportObjectCandidates[i];
            if (candidates.candidate.length > 0) {
                pipeline.reportObjects[0] = candidates.candidate[0];
            }
        }
        pipeline.setState(ReportObjectsState.getInstance());
    }
}
