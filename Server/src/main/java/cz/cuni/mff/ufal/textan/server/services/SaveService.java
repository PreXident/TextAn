package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.*;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

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

    @Autowired
    public SaveService(
            IDocumentTableDAO documentTableDAO,
            IObjectTypeTableDAO objectTypeTableDAO, IObjectTableDAO objectTableDAO,
            IAliasTableDAO aliasTableDAO,
            IAliasOccurrenceTableDAO aliasOccurrenceTableDAO,
            IRelationTypeTableDAO relationTypeTableDAO, IRelationTableDAO relationTableDAO,
            IRelationOccurrenceTableDAO relationOccurrenceTableDAO) {

        this.documentTableDAO = documentTableDAO;
        this.objectTypeTableDAO = objectTypeTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.aliasOccurrenceTableDAO = aliasOccurrenceTableDAO;
        this.relationTypeTableDAO = relationTypeTableDAO;
        this.relationTableDAO = relationTableDAO;
        this.relationOccurrenceTableDAO = relationOccurrenceTableDAO;
    }

    /*
    * TODO:
    *  - relation occurrence?
    * */

    public boolean save(
            String text,
            List<Object> objects, List<Pair<Long, Occurrence>> objectOccurrences,
            List<Relation> relations, List<Pair<Long, Occurrence>> relationOccurrences,
            boolean force, EditingTicket ticket) throws IdNotFoundException {

        if (!force && checkChanges()) {
            return false;
        }

        DocumentTable documentTable = new DocumentTable(text);
        documentTableDAO.add(documentTable);

        return innerSave(documentTable, objects, objectOccurrences, relations, relationOccurrences, ticket);
    }

    public boolean save(
            long documentId,
            List<Object> objects, List<Pair<Long, Occurrence>> objectOccurrences,
            List<Relation> relations, List<Pair<Long, Occurrence>> relationOccurrences,
            boolean force, EditingTicket ticket) throws IdNotFoundException {

        if (!force && checkChanges()) {
            return false;
        }

        DocumentTable documentTable = documentTableDAO.find(documentId);
        if (documentTable == null) {
            throw new IdNotFoundException("documentId", documentId);
        }

        return innerSave(documentTable, objects, objectOccurrences, relations, relationOccurrences, ticket);
    }

    private boolean checkChanges() {
        return false;
    }

    private boolean innerSave(
            DocumentTable documentTable,
            List<Object> objects, List<Pair<Long, Occurrence>> objectOccurrences,
            List<Relation> relations, List<Pair<Long, Occurrence>> relationOccurrences,
            EditingTicket ticket) throws IdNotFoundException {

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
                }

            } else {
                //find object in db
                objectTable = objectTableDAO.find(objectId);
            }

            if (objectTable == null) {
                throw new IdNotFoundException("objectId", objectId);
            }

            List<AliasTable> aliases = aliasTableDAO.findAllAliasesOfObject(objectTable);
            AliasTable aliasTable = null;
            for (AliasTable alias : aliases) {
                //TODO: test case sensitivity! (all lowercase?, different aliases?)
                if (occurrence.getValue().equals(alias.getAlias())) {
                    aliasTable = alias;
                }
            }

            if (aliasTable == null) {
                aliasTable = new AliasTable(objectTable, occurrence.getValue());
                aliasTableDAO.add(aliasTable);
            }

            AliasOccurrenceTable aliasOccurrenceTable = new AliasOccurrenceTable(occurrence.getPosition(), aliasTable, documentTable);
            aliasOccurrenceTableDAO.add(aliasOccurrenceTable);
        }

        //relations maps
        HashMap<Long, Relation> relationHashMap = new HashMap<>();
        for (Relation relation : relations) {
            relationHashMap.put(relation.getId(), relation);
        }

        HashMap<Long, RelationTable> relationIdMapping = new HashMap<>();

        //add relation
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
                for (Pair<Long,Integer> objectInRelation : relation.getObjectsInRelation()) {

                    long objectInRelationId = objectInRelation.getFirst();
                    int order = objectInRelation.getSecond();

                    ObjectTable objectInRelationTable = objectIdMapping.get(objectInRelationId);
                    if (objectInRelationTable == null) {
                        objectInRelationTable = objectTableDAO.find(objectInRelationId);

                        if (objectInRelationTable == null) {
                            throw new IdNotFoundException("objectInRelationId", objectInRelationId);
                        }
                    }

                    relationTable.getObjectsInRelation().add(new InRelationTable(order, relationTable, objectInRelationTable));
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
        }

        return true;
    }
}
