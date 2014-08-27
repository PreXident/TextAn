package cz.cuni.mff.ufal.textan.assigner.data;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple holder of information needed for heuristic ranking.
 * @author HOANGT
 */
public class EntityInfo {

    /** Wrapped entity. */
    public final Entity e;

    /** Candidate objects to be assigned to entity. */
    public List<ObjectTable> objects;

    /** Scores of the candidate objects. */
    public final Map<Long, Double> score;

    /**
     * Only constructor.
     * @param entity entity to wrap
     * @param objects object close to the entity
     */
    public EntityInfo(final Entity entity, List<ObjectTable> objects){
        this.e = entity;
        this.objects = objects;
        this.score = new HashMap<>(objects.size());
        //initialize
        for (ObjectTable object : objects) {
            score.put(object.getId(), 1.0);
        }
    }

    /**
     * Increases score associated with the given object.
     * @param object object to increase score
     */
    public void increaseScore(final ObjectTable object) {
        final long id = object.getId();
        final Double s = score.get(id);
        score.put(id, s + 1.0);
    }
}
