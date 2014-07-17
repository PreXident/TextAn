package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.DocumentData;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import java.util.function.Predicate;

/**
 * Provides information about graphs for documents.
 */
public class DocumentGrapher implements IGrapher {

    /** Client for communication with the server. */
    final protected Client client;

    /** Displayed document. */
    final protected Document document;

    /** Graph distance. It has no meaning to displaying the graph. */
    protected int distance;

    /** Information about displayed document. */
    protected DocumentData documentData;

    /** Graph built from document data. */
    protected Graph graph;

    /**
     * Only constructor.
     * @param client client for communition with the server
     * @param document document to display
     */
    public DocumentGrapher(final Client client, final Document document) {
        this.client = client;
        this.document = document;
    }

    @Override
    public Predicate<Object> getCenterer() {
        return (obj) -> false;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public Graph getGraph() throws IdNotFoundException {
        if (documentData == null || graph == null) {
            documentData = client.getDocumentData(document.getId());
            graph = new Graph(documentData.getObjects(),
                    documentData.getRelations().values());
        }
        return graph;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public long getRootId() {
        return -1;
    }

    @Override
    public void setRootId(long id) {
        //nothing
    }

    @Override
    public String getTitle() {
        return String.valueOf(document.getId()) + ": " + document.getText();
    }
}
