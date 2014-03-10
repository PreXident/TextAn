package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public void setReportWords(final ProcessReportPipeline pipeline, final List<Word> words) {
        pipeline.reportWords = words;
        final List<Entity> ents = pipeline.reportEntities;
        ents.clear();
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
        pipeline.client.getObjects(pipeline.reportText, pipeline.reportEntities);
        pipeline.reportObjects.clear();
        for (Entity ent : pipeline.reportEntities) {
            final Optional<Double> max = ent.getCandidates().keySet().stream().max(Double::compare);
            if (max.isPresent()) {
                ent.setCandidate(ent.getCandidates().get(max.get()));
            }
        }
        pipeline.setState(ReportObjectsState.getInstance());
    }
}
