package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.server.models.Assignment;
import cz.cuni.mff.ufal.textan.server.models.EditingTicket;
import cz.cuni.mff.ufal.textan.server.models.Entity;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.assigner.IObjectAssigner;
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
    private final IAliasTableDAO aliasTableDAO;
    private final IDocumentTableDAO documentTableDAO;
    private final IObjectAssigner objectAssigner;

    @Autowired
    public ObjectAssignmentService(IObjectTableDAO objectTableDAO, IAliasTableDAO aliasTableDAO, IDocumentTableDAO documentTableDAO, IObjectAssigner objectAssigner) {
        this.objectTableDAO = objectTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.documentTableDAO = documentTableDAO;
        this.objectAssigner = objectAssigner;
    }

    public List<Assignment> getAssignments(String text, List<Entity> entities, EditingTicket ticket) {
        return getAssignmentsInner(text, entities);
    }

    public List<Assignment> getAssignments(long documentId, List<Entity> entities, EditingTicket ticket)
            throws IdNotFoundException, DocumentAlreadyProcessedException, DocumentChangedException {

        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        } else if (documentTable.isProcessed()) {
            throw new DocumentAlreadyProcessedException(documentId, documentTable.getProcessedDate());
        } if (documentTable.getGlobalVersion() > ticket.getVersion()) {
            throw new DocumentChangedException(documentId, documentTable.getGlobalVersion(), ticket.getVersion());
        }

        return getAssignmentsInner(documentTable.getText(), entities);
    }

    private List<Assignment> getAssignmentsInner(String text, List<Entity> entities) {

        Map<cz.cuni.mff.ufal.textan.assigner.data.Entity, List<Pair<Long, Double>>> textProAssignments =
                objectAssigner.combinedObjectRanking(
                        text,
                        entities.stream().map(Entity::toObjectAssignerEntity).collect(Collectors.toList()),
                        50
                );

        List<Assignment> assignments = new ArrayList<>();
        for (Entity entity : entities) {
            List<Pair<Long, Double>> objectScorePairs = textProAssignments.get(entity.toObjectAssignerEntity());
            List<Pair<Object, Float>> ratedObjects = objectScorePairs.stream()
                    .map(x -> new Pair<>(Object.fromObjectTable(objectTableDAO.find(x.getFirst()), aliasTableDAO.findAllAliasesOfObject(x.getFirst())), x.getSecond().floatValue()))
                    .collect(Collectors.toList());
            assignments.add(new Assignment(entity, ratedObjects));
        }

        return assignments;
    }
}
