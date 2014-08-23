package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Document;
import cz.cuni.mff.ufal.textan.core.DocumentData;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Provides information about graphs for documents.
 */
public class DocumentGrapher extends AbstractGrapher {

    /** Displayed document. */
    final protected Document document;

    /** Information about displayed document. */
    protected DocumentData documentData;

    /**
     * Only constructor.
     * @param client client for communition with the server
     * @param document document to display
     */
    public DocumentGrapher(final Client client, final Document document) {
        super(client);
        this.document = document;
        setDistance(0);
    }

    @Override
    public Predicate<java.lang.Object> getCenterer() {
        return (obj) -> false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public String getTitle() {
        return String.valueOf(document.getId()) + ": " + document.getText();
    }

    @Override
    public void clearCache() {
        super.clearCache();
        documentData = null;
    }

    @Override
    protected Graph fetchGraph() throws IdNotFoundException {
        if (documentData == null || graph == null) {
            documentData = client.getDocumentData(document.getId());
            return new Graph(documentData.getObjects(),
                    documentData.getRelations().values());
        }
        return graph;
    }

    @Override
    protected Graph filterGraph() {
        final List<Object> roots = graph.getNodes().values().stream()
                .filter(obj -> !ignoredObjectTypes.contains(obj.getType()))
                .collect(Collectors.toList());
        return filterGraph(roots);
    }
}
