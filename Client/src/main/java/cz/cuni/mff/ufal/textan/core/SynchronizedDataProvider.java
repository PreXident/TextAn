package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.dataprovider.AddDocumentRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.AddDocumentResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetDocumentByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetDocumentByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetDocumentsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetGraphByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetGraphByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectTypesResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsByTypeIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsByTypeIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetPathByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetPathByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRelatedObjectsByIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRelatedObjectsByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRelationTypesResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRelationsByTypeIdRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRelationsByTypeIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetRelationsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.MergeObjectsRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.MergeObjectsResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.SplitObjectRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.SplitObjectResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.UpdateDocumentRequest;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.UpdateDocumentResponse;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.Void;
import cz.cuni.mff.ufal.textan.commons.ws.IDataProvider;
import cz.cuni.mff.ufal.textan.commons.ws.IdNotFoundException;

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
    synchronized public GetRelationTypesResponse getRelationTypes(
            final Void getRelationTypesRequest) {
        return innerDP.getRelationTypes(getRelationTypesRequest);
    }

    @Override
    synchronized public GetGraphByIdResponse getGraphById(
            final GetGraphByIdRequest getGraphByIdRequest)
            throws IdNotFoundException {
        return innerDP.getGraphById(getGraphByIdRequest);
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
    synchronized public SplitObjectResponse splitObject(
            final SplitObjectRequest splitObjectRequest)
            throws IdNotFoundException {
        return innerDP.splitObject(splitObjectRequest);
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
