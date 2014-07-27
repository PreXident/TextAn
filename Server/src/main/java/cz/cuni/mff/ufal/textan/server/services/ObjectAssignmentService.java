package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.server.models.Assignment;
import cz.cuni.mff.ufal.textan.server.models.EditingTicket;
import cz.cuni.mff.ufal.textan.server.models.Entity;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.textpro.ITextPro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final ITextPro textPro;

    @Autowired
    public ObjectAssignmentService(IObjectTableDAO objectTableDAO, IDocumentTableDAO documentTableDAO, ITextPro textPro) {
        this.objectTableDAO = objectTableDAO;
        this.documentTableDAO = documentTableDAO;
        this.textPro = textPro;
    }

    public List<Assignment> getAssignments(String text, List<Entity> entities, EditingTicket ticket) {
        return getAssignmentsInner(text, entities);
    }

    public List<Assignment> getAssignments(long documentId, List<Entity> entities, EditingTicket ticket)
            throws IdNotFoundException, DocumentAlreadyProcessedException, DocumentChangedException {
        //TODO:throw Document Changed Exception

        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        } else if (documentTable.isProcessed()) {
            throw new DocumentAlreadyProcessedException(documentId, documentTable.getProcessedDate());
        }

        return getAssignmentsInner(documentTable.getText(), entities);
    }

    private List<Assignment> getAssignmentsInner(String text, List<Entity> entities) {

        Map<cz.cuni.mff.ufal.textan.textpro.data.Entity, List<Pair<Long, Double>>> textProAssignments =
                textPro.DoubleRanking(
                        text,
                        entities.stream().map(Entity::toTextProEntity).collect(Collectors.toList()),
                        50
                );

        List<Assignment> assignments = new ArrayList<>();
        for (Entity entity : entities) {
            List<Pair<Long, Double>> objectScorePairs = textProAssignments.get(entity.toTextProEntity());
            List<Pair<Object, Float>> ratedObjects = objectScorePairs.stream()
                    .map(x -> new Pair<>(Object.fromObjectTable(objectTableDAO.find(x.getFirst())), x.getSecond().floatValue()))
                    .collect(Collectors.toList());
            assignments.add(new Assignment(entity, ratedObjects));
        }

        return assignments;
    }
}
