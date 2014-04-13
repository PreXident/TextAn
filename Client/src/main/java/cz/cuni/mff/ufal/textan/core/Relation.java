package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.Relation.ObjectInRelationIds;
import cz.cuni.mff.ufal.textan.commons.models.Relation.ObjectInRelationIds.InRelation;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Client side representation of {@link cz.cuni.mff.ufal.textan.commons.models.Relation}.
 */
public class Relation {

    /** Relation id. */
    private final long id;

    /** Relation type. */
    private final RelationType type;

    /** Objects and their order in relation. */
    private final Set<Pair<Object, Integer>> objects;

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
                .map(inRel -> new Pair<>(objects.get(inRel.getObjectId()), inRel.getOrder()))
                .collect(Collectors.toCollection(HashSet::new));
        isNew = relation.isIsNew();
    }

    public Relation(final long id, final RelationType type) {
        this.id  = id;
        this.type = type;
        objects = new HashSet<>();
        isNew = true;
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
    public Set<Pair<Object, Integer>> getObjects() {
        return objects;
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
        result.setIsNew(Boolean.TRUE); //TODO add relation feature
        final ObjectInRelationIds ids = new ObjectInRelationIds();
        for (Pair<Object, Integer> pair : objects) {
            final InRelation inRelation = new InRelation();
            inRelation.setObjectId(pair.getFirst().getId());
            inRelation.setOrder(pair.getSecond());
            ids.getInRelations().add(inRelation);
        }
        result.setObjectInRelationIds(ids);
        return result;
    }

    @Override
    public String toString() {
        return id + ": " + type.getName();
    }
}
