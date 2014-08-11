package cz.cuni.mff.ufal.textan.textpro;

// *************** INTERFACE*******************

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;

import java.util.List;
import java.util.Map;

/*
 * This is interface version 0
 */

public interface ITextPro {

    /**
     *
     */
    
    // Learn the model
    public void learn();
    
    //  Output a list of token after processing
    public List<String> TokenizeDoc(String document);
    
    
    // The main class of TextPro
    // The result of double ranking is a map from entity to the id value of 
    public Map<Entity, List<Pair<Long, Double>>> Ranking(String document, List<Entity> eList, int topK);
    
    public Map<Entity, List<Pair<Long, Double>>> MachineLearning(String document, List<Entity> eList, int topK);
}