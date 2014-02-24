package cz.cuni.mff.ufal.textan.core.processreport;

import static cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.separators;
import cz.cuni.mff.ufal.textan.commons.models.Entity;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link ProcessReportPipeline}'s {@link State} for editing the report's text.
 */
final class ReportEditState extends State {

    /** Holds the singleton's intance. */
    static private volatile ReportEditState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static ReportEditState getInstance() {
        if (instance == null) { //double checking
            synchronized (ReportEditState.class) {
                if (instance == null) {
                    instance = new ReportEditState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private ReportEditState() { }

    @Override
    public void setReport(final ProcessReportPipeline pipeline, final String report) {
        pipeline.reportText = report;
        IDocumentProcessor documentProcessor = pipeline.client.getDocumentProcessor();
        pipeline.reportEntities = documentProcessor.getEntities(report);
        Arrays.sort(pipeline.reportEntities, (Entity e1, Entity e2) -> e1.getPosition() - e2.getPosition());
        pipeline.reportWords = parse(report);
        assign(pipeline.reportWords, pipeline.reportEntities);
        pipeline.setState(ReportEntitiesState.getInstance());
    }

    @Override
    public StateType getType() {
        return StateType.EDIT_REPORT;
    }

    private void assign(final Word[] words, final Entity[] entities) {
        int i = 0;
        for (Entity entity : entities) {
            final EntityBuilder builder = new EntityBuilder(entity.getType());
            int entEnd = entity.getPosition() + entity.getLength();
            //find first word
            while (i < words.length
                    && words[i].getEnd() < entity.getPosition()) {
                ++i;
            }
            //proces words in entity
            while (i < words.length
                    && words[i].getStart() <= entEnd) {
                words[i].setEntity(builder);
            }
        }
    }

    private Word[] parse(final String report) {
        int start = 0;
        final List<Word> words = new ArrayList<>();
        for(int i = 0; i < report.length(); ++i) {
            if (separators.contains(report.charAt(i))) {
                if (start < i) {
                    final String s = report.substring(start, i);
                    final Word word  = new Word(words.size(), start, i - 1, s);
                    words.add(word);
                }
                final String s = report.substring(i, i + 1);
                final Word word  = new Word(words.size(), i, i, s);
                words.add(word);
                start = i + 1;
            }
        }
        if (start < report.length()) {
            final String s = report.substring(start, report.length());
            final Word word  = new Word(words.size(), start, report.length(), s);
            words.add(word);
        }
        return words.toArray(new Word[words.size()]);
    }
}
