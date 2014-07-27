package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.server.models.EditingTicket;
import cz.cuni.mff.ufal.textan.server.models.Entity;
import cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service which provides a named entity recognition.
 * @author Petr Fanta
 */
@Service
public class NamedEntityRecognizerService {
    private final IDocumentTableDAO documentTableDAO;
    private final NamedEntityRecognizer namedEntityRecognizer;

    /**
     * Instantiates a new Named entity recognizer service.
     *
     * @param documentTableDAO the document table dAO
     */
    @Autowired
    public NamedEntityRecognizerService(IDocumentTableDAO documentTableDAO, NamedEntityRecognizer namedEntityRecognizer) {
        this.documentTableDAO = documentTableDAO;
        this.namedEntityRecognizer = namedEntityRecognizer;
    }

    /**
     * Gets entities from a plain text.
     *
     * @param text          the text
     * @param editingTicket the editing ticket
     * @return the list of entities found in the text
     */
    public List<Entity> getEntities(String text, EditingTicket editingTicket) {
        return namedEntityRecognizer.tagText(text);
    }

    /**
     * Gets entities from a document with the given identifier .
     *
     * @param documentId    the identifier of a document
     * @param editingTicket the editing ticket
     * @return the list of entities found in the document
     * @throws IdNotFoundException the exception thrown if a document with the given id wasn't found
     */
    public List<Entity> getEntities(long documentId, EditingTicket editingTicket)
            throws IdNotFoundException, DocumentAlreadyProcessedException, DocumentChangedException {
        //TODO:throw Document Changed Exception

        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        } else if (documentTable.isProcessed()) {
            throw new DocumentAlreadyProcessedException(documentId, documentTable.getProcessedDate());
        }

        return namedEntityRecognizer.tagText(documentTable.getText());
    }
}
