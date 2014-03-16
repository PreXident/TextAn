package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.utils.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Client side representation of {@link cz.cuni.mff.ufal.textan.commons.models.Relation}.
 */
public class Relation {

    /** Relation id. */
    private final int id;

    /** Relation type. */
    private final RelationType type;

    /** Objects and their order in relation. */
    private final Set<Pair<Object, Integer>> objects = new HashSet<>();

    /**
     * Creates Relation from commons blue print.
     * @param relation blue print relation
     * @param objects id -> object mapping to resolve relation ids
     */
    public Relation(final cz.cuni.mff.ufal.textan.commons_old.models.Relation relation, final Map<Integer, Object> objects) {
        id = relation.getId();
        type = new RelationType(relation.getType());
        List<Integer> ids = relation.getObjectInRelationIds();
        List<Integer> orders = relation.getOrderInRelation();
        for (int i = 0; i < ids.size(); ++i) {
            this.objects.add(new Pair<>(objects.get(ids.get(i)), orders.get(i)));
        }
    }

    public Relation(final int id, final RelationType type) {
        this.id  = id;
        this.type = type;
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
    public cz.cuni.mff.ufal.textan.commons_old.models.Relation toRelation() {
        final cz.cuni.mff.ufal.textan.commons_old.models.Relation result = new cz.cuni.mff.ufal.textan.commons_old.models.Relation(
                id, type.toRelationType()
        );
        objects.stream().forEach((pair) -> {
            result.getObjectInRelationIds().add(pair.getFirst().getId());
            result.getObjectInRelationIds().add(pair.getSecond());
        });
        return result;
    }
}
