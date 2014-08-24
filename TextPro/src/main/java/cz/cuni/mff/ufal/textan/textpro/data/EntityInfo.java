/*
 * 
 */

package cz.cuni.mff.ufal.textan.textpro.data;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HOANGT
 */
public class EntityInfo {
    public Entity e;
    
    public Map<Long, Double> score;
    public List<ObjectTable> objects;
    public Map<ObjectTable,Long> match_object;

    public EntityInfo(Entity _e, Map<Long,Double> _score, List<ObjectTable> _objects, Map<ObjectTable,Long> _match_object){
        this.e = _e;
        this.score = _score;
        this.objects = _objects;
        this.match_object = _match_object;
    }
}
