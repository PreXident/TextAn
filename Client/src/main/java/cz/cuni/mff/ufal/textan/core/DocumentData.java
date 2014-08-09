package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.ObjectOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.RelationOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.dataprovider.GetObjectsAndRelationsOccurringInDocumentResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holder of document data, ie. its objects and relations.
 */
public class DocumentData {

    /** Possible types of Occurrences. */
    public enum OccurrenceType {
        OBJECT, RELATION
    }

    /** Objects occurring in the document. */
    private final Map<Long, Object> objects;

    /** Relations occurring in the document. */
    private final Map<Long, Relation> relations;

    /** Common list of occurrences of both objects and relations. */
    private final List<Occurrence> occurrences;

    /**
     * Only constructor.
     * @param docData blueprint
     */
    public DocumentData(final GetObjectsAndRelationsOccurringInDocumentResponse docData) {
        objects = docData.getObjects().stream()
                .collect(Collectors.toMap(
                        cz.cuni.mff.ufal.textan.commons.models.Object::getId,
                        Object::new));
        relations = docData.getRelations().stream()
                .collect(Collectors.toMap(
                        cz.cuni.mff.ufal.textan.commons.models.Relation::getId,
                        rel -> new Relation(rel, getObjects())));
        occurrences = new ArrayList<>();
        docData.getObjectOccurrences().stream()
                .map(Occurrence::new)
                .forEach(occurrences::add);
        docData.getRelationOccurrences().stream()
                .map(Occurrence::new)
                .forEach(occurrences::add);
        Collections.sort(occurrences, (o1, o2) -> o1.position - o2.position);
    }

    /**
     * Returns document's objects.
     * @return document's objects
     */
    public Map<Long, Object> getObjects() {
        return objects;
    }

    /**
     * Returns document's relations.
     * @return document's relations.
     */
    public Map<Long, Relation> getRelations() {
        return relations;
    }

    /**
     * Returns occurrences of objects/relations.
     * @return occurrences of objects/relations
     */
    public List<Occurrence> getOccurrences() {
        return occurrences;
    }

    /**
     * Simple representation of object/relation occurrence in the document.
     */
    public static class Occurrence {

        /** Object/Relation id. */
        public final long id;

        /** Occurrence type. */
        public final OccurrenceType type;

        /** Index of the first character. */
        public final int position;

        /** Occurrence length. */
        public final int length;

        /** Index of the last character. */
        public final int last;

        /**
         * Contructs Occurrence from object occurrence.
         * @param occurrence object occurrence
         */
        public Occurrence(final ObjectOccurrence occurrence) {
            id = occurrence.getObjectId();
            type = OccurrenceType.OBJECT;
            position = occurrence.getAlias().getPosition();
            length = occurrence.getAlias().getValue().length();
            last = position + length;
        }

        /**
         * Constructs Occurrece from relation occurrence.
         * @param occurrence relation occurrence
         */
        public Occurrence(final RelationOccurrence occurrence) {
            id = occurrence.getRelationId();
            type = OccurrenceType.RELATION;
            position = occurrence.getAnchor().getPosition();
            final String anchor = occurrence.getAnchor().getValue();
            if (anchor != null) {
                length = anchor.length();
            } else {
                length = 0;
            }
            last = position + length;
        }
    }
}
