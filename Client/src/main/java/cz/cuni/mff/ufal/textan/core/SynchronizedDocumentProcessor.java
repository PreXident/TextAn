package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.*;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;

/**
 * Simple wrapper around IDocumentProcessor to provide synchronization.
 */
public class SynchronizedDocumentProcessor implements IDocumentProcessor {

    /** Wrapped IDocumentProcessor. */
    protected final IDocumentProcessor innerDP;

    /**
     * Only constructor.
     * @param dp DocumentProcessor to wrap
     */
    public SynchronizedDocumentProcessor(final IDocumentProcessor dp) {
        innerDP = dp;
    }

    @Override
    synchronized public GetAssignmentsByIdResponse getAssignmentsById(
            final GetAssignmentsByIdRequest getAssignmentsByIdRequest,
            final EditingTicket editingTicket) throws IdNotFoundException {
        return innerDP.getAssignmentsById(getAssignmentsByIdRequest, editingTicket);
    }

    @Override
    synchronized public SaveProcessedDocumentByIdResponse saveProcessedDocumentById(
            final SaveProcessedDocumentByIdRequest saveProcessedDocumentByIdRequest,
            final EditingTicket editingTicket) throws IdNotFoundException {
        return innerDP.saveProcessedDocumentById(saveProcessedDocumentByIdRequest, editingTicket);
    }

    @Override
    public GetProblemsResponse getProblems(
            final GetProblemsRequest getProblemsRequest,
            final EditingTicket editingTicket) throws IdNotFoundException {
        return innerDP.getProblems(getProblemsRequest, editingTicket);
    }

//    @Override
//    synchronized public GetProblemsByIdResponse getProblemsById(
//            final GetProblemsByIdRequest getProblemsByIdRequest,
//            final EditingTicket editingTicket) throws IdNotFoundException {
//        return innerDP.getProblemsById(getProblemsByIdRequest, editingTicket);
//    }

    @Override
    synchronized public GetAssignmentsFromStringResponse getAssignmentsFromString(
            final GetAssignmentsFromStringRequest getAssignmentsFromStringRequest,
            final EditingTicket editingTicket) {
        return innerDP.getAssignmentsFromString(getAssignmentsFromStringRequest, editingTicket);
    }

    @Override
    synchronized public GetEntitiesByIdResponse getEntitiesById(
            final GetEntitiesByIdRequest getEntitiesByIdRequest,
            final EditingTicket editingTicket) throws IdNotFoundException {
        return innerDP.getEntitiesById(getEntitiesByIdRequest, editingTicket);
    }

    @Override
    synchronized public GetEntitiesFromStringResponse getEntitiesFromString(
            final GetEntitiesFromStringRequest getEntitiesFromStringRequest,
            final EditingTicket editingTicket) {
        return innerDP.getEntitiesFromString(getEntitiesFromStringRequest, editingTicket);
    }

    @Override
    synchronized public SaveProcessedDocumentFromStringResponse saveProcessedDocumentFromString(
            final SaveProcessedDocumentFromStringRequest saveProcessedDocumentFromStringRequest,
            final EditingTicket editingTicket) throws IdNotFoundException {
        return innerDP.saveProcessedDocumentFromString(saveProcessedDocumentFromStringRequest, editingTicket);
    }

//    @Override
//    synchronized public GetProblemsFromStringResponse getProblemsFromString(
//            final GetProblemsFromStringRequest getProblemsFromStringRequest,
//            final EditingTicket editingTicket) {
//        return innerDP.getProblemsFromString(getProblemsFromStringRequest, editingTicket);
//    }

    @Override
    synchronized public GetEditingTicketResponse getEditingTicket(
            final GetEditingTicketRequest getEditingTicketRequest) {
        return innerDP.getEditingTicket(getEditingTicketRequest);
    }
}
