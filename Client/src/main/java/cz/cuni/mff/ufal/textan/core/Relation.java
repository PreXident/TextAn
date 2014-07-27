package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.Relation.ObjectInRelationIds;
import cz.cuni.mff.ufal.textan.commons.models.Relation.ObjectInRelationIds.InRelation;
import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Client side representation of {@link cz.cuni.mff.ufal.textan.commons.models.Relation}.
 */
public class Relation implements Serializable {

    /** Relation id. */
    private final long id;

    /** Relation type. */
    private final RelationType type;

    /** Objects (and their order and role) in relation. */
    private final Set<Triple<Integer, String, Object>> objects;

    /** List of anchors. */
    private final List<String> anchors;

    /** Flag indicating whether the relation was fetched from db or created by client. */
    private final boolean isNew;

    /**
     * Creates Relation from commons blue print.
     * @param relation blue print relation
     * @param objects id -> object mapping to resolve relation ids
     */
    public Relation(final cz.cuni.mff.ufal.textan.commons.models.Relation relation, final Map<Long, Object> objects) {
        id = relation.getId();
        type = new RelationType(relation.getRelationType());
        this.objects = relation.getObjectInRelationIds().getInRelations().stream()
                .map(inRel -> new Triple<>(inRel.getOrder(), inRel.getRole(), objects.get(inRel.getObjectId())))
                .collect(Collectors.toCollection(HashSet::new));
        isNew = relation.isIsNew();
        anchors = new ArrayList<>(relation.getAnchors());
    }

    /**
     * Creates new relation from id and relation type.
     * @param id relation id
     * @param type relation type
     */
    public Relation(final long id, final RelationType type) {
        this.id  = id;
        this.type = type;
        objects = new HashSet<>();
        isNew = true;
        anchors = Collections.emptyList();
    }

    /**
     * Returns anchors.
     * @return anchors
     */
    public List<String> getAnchors() {
        return anchors;
    }

    /**
     * Returns concatenated aliases.
     * @return concatenated aliasess
     */
    public String getAnchorString() {
        return String.join(", ", anchors);
    }

    /**
     * Returns relation id.
     * @return relation id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns relation type.
     * @return relation type
     */
    public RelationType getType() {
        return type;
    }

    /**
     * Returns objects in relation.
     * @return objects in relation
     */
    public Set<Triple<Integer, String, Object>> getObjects() {
        return objects;
    }

    @Override
    public boolean equals(java.lang.Object other) {
        if (other instanceof Relation) {
            return id == ((Relation) other).id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (int)(67 * hash + this.id);
        return hash;
    }

    /**
     * Creates new commons Relation.
     * @return new commons Relation
     */
    public cz.cuni.mff.ufal.textan.commons.models.Relation toRelation() {
        final cz.cuni.mff.ufal.textan.commons.models.Relation result =
                new cz.cuni.mff.ufal.textan.commons.models.Relation();
        result.setId(id);
        result.setRelationType(type.toRelationType());
        result.setIsNew(isNew);
        final ObjectInRelationIds ids = new ObjectInRelationIds();
        for (Triple<Integer, String, Object> triple : objects) {
            final InRelation inRelation = new InRelation();
            inRelation.setOrder(triple.getFirst());
            inRelation.setRole(triple.getSecond());
            inRelation.setObjectId(triple.getThird().getId());
            ids.getInRelations().add(inRelation);
        }
        result.setObjectInRelationIds(ids);
        result.getAnchors().addAll(anchors);
        return result;
    }

    @Override
    public String toString() {
        return id + ": " + type.getName();
    }
}
