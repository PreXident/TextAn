package cz.cuni.mff.ufal.textan.commons.ws;

import cz.cuni.mff.ufal.textan.commons.models.Entity;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * The interface for Web Service, which provides processing of documents by the system.
 * Created by Petr Fanta on 7.1.14.
 */

@WebService(name = "DocumentProcessor")
public interface IDocumentProcessor {

    //TODO: Add user identification to methods
    //TODO: Add some special document identification

    /**
     * Adds a new document into the system.
     * @param text text of the new document
     * @return id of the added document
     */
    @WebMethod
    public int addDocument(String text);

    /**
     * Updates the document with given id saved in the system.
     * @param text updated text
     * @param documentId id of the original document
     */
    //TODO: Add exception if document is already processed
    @WebMethod
    public void updateDocument(String text, int documentId);

    /**
     * Find entities in a document.
     * @param text text of the document
     * @return array of entities ({@link cz.cuni.mff.ufal.textan.commons.models.Entity}) found in the document
     */
    @WebMethod
    public Entity[] getEntities(String text);

    /**
     * Find entities in a document.
     * @param documentId id of document saved in the system
     * @return array of entities ({@link cz.cuni.mff.ufal.textan.commons.models.Entity}) found in the document
     */
    @WebMethod
    public Entity[] getEntities(int documentId);

    //TODO: method which returns posible objects for one entity? many entities?

    /**
     * Saves processed document, object and relations in the system and marks the document as processed.
     */
    //TODO: Add parameters
    @WebMethod
    public void saveProcessedDocument();

//    @WebMethod
//    public void
}
