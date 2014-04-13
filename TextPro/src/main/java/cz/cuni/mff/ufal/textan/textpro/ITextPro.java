package cz.cuni.mff.ufal.textan.textpro;

// *************** INTERFACE*******************

import java.util.List;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import java.util.Map;

/*
 * This is interface version 0
 */

public interface ITextPro {

    /**
     *
     */
    
    public void init(List<String> objectlist);
    //  Output a list of entity after processing
    public List<Entity> recognizedEntity(String document);
    
    public List<Entity> SimpleRanking(String TestDir, String DataDir);
    
    public Map<Entity, Map<Object, Double>> DoubleRanking();
}