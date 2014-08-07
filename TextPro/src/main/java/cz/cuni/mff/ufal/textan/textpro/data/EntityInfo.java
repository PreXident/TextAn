/*
 * 
 */

package cz.cuni.mff.ufal.textan.textpro.data;

import java.util.Map;

/**
 *
 * @author HOANGT
 */
public class EntityInfo {
    public static Entity e;
    
    Map<Long, Double> score;
    public EntityInfo(Entity _e, Map _score){
        this.e = _e;
        this.score = _score;
    }
    
}
