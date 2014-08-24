package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.DocumentData.Occurrence;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Relation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's objects.
 */
final class ReportObjectsState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportObjectsState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportObjectsState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportObjectsState.class) {
                if (instance == null) {
                    instance = new ReportObjectsState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportObjectsState() { }

    @Override
    public State.StateType getType() {
        return State.StateType.EDIT_OBJECTS;
    }

    @Override
    protected java.lang.Object readResolve() {
        return getInstance();
    }

    @Override
    public void back(final ProcessReportPipeline pipeline) {
        pipeline.incStepsBack();
        pipeline.setState(ReportEntitiesState.getInstance());
    }

    @Override
    public void setReportObjects(final ProcessReportPipeline pipeline,
            final List<Entity> entities, final Function<Relation, RelationBuilder> factory)
            throws DocumentAlreadyProcessedException, DocumentChangedException {
        pipeline.reportEntities = entities;
        if (pipeline.getStepsBack() <= 0) {
            pipeline.reportWords.stream().forEach(w -> w.setRelation(null));
            pipeline.getReportRelations().clear();
            
            final List<Relation> relations = new ArrayList<>();
            final List<Occurrence> occurrences = new ArrayList<>();
            
            if (pipeline.reportId > 0) {
                try {
                    pipeline.client.getRelations(pipeline.ticket,
                            pipeline.reportId, pipeline.reportEntities,
                            relations, occurrences);
                } catch (IdNotFoundException ex) {
                    throw new RuntimeException("This should never happen!", ex);
                }
            } else {
                pipeline.client.getRelations(pipeline.ticket,
                        pipeline.reportText, pipeline.reportEntities,
                        relations, occurrences);
            }
            
            Map<Long, RelationBuilder> relationMap = new HashMap<>();
            for (Relation rel : relations) {
                final RelationBuilder builder = factory.apply(rel);
                relationMap.put(rel.getId(), builder);
                pipeline.reportRelations.add(builder);
            }
            
            int i = 0;
            final List<Word> words = pipeline.getReportWords();
            for (Occurrence occurrence : occurrences) {
                final RelationBuilder builder = relationMap.get(occurrence.id);
                int entEnd = occurrence.position + occurrence.length;
                //find first word
                while (i < words.size()
                        && words.get(i).getEnd() < occurrence.position) {
                    ++i;
                }
                //proces words in relation
                while (i < words.size()
                        && words.get(i).getStart() <= entEnd) {
                    builder.register(words.get(i));
                    ++i;
                }
            }
            
        } else {
            pipeline.decStepsBack();
        }
        pipeline.setState(ReportRelationsState.getInstance());
    }
}
