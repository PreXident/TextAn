package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.processreport.candidate.CandidateSelectorService;
import cz.cuni.mff.ufal.textan.core.processreport.candidate.ICandidateSelector;
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
    protected java.lang.Object readResolve() {
        return getInstance();
    }

    @Override
    public void back(final ProcessReportPipeline pipeline) {
        if (pipeline.reportId < 1) {
            pipeline.incStepsBack();
            pipeline.setState(ReportEditState.getInstance());
        } else {
            pipeline.lock.release();
        }
    }

    @Override
    public void setReportWords(final ProcessReportPipeline pipeline,
            final List<Word> words) throws DocumentChangedException,
            DocumentAlreadyProcessedException {
        pipeline.reportWords = words;
        if (pipeline.getStepsBack() <= 0) {
            final List<Entity> ents = pipeline.reportEntities;
            ents.clear();
            EntityBuilder builder = null;
            int start = 0;
            StringBuilder alias = new StringBuilder();
            for (Word word : words) {
                if (word.getEntity() != builder) {
                    if (builder != null) {
                        builder.index = ents.size();
                        ents.add(new Entity(alias.toString(), start, word.getStart() - start, builder.getType()));
                    }
                    start = word.getStart();
                    alias.setLength(0);
                    builder = word.getEntity();
                }
                alias.append(word.getWord());
            }
            if (builder != null) {
                builder.index = ents.size();
                ents.add(new Entity(alias.toString(), start, pipeline.reportText.length() - start, builder.getType()));
            }
            if (pipeline.reportId > 0) {
                try {
                    pipeline.client.getObjects(pipeline.ticket, pipeline.reportId, pipeline.reportEntities);
                } catch (IdNotFoundException ex) {
                    throw new RuntimeException("This should never happen!", ex);
                }
            } else {
                pipeline.client.getObjects(pipeline.ticket, pipeline.reportText, pipeline.reportEntities);
            }
            final String selectorId = pipeline.getClient().getSettings().getProperty("candidate", "MaxSelector");
            final ICandidateSelector selector = CandidateSelectorService.getInstance().getSelector(selectorId);
            for (Entity ent : pipeline.reportEntities) {
                final Object candidate = selector.selectCandidate(ent.getCandidates());
                if (candidate != null) {
                    ent.setCandidate(candidate);
                }
            }
        } else {
            pipeline.decStepsBack();
        }
        pipeline.setState(ReportObjectsState.getInstance());
    }
}
