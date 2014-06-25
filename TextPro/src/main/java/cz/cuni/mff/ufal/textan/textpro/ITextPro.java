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
    
    //  Output a list of token after processing
    public List<String> TokenizeDoc(String document);
    
    
    // The main class of TextPro
    // The result of double ranking is a map from entity to the id value of 
    public Map<Entity, Map<Long, Double>> DoubleRanking(String document,List<Entity> eList, int topK);
}