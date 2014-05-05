package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.*;
import cz.cuni.mff.ufal.textan.server.models.EditingTicket;
import cz.cuni.mff.ufal.textan.server.models.Object;
import cz.cuni.mff.ufal.textan.server.models.Occurrence;
import cz.cuni.mff.ufal.textan.server.models.Relation;
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
    private final IObjectTableDAO objectTableDAO;
    private final IAliasTableDAO aliasTableDAO;
    private final IAliasOccurrenceTableDAO aliasOccurrenceTableDAO;
    private final IRelationTableDAO relationTableDAO;
    private final IRelationOccurrenceTableDAO relationOccurrenceTableDAO;

    @Autowired
    public SaveService(
            IDocumentTableDAO documentTableDAO,
            IObjectTableDAO objectTableDAO,
            IAliasTableDAO aliasTableDAO,
            IAliasOccurrenceTableDAO aliasOccurrenceTableDAO,
            IRelationTableDAO relationTableDAO,
            IRelationOccurrenceTableDAO relationOccurrenceTableDAO) {

        this.documentTableDAO = documentTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.aliasOccurrenceTableDAO = aliasOccurrenceTableDAO;
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
                    objectTable = object.toObjectTable();
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

                    relationTable = relation.toRelationTable();
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

            RelationOccurrenceTable relationOccurrenceTable = new RelationOccurrenceTable(relationTable,documentTable, occurrence.getPosition(), occurrence.getValue());
            relationOccurrenceTableDAO.add(relationOccurrenceTable);
        }

        return true;
    }
}
