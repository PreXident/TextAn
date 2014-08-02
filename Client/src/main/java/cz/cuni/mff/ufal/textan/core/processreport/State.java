package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline.FileType;
import java.io.Serializable;
import java.util.List;

/**
 * Abstract ancestor for states of {@link ProcessReportPipeline}.
 * The pipeline delegates its methods to its state passing this reference.
 * States are intented to be stateless singletons and should override methods
 * they are responsible for because default implementations just throw
 * {@link IllegalStateException}.
 */
public abstract class State implements Serializable {

    /**
     * Only constructor.
     */
    protected State() { }

    /**
     * Returns state's type.
     * @return state's type
     */
    public abstract StateType getType();

    /**
     * Implementation of deserialization.
     * @return singleton instance
     */
    protected abstract java.lang.Object readResolve();

    /**
     * Moves one step back in pipeline.
     * @param pipeline pipeline delegating the request
     */
    public void back(final ProcessReportPipeline pipeline) {
        throw new IllegalStateException("Cannot go back when in state " + getType());
    }

    /**
     * Forces the document to be save into the db.
     * @param pipeline pipeline delegating the request
     * @throws DocumentChangedException if processed document has been changed
     * @throws DocumentAlreadyProcessedException if document has been already processed
     */
    public void forceSave(final ProcessReportPipeline pipeline)
            throws DocumentChangedException, DocumentAlreadyProcessedException {
        throw new IllegalStateException("Cannot force save when in state " + getType());
    }

    /**
     * Selects database as a source of the new report.
     * Available in {@link State.StateType#LOAD} state.
     * @param pipeline pipeline delegating the request
     */
    public void selectDatabaseDatasource(final ProcessReportPipeline pipeline) {
        throw new IllegalStateException("Cannot select report data source when in state " + getType());
    }

    /**
     * Selects empty report as a source of the new report.
     * Available in {@link State.StateType#LOAD} state.
     * @param pipeline pipeline delegating the request
     */
    public void selectEmptyDatasource(final ProcessReportPipeline pipeline) {
        throw new IllegalStateException("Cannot select report data source when in state " + getType());
    }

    /**
     * Selects file as a source of the new report.
     * Available in {@link State.StateType#LOAD} state.
     * @param pipeline pipeline delegating the request
     */
    public void selectFileDatasource(final ProcessReportPipeline pipeline) {
        throw new IllegalStateException("Cannot select report data source when in state " + getType());
    }

    /**
     * Extracts text from bytes in fileType.
     * @param pipeline pipeline delegating the request
     * @param data file data
     * @param fileType file's type
     * @return
     */
    public String extractText(final ProcessReportPipeline pipeline,
            final byte[] data, final FileType fileType) {
        throw new IllegalStateException("Cannot select file as report data source when in state " + getType());
    }

    /**
     * Selects unfinished report as a source of the new report.
     * Available in {@link State.StateType#LOAD} state.
     * @param pipeline pipeline delegating the request
     * @param path path to file with saved report
     */
    public void selectLoadDatasource(final ProcessReportPipeline pipeline, final String path) {
        throw new IllegalStateException("Cannot select report data source when in state " + getType());
    }

    /**
     * Sets document to process.
     * @param pipeline pipeline delegating the request
     * @param document document to process
     * @throws DocumentChangedException if processed document has been changed
     * @throws DocumentAlreadyProcessedException if document has been already processed
     */
    public void setReport(final ProcessReportPipeline pipeline,
            final Document document)
            throws DocumentChangedException, DocumentAlreadyProcessedException {
        throw new IllegalStateException("Cannot set report when in state " + getType());
    }

    /**
     * Sets the report's text.
     * @param pipeline pipeline delegating the request
     * @param report new report's text
     * @throws DocumentChangedException if processed document has been changed
     * @throws DocumentAlreadyProcessedException if document has already been processed
     */
    public void setReportText(final ProcessReportPipeline pipeline, final String report)
            throws DocumentChangedException, DocumentAlreadyProcessedException {
        throw new IllegalStateException("Cannot set report's text when in state " + getType());
    }

    /**
     * Sets the report's words. Repopulates entities as well.
     * @param pipeline pipeline delegating the request
     * @param words new report's words
     * @throws DocumentChangedException if processed document has been changed
     * @throws DocumentAlreadyProcessedException if document has already been processed
     */
    public void setReportWords(final ProcessReportPipeline pipeline,
            final List<Word> words) throws DocumentChangedException,
            DocumentAlreadyProcessedException {
        throw new IllegalStateException("Cannot set report's words when in state " + getType());
    }

    /**
     * Sets the report's objects.
     * @param pipeline pipeline delegating the request
     * @param objects objects set as candidates
     */
    public void setReportObjects(final ProcessReportPipeline pipeline, final List<Entity> objects) {
        throw new IllegalStateException("Cannot set report's objects when in state " + getType());
    }

    /**
     * Sets the report's relations.
     * @param pipeline pipeline delegating the request
     * @param words words with assigned relations
     * @param unanchoredRelations list of unanchored relations
     * @throws DocumentChangedException if document has been changed under our hands
     * @throws DocumentAlreadyProcessedException if document has been processed under our hands
     */
    public void setReportRelations(final ProcessReportPipeline pipeline,
            final List<Word> words,
            final List<? extends RelationBuilder> unanchoredRelations)
            throws DocumentChangedException, DocumentAlreadyProcessedException {
        throw new IllegalStateException("Cannot set report's relations when in state " + getType());
    }

    /** Possible states. */
    public enum StateType {
        /** Selecting report source. Implemented by {@link LoadReportState}. */
        LOAD {
            @Override
            public boolean isLocking() {
                return false;
            }
        },
        /** Selecting file with report. */
        SELECT_FILE,
        /** Selecting document from db. */
        SELECT_DOCUMENT,
        /** Editing the report. Implemented by {@link ReportEditState}. */
        EDIT_REPORT,
        /** Editing the entities. Implemented by {@link ReportEntitiesState}. */
        EDIT_ENTITIES,
        /** Editing the objects. Implemented by {@link ReportObjectsState}. */
        EDIT_OBJECTS,
        /** Editing the relations. Implemented by {@link ReportRelationsState}. */
        EDIT_RELATIONS,
        /** Document saved. */
        DONE {
            @Override
            public boolean isLocking() {
                return false;
            }
        },
        /** Document error. */
        ERROR;

        /**
         * Returns if the state uses pipeline's lock.
         * @return true if the state uses pipeline's lock, false otherwise
         */
        public boolean isLocking() {
            return true;
        }
    }
}
