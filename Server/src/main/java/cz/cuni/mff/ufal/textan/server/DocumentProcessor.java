package cz.cuni.mff.ufal.textan.server;

import cz.cuni.mff.ufal.textan.commons.models.Entity;
import cz.cuni.mff.ufal.textan.commons.models.Object;
import cz.cuni.mff.ufal.textan.commons.models.ObjectType;
import cz.cuni.mff.ufal.textan.commons.models.Rating;
import cz.cuni.mff.ufal.textan.commons.models.Relation;
import cz.cuni.mff.ufal.textan.commons.models.Ticket;
import cz.cuni.mff.ufal.textan.commons.ws.IDocumentProcessor;
import java.util.ArrayList;
import java.util.Arrays;
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
    public Rating[] getObjects(String text, Entity[] entities) {
        final Rating[] array = new Rating[entities.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = new Rating();
            array[i].candidate = new Object[] { new Object(0, new ObjectType(0, "Person"), new ArrayList<>(Arrays.asList("Pepa"))) };
            array[i].rating = new double[] { 1D };
        }
        return array;
    }

    @Override
    public Rating[] getObjectsById(int documentId, Entity[] entities) {
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
