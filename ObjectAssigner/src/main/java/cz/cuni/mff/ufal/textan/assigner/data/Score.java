/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.assigner.data;

import java.util.HashMap;
import java.util.Map;

/**
 * This has the format:
 * Entity - the entity of document
 * List of Object and score that it has
 * @author Tamhd
 */
public class Score {
    /*
     * The entity 
     */
    public Entity e;

    public Entity getE() {
        return e;
    }

    public void setE(Entity e) {
        this.e = e;
    }
    Map<Entity, Map<Object, Double>> map = new HashMap<Entity, Map<Object, Double>>();
    
}
