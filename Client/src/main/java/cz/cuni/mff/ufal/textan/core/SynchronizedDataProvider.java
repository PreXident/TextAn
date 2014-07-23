package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.dataprovider.*;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.ws.IDataProvider;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;

import javax.jws.WebParam;

/**
 * Simple wrapper around IDataProvider to provide synchronization.
 */
public class SynchronizedDataProvider implements IDataProvider {

    /** Wrapped IDataProvider. */
    protected final IDataProvider innerDP;

    /**
     * Only constructor.
     * @param dp DataProvider to wrap
     */
    public SynchronizedDataProvider(final IDataProvider dp) {
        innerDP = dp;
    }

    @Override
    synchronized public GetObjectsResponse getObjects(final Void getObjectsRequest) {
        return innerDP.getObjects(getObjectsRequest);
    }

    @Override
    synchronized public UpdateDocumentResponse updateDocument(
            final UpdateDocumentRequest updateDocumentRequest)
            throws IdNotFoundException {
        return innerDP.updateDocument(updateDocumentRequest);
    }

    @Override
    synchronized public GetRelationsResponse getRelations(final Void getRelationsRequest) {
        return innerDP.getRelations(getRelationsRequest);
    }

    @Override
    synchronized public GetObjectTypesResponse getObjectTypes(final Void getObjectTypesRequest) {
        return innerDP.getObjectTypes(getObjectTypesRequest);
    }

    @Override
    synchronized public GetDocumentByIdResponse getDocumentById(
            final GetDocumentByIdRequest getDocumentByIdRequest)
            throws IdNotFoundException {
        return innerDP.getDocumentById(getDocumentByIdRequest);
    }

    @Override
    public GetGraphByRelationIdResponse getGraphByRelationId(
            final GetGraphByRelationIdRequest getGraphByRelationIdRequest)
            throws IdNotFoundException {
        return innerDP.getGraphByRelationId(getGraphByRelationIdRequest);
    }

    @Override
    public GetFilteredRelationsResponse getFilteredRelations(
            final GetFilteredRelationsRequest getFilteredRelationsRequest)
            throws IdNotFoundException {
        return innerDP.getFilteredRelations(getFilteredRelationsRequest);
    }

    @Override
    synchronized public GetFilteredObjectsResponse getFilteredObjects(
            final GetFilteredObjectsRequest getFilteredObjectsRequest)
            throws IdNotFoundException {
        return innerDP.getFilteredObjects(getFilteredObjectsRequest);
    }

    @Override
    synchronized public GetRelationTypesResponse getRelationTypes(
            final Void getRelationTypesRequest) {
        return innerDP.getRelationTypes(getRelationTypesRequest);
    }

    @Override
    synchronized public GetGraphByObjectIdResponse getGraphByObjectId(
            final GetGraphByObjectIdRequest getGraphByIdRequest)
            throws IdNotFoundException {
        return innerDP.getGraphByObjectId(getGraphByIdRequest);
    }

    @Override
    synchronized public GetRelatedObjectsByIdResponse getRelatedObjectsById(
            final GetRelatedObjectsByIdRequest getRelatedObjectsByIdRequest)
            throws IdNotFoundException {
        return innerDP.getRelatedObjectsById(getRelatedObjectsByIdRequest);
    }

    @Override
    synchronized public AddDocumentResponse addDocument(
            final AddDocumentRequest addDocumentRequest) {
        return innerDP.addDocument(addDocumentRequest);
    }

    @Override
    synchronized public GetObjectsAndRelationsOccurringInDocumentResponse getObjectsAndRelationsOccurringInDocument(
            final GetObjectsAndRelationsOccurringInDocumentRequest getObjectsAndRelationsOccurringInDocumentRequest)
            throws IdNotFoundException {
        return innerDP.getObjectsAndRelationsOccurringInDocument(getObjectsAndRelationsOccurringInDocumentRequest);
    }

    @Override
    synchronized public GetDocumentsContainingObjectByIdResponse getDocumentsContainingObjectById(
            final GetDocumentsContainingObjectByIdRequest getDocumentsContainingObjectByIdRequest)
            throws IdNotFoundException {
        return innerDP.getDocumentsContainingObjectById(getDocumentsContainingObjectByIdRequest);
    }

    @Override
    synchronized public GetObjectsByIdsResponse getObjectsByIds(
            final GetObjectsByIdsRequest getObjectsByIdsRequest) throws IdNotFoundException {
        return innerDP.getObjectsByIds(getObjectsByIdsRequest);
    }

    @Override
    public GetDocumentsContainingRelationByIdResponse getDocumentsContainingRelationById(
            final GetDocumentsContainingRelationByIdRequest getDocumentsContainingRelationByIdRequest)
            throws IdNotFoundException {
        return innerDP.getDocumentsContainingRelationById(getDocumentsContainingRelationByIdRequest);
    }

    @Override
    synchronized public SplitObjectResponse splitObject(
            final SplitObjectRequest splitObjectRequest)
            throws IdNotFoundException {
        return innerDP.splitObject(splitObjectRequest);
    }

    @Override
    synchronized public GetRolesForRelationTypeByIdResponse getRolesForRelationTypeById(
            final GetRolesForRelationTypeByIdRequest getRolesForRelationTypeByIdRequest)
            throws IdNotFoundException {
        return innerDP.getRolesForRelationTypeById(getRolesForRelationTypeByIdRequest);
    }

    @Override
    synchronized public GetPathByIdResponse getPathById(
            final GetPathByIdRequest getPathByIdRequest) throws IdNotFoundException {
        return innerDP.getPathById(getPathByIdRequest);
    }

    @Override
    synchronized public GetDocumentsResponse getDocuments(final Void getDocumentsRequest) {
        return innerDP.getDocuments(getDocumentsRequest);
    }

    @Override
    synchronized public GetRelationsByTypeIdResponse getRelationsByTypeId(
            final GetRelationsByTypeIdRequest getRelationsByTypeIdRequest)
            throws IdNotFoundException {
        return innerDP.getRelationsByTypeId(getRelationsByTypeIdRequest);
    }

    @Override
    synchronized public GetFilteredDocumentsResponse getFilteredDocuments(
            final GetFilteredDocumentsRequest getFilteredDocumentsRequest) {
        return innerDP.getFilteredDocuments(getFilteredDocumentsRequest);
    }

    @Override
    synchronized public GetObjectsByTypeIdResponse getObjectsByTypeId(
            final GetObjectsByTypeIdRequest getObjectsByTypeIdRequest)
            throws IdNotFoundException {
        return innerDP.getObjectsByTypeId(getObjectsByTypeIdRequest);
    }

    @Override
    synchronized public GetObjectResponse getObject(
            final GetObjectRequest getObjectRequest) throws IdNotFoundException {
        return innerDP.getObject(getObjectRequest);
    }

    @Override
    synchronized public MergeObjectsResponse mergeObjects(
            final MergeObjectsRequest mergeObjectsRequest)
            throws IdNotFoundException {
        return innerDP.mergeObjects(mergeObjectsRequest);
    }
}
