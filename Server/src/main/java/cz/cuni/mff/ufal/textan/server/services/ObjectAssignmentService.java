package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.server.models.Assignment;
import cz.cuni.mff.ufal.textan.server.models.EditingTicket;
import cz.cuni.mff.ufal.textan.server.models.Entity;
import cz.cuni.mff.ufal.textan.server.models.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * A service which provides assignment of entities to objects.
 *
 * @author Petr Fanta
 */
@Service
@Transactional
public class ObjectAssignmentService {

    private final IObjectTableDAO objectTableDAO;
    private final IDocumentTableDAO documentTableDAO;

    @Autowired
    public ObjectAssignmentService(IObjectTableDAO objectTableDAO, IDocumentTableDAO documentTableDAO) {
        this.objectTableDAO = objectTableDAO;
        this.documentTableDAO = documentTableDAO;
    }

    public List<Assignment> getAssignments(String text, List<Entity> entities, EditingTicket ticket) {
        //TODO: implement

        List<Assignment> assignments = new ArrayList<>();

        for (Entity entity : entities) {

            List<ObjectTable> objectTables = objectTableDAO.findAllByObjectType(entity.getType());
            List<Pair<Object, Float>> ratedObjects = new ArrayList<>();

            for (ObjectTable table : objectTables) {
                ratedObjects.add(new Pair<Object, Float>(Object.fromObjectTable(table), 0.0f));
            }

            assignments.add(new Assignment(entity, ratedObjects));
        }

        return assignments;
    }

    public List<Assignment> getAssignments(long documentId, List<Entity> entities, EditingTicket ticket) throws IdNotFoundException {

        //TODO: implement
        return getAssignments("", entities, ticket);
    }
}
