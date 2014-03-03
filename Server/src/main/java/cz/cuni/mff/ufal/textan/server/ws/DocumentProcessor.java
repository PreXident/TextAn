package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.Entity;
import cz.cuni.mff.ufal.textan.commons.models.Object;
import cz.cuni.mff.ufal.textan.commons.models.Relation;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import javax.jws.WebService;

/**
 * For now only mocking document processing.
 */
@WebService(endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor", serviceName = "DocumentProcessor")
public class DocumentProcessor implements IDocumentProcessor {

    @Override
    public Ticket getTicket(String username) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int addDocument(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateDocument(String text, int documentId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Entity[] getEntities(String text) {
        return new Entity[] { new Entity(text, 0, text.length(), 0) };
    }

    @Override
    public Entity[] getEntitiesById(int documentId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] getObjects(String text, Entity[] entities) {
        return new Object[entities.length];
    }

    @Override
    public Entity[] getObjectsById(int documentId, Entity[] entities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void saveProcessedDocument(int documentId, Object[] objects, Relation[] relations, Ticket ticket, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getProblems(int documentId, Object[] objects, Relation[] relations, Ticket ticket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
