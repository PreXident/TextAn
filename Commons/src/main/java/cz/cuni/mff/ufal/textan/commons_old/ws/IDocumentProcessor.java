package cz.cuni.mff.ufal.textan.commons_old.ws;

import cz.cuni.mff.ufal.textan.commons_old.models.*;
import cz.cuni.mff.ufal.textan.commons_old.models.Object;

import javax.jws.WebMethod;
import javax.jws.WebParam;
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
     * Gets ticket with information for editing.
     *
     * @param username the username
     * @return the ticket
     */
    @WebMethod
    public Ticket getTicket(@WebParam(name = "username") String username);

    /**
     * Adds a new document into the system.
     *
     * @param text text of the new document
     * @return id of the added document
     */
    @WebMethod
    public int addDocument(@WebParam(name = "text") String text);

    /**
     * Updates the document with given id saved in the system.
     *
     * @param text updated text
     * @param documentId id of the original document
     */
    //TODO: Add exception if document is already processed
    @WebMethod
    public void updateDocument(@WebParam(name = "text") String text, @WebParam(name = "documentId") int documentId);

    /**
     * Find entities in a document.
     * @param text text of the document
     * @return array of entities ({@link cz.cuni.mff.ufal.textan.commons_old.models.Entity}) found in the document
     */
    @WebMethod(operationName = "getEntitiesFromString")
    public Entity[] getEntities(@WebParam(name = "text") String text);

    /**
     * Find entities in a document.
     * @param documentId id of document saved in the system
     * @return array of entities ({@link cz.cuni.mff.ufal.textan.commons_old.models.Entity}) found in the document
     */
    @WebMethod
    public Entity[] getEntitiesById(@WebParam(name = "documentId") int documentId);

    //TODO: method which returns possible objects for one entity? many entities?

    /**
     * Assigns objects to entities in a document. The best match is used.
     * @param text text of the document
     * @param entities entities in the document
     * @return array of objects ({@link cz.cuni.mff.ufal.textan.commons_old.models.Object}) assigned to entities
     */
    @WebMethod(operationName = "getObjectsFromString")
    public Rating[] getObjects(@WebParam(name = "text") String text, @WebParam(name = "entities") Entity[] entities);

    /*
     * Assigns objects to entities in a document. The best match is used.
     * @param documentId id of document saved in the system
     * @param entities entities in the document
     * @return array of objects ({@link cz.cuni.mff.ufal.textan.commons_old.models.Object}) assigned to entities
     */
    @WebMethod
    public Rating[] getObjectsById(@WebParam(name = "documentId") int documentId, @WebParam(name = "entities") Entity[] entities);

    /**
     * Saves processed document, object and relations in the system and marks the document as processed.
     *
     * @param documentId the processed document id
     * @param objects objects in the document
     * @param relations relations between objects in the document
     * @param ticket the ticket
     * @param force saving is performed in every case if force is true
     */
    @WebMethod
    public void saveProcessedDocument(
            @WebParam(name = "documentId") int documentId,
            @WebParam(name = "objects") Object[] objects,
            @WebParam(name = "relations") Relation[] relations,
            @WebParam(name = "ticket") Ticket ticket,
            @WebParam(name = "force") boolean force
    );

    /**
     * Returns problems in saving process.
     *
     * @param documentId the processed document id
     * @param objects objects in the document
     * @param relations relations between objects in the document
     * @param ticket the ticket
     */
    @WebMethod
    public void getProblems(  //TODO: add return type
            @WebParam(name = "documentId") int documentId,
            @WebParam(name = "objects") Object[] objects,
            @WebParam(name = "relations") Relation[] relations,
            @WebParam(name = "ticket") Ticket ticket
    );

}
