package cz.cuni.mff.ufal.textan.server.models;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.data.tables.RelationOccurrenceTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.server.services.IdNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A service layer representation of the Relation
 * @author Petr Fanta
 */
public class Relation {

    private final long id;
    private final RelationType type;
    private final List<Triple<Object, String, Integer>> objectsInRelation;
    private final List<String> anchors;
    private final boolean isNew;


    /**
     * Instantiates a new Relation.
     * @param id the id
     * @param type the type
     * @param objectsInRelation the objects in relation
     * @param anchors
     * @param isNew the is new
     */
    public Relation(long id, RelationType type, List<Triple<Object, String, Integer>>objectsInRelation, List<String> anchors, boolean isNew) {
        this.id = id;
        this.type = type;
        this.objectsInRelation = objectsInRelation;
        this.anchors = anchors;
        this.isNew = isNew;
    }

    /**
     * Creates a {@link cz.cuni.mff.ufal.textan.server.models.Relation} from a {@link cz.cuni.mff.ufal.textan.data.tables.RelationTable}.
     *
     * @param relationTable the relation table
     * @return the relation
     */
    public static Relation fromRelationTable(RelationTable relationTable) {

        List<Triple<Object, String, Integer>> objectsInRelation = relationTable.getObjectsInRelation().stream()
                .map(inRelation -> new Triple<>(Object.fromObjectTable(inRelation.getObject()), inRelation.getRole(), inRelation.getOrder()))
                .collect(Collectors.toList());

        //TODO: test if this is unique (distinct)
        List<String> anchors = relationTable.getOccurrences().stream()
                .map(RelationOccurrenceTable::getAnchor)
                .collect(Collectors.toList());

        return new Relation(
                relationTable.getId(),
                RelationType.fromRelationTypeTable(relationTable.getRelationType()),
                objectsInRelation,
                anchors,
                false
                );
    }

    public static Relation fromCommonsRelation(cz.cuni.mff.ufal.textan.commons.models.Relation commonsRelation, Map<Long, Object> objectMap) throws IdNotFoundException {

        List<Triple<Object, String, Integer>> objectsInRelation = new ArrayList<>();
        for (cz.cuni.mff.ufal.textan.commons.models.Relation.InRelation inRelation : commonsRelation.getInRelations()) {
            Object object = objectMap.get(inRelation.getObjectId());
            if (object == null) {
                throw new IdNotFoundException(
                        "The object with id " + inRelation.getObjectId() + " was not found in objects list.",
                        "inRelation.objectId",
                        inRelation.getObjectId()
                );
            }

            objectsInRelation.add(new Triple<>(object, inRelation.getRole(), inRelation.getOrder()));
        }

        return new Relation(
                commonsRelation.getId(),
                RelationType.fromCommonsRelationType(commonsRelation.getRelationType()),
                objectsInRelation,
                commonsRelation.getAnchors(),
                commonsRelation.isIsNew()
        );
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public RelationType getType() {
        return type;
    }

    /**
     * Gets identifiers of objects in relation and they order in relation.
     *
     * @return the objects in relation
     */
    public List<Triple<Object, String, Integer>> getObjectsInRelation() {
        return objectsInRelation;
    }


    /**
     * Gets anchors.
     *
     * @return the anchors
     */
    public List<String> getAnchors() {
        return anchors;
    }

    /**
     * Indicates if relation is in database.
     *
     * @return the boolean
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Converts the instance to a {@link cz.cuni.mff.ufal.textan.commons.models.Relation}.
     *
     * @return the {@link cz.cuni.mff.ufal.textan.commons.models.Relation}
     */
    public cz.cuni.mff.ufal.textan.commons.models.Relation toCommonsRelation() {

        cz.cuni.mff.ufal.textan.commons.models.Relation commonsRelation = new cz.cuni.mff.ufal.textan.commons.models.Relation();
        commonsRelation.setId(id);
        commonsRelation.setRelationType(type.toCommonsRelationType());

        for (Triple<Object, String, Integer> inRelation : objectsInRelation) {
            cz.cuni.mff.ufal.textan.commons.models.Relation.InRelation commonsInRelation = new cz.cuni.mff.ufal.textan.commons.models.Relation.InRelation();
            commonsInRelation.setObjectId(inRelation.getFirst().getId());
            commonsInRelation.setRole(inRelation.getSecond());
            commonsInRelation.setOrder(inRelation.getThird());

            commonsRelation.getInRelations().add(commonsInRelation);
        }
        commonsRelation.getAnchors().addAll(anchors);

        commonsRelation.setIsNew(isNew);

        return commonsRelation;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;

        if (id != relation.id) return false;
        if (type != null ? !type.equals(relation.type) : relation.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Relation{");
        sb.append("id=").append(id);
        sb.append(", type=").append(type);
        sb.append(", objectsInRelation=").append(objectsInRelation);
        sb.append(", anchors=").append(anchors);
        sb.append(", isNew=").append(isNew);
        sb.append('}');
        return sb.toString();
    }
}
