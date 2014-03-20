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
    private final int id;

    /** Relation type. */
    private final RelationType type;

    /** Objects and their order in relation. */
    private final Set<Pair<Object, Integer>> objects;

    /**
     * Creates Relation from commons blue print.
     * @param relation blue print relation
     * @param objects id -> object mapping to resolve relation ids
     */
    public Relation(final cz.cuni.mff.ufal.textan.commons.models.Relation relation, final Map<Integer, Object> objects) {
        id = relation.getId();
        type = new RelationType(relation.getRelationType());
        this.objects = relation.getObjectInRelationIds().getInRelation().stream()
                .map(inRel -> new Pair<>(objects.get(inRel.getObjectId()), inRel.getOrder()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Relation(final int id, final RelationType type) {
        this.id  = id;
        this.type = type;
        objects = new HashSet<>();
    }

    /**
     * Returns relation id.
     * @return relation id
     */
    public int getId() {
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
            ids.getInRelation().add(inRelation);
        }
        result.setObjectInRelationIds(ids);
        return result;
    }
}
