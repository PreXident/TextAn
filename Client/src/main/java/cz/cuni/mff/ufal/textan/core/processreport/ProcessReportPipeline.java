package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.Ticket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents pipeline handling processing documents.
 */
public class ProcessReportPipeline {

    /** Separators delimiting words. */
    public static final Set<Character> separators = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('\n', '\t', '\r', ' ', ',', '.', ';', '!')));

    /** Parent Client of the pipeline. */
    protected final Client client;

    /** Report text. TOODO change test content to empty string */
    protected String reportText = "Ahoj, toto je testovaci zprava urcena pro vyzkouseni vsech moznosti oznacovani textu.";

    /** Report entities. */
    protected List<Word> reportWords = new ArrayList<>();

    /** Report entities. */
    protected List<Entity> reportEntities = new ArrayList<>();

    /** Report objects. */
    protected Set<Object> reportObjects = new HashSet<>();

    /** Report relations. */
    protected Set<Relation> reportRelations = new HashSet<>();

    /** State of the pipeline. */
    protected State state = LoadReportState.getInstance();

    /** List of listeners registered for state changing. */
    protected final List<IStateChangedListener> stateChangedListeners = new ArrayList<>();

    /** Ticket for document processing. */
    protected final Ticket ticket;

    /**
     * Only constructor. Do not use directly!
     * TODO think of a design preventing users from calling this constructor directly
     * @param client parent Client of the pipeline
     */
    public ProcessReportPipeline(final Client client) {
        this.client = client;
        final String username = client.getSettings().getProperty("username");
        ticket = client.getTicket(username);
    }

    /**
     * Sets the state of the pipeline.
     * Intended to be used by the {@link State} to proceed to another state.
     * Notifies the {@link #stateChangedListeners}.
     * @param newState new state of the pipeline
     */
    protected void setState(final State newState) {
        state = newState;
        for (IStateChangedListener listener : stateChangedListeners) {
            listener.stateChanged(newState);
        }
    }

    /**
     * Returns report's text.
     * @return report's text
     */
    public String getReportText() {
        return reportText;
    }

    /**
     * Registers the listener to changing state event.
     * The listener is immediately notified about the current state.
     * @param listener listener to be registered
     */
    public void addStateChangedListener(final IStateChangedListener listener) {
        stateChangedListeners.add(listener);
        listener.stateChanged(state);
    }

    /**
     * Unregisters the listener to changing state event.
     * @param listener listener to be unregistered
     * @return true if the listener was registered
     */
    public boolean removeStateChangedListener(final IStateChangedListener listener) {
        return stateChangedListeners.remove(listener);
    }

    /**
     * Selects database as a source of the new report.
     * Available in {@link State.StateType#LOAD} state. Proceeds to next State.
     * @see State#selectDatabaseDatasource(cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline)
     */
    public void selectDatabaseDatasource() {
        state.selectDatabaseDatasource(this);
    }

    /**
     * Selects empty report as a source of the new report.
     * Available in {@link State.StateType#LOAD} state. Proceeds to next State.
     * @see State#selectEmptyDatasource(cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline)
     */
    public void selectEmptyDatasource() {
        state.selectEmptyDatasource(this);
    }

    /**
     * Selects empty report as a source of the new report.
     * Available in {@link State.StateType#LOAD} state. Proceeds to next State.
     * @see State#selectFileDatasource(cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline)
     */
    public void selectFileDatasource() {
        state.selectFileDatasource(this);
    }

    /**
     * Selects unfinished report as a source of the new report.
     * Available in {@link State.StateType#LOAD} state. Proceeds to next State.
     * @see State#selectLoadDatasource(cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline)
     */
    public void selectLoadDatasource() {
        state.selectLoadDatasource(this);
    }

    /**
     * Sets report's text.
     * Available in {@link State.StateType#EDIT_REPORT} state.
     * Proceeds to next State.
     * @param reportText new report text
     * @see State#setReport(cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline, java.lang.String)
     */
    public void setReportText(final String reportText) {
        state.setReport(this, reportText);
    }

    /**
     * Returns parent client.
     * @return parent client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Returns report words.
     * @return report words
     */
    public List<Word> getReportWords() {
        return reportWords;
    }

    public void setReportWords(final List<Word> words) {
        state.setReportWords(this, words);
    }

    /**
     * Sets report's entities.
     * @param entities new entities
     */
    public void setReportEntities(final List<Entity> entities) {
        state.setReportEntities(this, entities);
    }

    public List<Entity> getReportEntities() {
        return reportEntities;
    }

    /**
     * Returns report's objects.
     * @return report's objects
     */
    public Set<Object> getReportObjects() {
        return reportObjects;
    }

    /**
     * Sets report's objects.
     * @param objects new objects
     */
    public void setReportObjects(final Set<Object> objects) {
        state.setReportObjects(this, objects);
    }

    /**
     * Returns report's relations.
     * @return report's relations
     */
    public Set<Relation> getReportRelations() {
        return reportRelations;
    }

    /**
     * Sets report's relations.
     * @param relations new relations
     */
    public void setReportRelations(final Set<Relation> relations) {
        state.setReportRelations(this, relations);
    }
}
