package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.*;
import cz.cuni.mff.ufal.textan.server.commands.CommandInvoker;
import cz.cuni.mff.ufal.textan.server.commands.NamedEntityRecognizerLearnCommand;
import cz.cuni.mff.ufal.textan.server.commands.TextProLearnCommand;
import cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.textpro.ITextPro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * A service which provides a saving of a processed document.
 *
 * @author Petr Fanta
 */
@Service
@Transactional
public class SaveService {

    private final IDocumentTableDAO documentTableDAO;
    private final IObjectTypeTableDAO objectTypeTableDAO;
    private final IObjectTableDAO objectTableDAO;
    private final IAliasTableDAO aliasTableDAO;
    private final IAliasOccurrenceTableDAO aliasOccurrenceTableDAO;
    private final IRelationTypeTableDAO relationTypeTableDAO;
    private final IRelationTableDAO relationTableDAO;
    private final IRelationOccurrenceTableDAO relationOccurrenceTableDAO;

    private final IInRelationTableDAO inRelationTableDAO;
    private final IJoinedObjectsTableDAO joinedObjectsTableDAO;

    private final CommandInvoker invoker;
    private final NamedEntityRecognizer recognizer;
    private final ITextPro textPro;

    private final Lock writeLock;

    /**
     * Instantiates a new Save service.
     * @param documentTableDAO the document table dAO
     * @param objectTypeTableDAO the object type table dAO
     * @param objectTableDAO the object table dAO
     * @param aliasTableDAO the alias table dAO
     * @param aliasOccurrenceTableDAO the alias occurrence table dAO
     * @param relationTypeTableDAO the relation type table dAO
     * @param relationTableDAO the relation table dAO
     * @param relationOccurrenceTableDAO the relation occurrence table dAO
     * @param inRelationTableDAO the in relation table dAO
     * @param joinedObjectsTableDAO
     * @param invoker
     * @param recognizer
     * @param textPro
     * @param writeLock
     */
    @Autowired
    public SaveService(
            IDocumentTableDAO documentTableDAO,
            IObjectTypeTableDAO objectTypeTableDAO, IObjectTableDAO objectTableDAO,
            IAliasTableDAO aliasTableDAO,
            IAliasOccurrenceTableDAO aliasOccurrenceTableDAO,
            IRelationTypeTableDAO relationTypeTableDAO, IRelationTableDAO relationTableDAO,
            IRelationOccurrenceTableDAO relationOccurrenceTableDAO, IInRelationTableDAO inRelationTableDAO,
            IJoinedObjectsTableDAO joinedObjectsTableDAO, CommandInvoker invoker, NamedEntityRecognizer recognizer, ITextPro textPro, @Qualifier("writeLock") Lock writeLock) {

        this.documentTableDAO = documentTableDAO;
        this.objectTypeTableDAO = objectTypeTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.aliasOccurrenceTableDAO = aliasOccurrenceTableDAO;
        this.relationTypeTableDAO = relationTypeTableDAO;
        this.relationTableDAO = relationTableDAO;
        this.relationOccurrenceTableDAO = relationOccurrenceTableDAO;
        this.inRelationTableDAO = inRelationTableDAO;
        this.joinedObjectsTableDAO = joinedObjectsTableDAO;
        this.invoker = invoker;
        this.recognizer = recognizer;
        this.textPro = textPro;
        this.writeLock = writeLock;
    }

    /**
     * Saves a processed document in the database.
     * Creates a new document in the database and adds records related to the document.
     *
     * @param text the text of document
     * @param objects the list of objects which are modified by the save (e.g. new objects)
     * @param objectOccurrences the list of occurrences of objects in the document.
     *                          Objects are searched (in sequence) in: {@code objects} parameter, the database.
     * @param relations the list of relations which are modified by the save (e.g. new relations, new objects in relation)
     * @param relationOccurrences the list occurrences of relations in the document.
     *                            Relations are searched (in sequence) in: {@code relations}, the database.
     *                            Relations without anchor in the text of the document should be in the list,
     *                            but with {@link cz.cuni.mff.ufal.textan.server.models.Occurrence} set to null.
     * @param force if true changes in the database made ​​during editing the document will not be considered an error TODO: better explanation
     * @param ticket the ticket
     * @return true if the processed document was successfully saved, false otherwise
     * @throws IdNotFoundException the id not found exception TODO
     */
    public boolean save(
            String text,
            List<Object> objects, List<Pair<Long, Occurrence>> objectOccurrences,
            List<Relation> relations, List<Pair<Long, Occurrence>> relationOccurrences,
            boolean force, EditingTicket ticket) throws IdNotFoundException {

        writeLock.lock();
        try {
            if (!force && checkChanges(ticket)) {
                return false;
            }

            final DocumentTable documentTable = new DocumentTable(text);
            documentTableDAO.add(documentTable);

            return innerSave(documentTable, objects, objectOccurrences, relations, relationOccurrences);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Saves a processed document in the database.
     * Finds a document with given identifier in the database and adds records related to the document.
     *
     * @param documentId the identifier of a document in the database
     * @param objects the list of objects which are modified by the save (e.g. new objects)
     * @param objectOccurrences the list of occurrences of objects in the document.
     *                          Objects are searched (in sequence) in: {@code objects} parameter, the database.
     * @param relations the list of relations which are modified by the save (e.g. new relations, new objects in relation)
     * @param relationOccurrences the list occurrences of relations in the document.
     *                            Relations are searched (in sequence) in: {@code relations}, the database.
     *                            Relations without anchor in the text of the document should be in the list,
     *                            but with {@link cz.cuni.mff.ufal.textan.server.models.Occurrence} set to null.
     * @param force if true changes in the database made ​​during editing the document will not be considered an error TODO: better explanation
     * @param ticket the ticket
     * @return true if the processed document was successfully saved, false otherwise
     * @throws IdNotFoundException the id not found exception TODO
     */
    public boolean save(
            long documentId,
            List<Object> objects, List<Pair<Long, Occurrence>> objectOccurrences,
            List<Relation> relations, List<Pair<Long, Occurrence>> relationOccurrences,
            boolean force, EditingTicket ticket)
            throws IdNotFoundException, DocumentAlreadyProcessedException, DocumentChangedException {

        writeLock.lock();
        try {
            DocumentTable documentTable = documentTableDAO.find(documentId);
            if (documentTable == null) {
                throw new IdNotFoundException("documentId", documentId);
            } else if (documentTable.isProcessed()) {
                throw new DocumentAlreadyProcessedException(documentId, documentTable.getProcessedDate());
            }
            if (documentTable.getGlobalVersion() > ticket.getVersion()) {
                throw new DocumentChangedException(documentId, documentTable.getGlobalVersion(), ticket.getVersion());
            }

            if (!force && checkChanges(ticket)) {
                return false;
            }

            return innerSave(documentTable, objects, objectOccurrences, relations, relationOccurrences);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean save(
            long documentId, String text,
            List<Object> objects, List<Pair<Long, Occurrence>> objectOccurrences,
            List<Relation> relations, List<Pair<Long, Occurrence>> relationOccurrences,
            boolean force, EditingTicket ticket) throws IdNotFoundException, DocumentAlreadyProcessedException {

        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        } else if (documentTable.isProcessed()) {
            throw new DocumentAlreadyProcessedException(documentId, documentTable.getProcessedDate());
        }

        if (!force && checkChanges(ticket)) {
            return false;
        }

        documentTable.setText(text);

        return innerSave(documentTable, objects, objectOccurrences, relations, relationOccurrences);
    }

    private boolean checkChanges(EditingTicket ticket) {
        long nextVersion = ticket.getVersion() + 1;
        return ((objectTableDAO.findAllSinceGlobalVersion(nextVersion).size() > 0 )||
                (relationTableDAO.findAllSinceGlobalVersion(nextVersion).size() > 0) ||
                (joinedObjectsTableDAO.findAllSinceGlobalVersion(nextVersion).size() > 0));
    }

    private boolean innerSave(
            DocumentTable documentTable,
            List<Object> objects, List<Pair<Long, Occurrence>> objectOccurrences,
            List<Relation> relations, List<Pair<Long, Occurrence>> relationOccurrences) throws IdNotFoundException {

        documentTable.setProcessedDateToNow();

        //create hashmaps for objects, objects id mappings
        HashMap<Long, Object> objectHashMap = new HashMap<>();
        for (Object object : objects) {
            objectHashMap.put(object.getId(), object);
        }

        HashMap<Long, ObjectTable> objectIdMapping = new HashMap<>();

        //add objects
        for (Pair<Long, Occurrence> objectOccurrence : objectOccurrences) {

            ObjectTable objectTable;

            long objectId = objectOccurrence.getFirst();
            Occurrence occurrence = objectOccurrence.getSecond();

            Object object = objectHashMap.get(objectId);
            if (object != null && object.isNew()) {

                if (!objectIdMapping.containsKey(objectId)) {
                    //add object
                    objectTable = new ObjectTable();
                    ObjectTypeTable objectTypeTable = objectTypeTableDAO.find(object.getType().getId());
                    if (objectTypeTable == null) {
                        throw new IdNotFoundException("objectTypeId", object.getType().getId());
                    }
                    objectTable.setObjectType(objectTypeTable);

                    objectTableDAO.add(objectTable);
                    objectIdMapping.put(objectId, objectTable);
                } else {
                    objectTable = objectIdMapping.get(objectId);
                    //todo: test if object is still root?
                }

            } else {
                //find object in db
                objectTable = objectTableDAO.find(objectId);
                if (!objectTable.isRoot()) {
                    objectTable = objectTable.getRootObject();
                }
            }

            if (objectTable == null) {
                throw new IdNotFoundException("objectId", objectId);
            }

            AliasTable aliasTable = null;
            for (AliasTable alias : objectTable.getAliases()) {
                if (occurrence.getValue().equals(alias.getAlias())) {
                    aliasTable = alias;
                }
            }

            if (aliasTable == null) {
                aliasTable = new AliasTable(objectTable, occurrence.getValue());
                objectTable.getAliases().add(aliasTable);
                aliasTableDAO.add(aliasTable);
            }

            AliasOccurrenceTable aliasOccurrenceTable = new AliasOccurrenceTable(occurrence.getPosition(), aliasTable, documentTable);
            aliasTable.getOccurrences().add(aliasOccurrenceTable);
            documentTable.getAliasOccurrences().add(aliasOccurrenceTable);

            aliasOccurrenceTableDAO.add(aliasOccurrenceTable);
        }

        //relations maps
        HashMap<Long, Relation> relationHashMap = new HashMap<>();
        for (Relation relation : relations) {
            relationHashMap.put(relation.getId(), relation);
        }

        HashMap<Long, RelationTable> relationIdMapping = new HashMap<>();

        //add relation
        //TODO: group relations?
        for (Pair<Long, Occurrence> relationOccurrence : relationOccurrences) {

            RelationTable relationTable;

            long relationId = relationOccurrence.getFirst();
            Occurrence occurrence = relationOccurrence.getSecond();

            Relation relation = relationHashMap.get(relationId);
            if (relation != null && relation.isNew()) {

                if (!relationIdMapping.containsKey(relationId)) {

                    relationTable = new RelationTable();

                    RelationTypeTable relationTypeTable = relationTypeTableDAO.find(relation.getType().getId());
                    if (relationTypeTable == null) {
                        throw new IdNotFoundException("relationTypeId", relation.getType().getId());
                    }

                    relationTable.setRelationType(relationTypeTable);

                    relationTableDAO.add(relationTable);
                    relationIdMapping.put(relationId, relationTable);
                } else {
                    relationTable = relationIdMapping.get(relationId);
                }

            } else {
                relationTable = relationTableDAO.find(relationId);
            }

            if (relationTable == null) {
                throw new IdNotFoundException("relationId", relationId);
            }

            if (relation != null) {
                Set<Long> alreadyInRelation = relationTable.getObjectsInRelation().stream()
                        .map(x -> x.getObject().getId())
                        .collect(Collectors.toSet());

                for (Triple<Object, String, Integer> objectInRelation : relation.getObjectsInRelation()) {

                    long objectInRelationId = objectInRelation.getFirst().getId();
                    String role = objectInRelation.getSecond();
                    int order = objectInRelation.getThird();

                    ObjectTable objectInRelationTable = objectIdMapping.get(objectInRelationId);
                    if (objectInRelationTable == null) {
                        objectInRelationTable = objectTableDAO.find(objectInRelationId);

                        if (objectInRelationTable == null) {
                            throw new IdNotFoundException("objectInRelationId", objectInRelationId);
                        }
                    }

                    //todo: add test: can be object in relation more than once?
                    if (!alreadyInRelation.contains(objectInRelationTable.getId())) {
                        InRelationTable inRelationTable = new InRelationTable(role, order, relationTable, objectInRelationTable);
                        inRelationTableDAO.add(inRelationTable);
                        relationTable.getObjectsInRelation().add(inRelationTable);
                        objectInRelationTable.getRelations().add(inRelationTable);

                        alreadyInRelation.add(objectInRelationTable.getId());
                    }
                }
            }

            RelationOccurrenceTable relationOccurrenceTable;
            if (occurrence != null) {
                relationOccurrenceTable = new RelationOccurrenceTable(relationTable, documentTable, occurrence.getPosition(), occurrence.getValue());
            } else {
                relationOccurrenceTable = new RelationOccurrenceTable();
                relationOccurrenceTable.setDocument(documentTable);
                relationOccurrenceTable.setRelation(relationTable);
            }
            relationOccurrenceTableDAO.add(relationOccurrenceTable);
            documentTable.getRelationOccurrences().add(relationOccurrenceTable);
            relationTable.getOccurrences().add(relationOccurrenceTable);
        }

        //register re-learn command for named entity recognizer and text pro
        invoker.register(new TextProLearnCommand(textPro));
        invoker.register(new NamedEntityRecognizerLearnCommand(recognizer));

        return true;
    }

    public Problems getProblems(EditingTicket ticket) {
        long nextVersion = ticket.getVersion() + 1;
        List<Object> newObjects = objectTableDAO.findAllSinceGlobalVersion(nextVersion).stream()
                .map(x -> Object.fromObjectTable(x, aliasTableDAO.findAllAliasesOfObject(x)))
                .collect(Collectors.toList());

        List<Relation> newRelations = relationTableDAO.findAllSinceGlobalVersion(nextVersion).stream()
                .map(x -> Relation.fromRelationTable(x, aliasTableDAO))
                .collect(Collectors.toList());

        List<JoinedObject> newJoinedObjects = joinedObjectsTableDAO.findAllSinceGlobalVersion(nextVersion).stream()
                .map(x -> new JoinedObject(
                        Object.fromObjectTable(x.getNewObject(), aliasTableDAO.findAllAliasesOfObject(x.getNewObject())),
                        Object.fromObjectTable(x.getOldObject1(), aliasTableDAO.findAllAliasesOfObject(x.getOldObject1())),
                        Object.fromObjectTable(x.getOldObject2(), aliasTableDAO.findAllAliasesOfObject(x.getOldObject2()))))
                .collect(Collectors.toList());

        return new Problems(newObjects, newRelations, newJoinedObjects);
    }
}
