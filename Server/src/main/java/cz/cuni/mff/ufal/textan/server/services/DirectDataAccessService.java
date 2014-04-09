package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A service which provides direct access into the database.
 *
 * @author Petr Fanta
 */
@Service
public class DirectDataAccessService {

    private final IDocumentTableDAO documentTableDAO;

    private final IObjectTypeTableDAO objectTypeTableDAO;
    private final IObjectTableDAO objectTableDAO;

    private final IRelationTypeTableDAO relationTypeTableDAO;
    private final IRelationTableDAO relationTableDAO;

    @Autowired
    public DirectDataAccessService(
            IDocumentTableDAO documentTableDAO,
            IObjectTypeTableDAO objectTypeTableDAO,
            IObjectTableDAO objectTableDAO,
            IRelationTypeTableDAO relationTypeTableDAO,
            IRelationTableDAO relationTableDAO) {

        this.documentTableDAO = documentTableDAO;
        this.objectTypeTableDAO = objectTypeTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.relationTypeTableDAO = relationTypeTableDAO;
        this.relationTableDAO = relationTableDAO;
    }

    public long addDocument(String text, Ticket ticket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Document getDocument(long documentId, Ticket ticket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Document> getDocuments(Ticket serverTicket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean updateDocument(long documentId, String text, Ticket ticket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**/
    public List<ObjectType> getObjectTypes(Ticket ticket) {

        return objectTypeTableDAO.findAll().stream()
                .map(ObjectType::fromObjectTypeTable)
                .collect(Collectors.toList());
    }

    public List<Object> getObjects(Ticket ticket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Object getObject(long objectId, Ticket serverTicket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Object> getObjects(long objectTypeId, Ticket serverTicket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public long mergeObjects(long object1Id, long object2Id, Ticket serverTicket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean splitObject(long objectId, Ticket serverTicket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }


    public List<RelationType> getRelationTypes(Ticket ticket) {

        return relationTypeTableDAO.findAll().stream()
                .map(RelationType::fromRelationTypeTable)
                .collect(Collectors.toList());

    }

    public List<Relation> getRelations(Ticket serverTicket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Relation> getRelations(long relationTypeId, Ticket serverTicket) {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
