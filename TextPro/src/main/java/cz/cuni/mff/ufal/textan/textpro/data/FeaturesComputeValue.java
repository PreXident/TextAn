/*
 * To change this license header, choose License Headers in Project Properties.
 * To compute the feature value for machine learning
 * First, the difference between alias name and the entity text
 */

package cz.cuni.mff.ufal.textan.textpro.data;

import de.linuxusers.levenshtein.util.SimpleLevenshtein;
import java.util.ArrayList;

/**
 *
 * @author HOANGT
 */
public class FeaturesComputeValue {

    public FeaturesComputeValue(){
        // Initialize the class
    }
    
    /*
    * Compare the extity text and alias of object
    * Word with one alias only
    */
    public static double EntityTextAndObjectAlias(String entityString, String objectString) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //JaroWinkler algorithm = new JaroWinkler();
        //return algorithm.getSimilarity(stringA, stringB);
        return SimpleLevenshtein.getStringDistance( entityString, objectString);
        //return 0;
    }
    
    
    /*
    * Compare the type of entity and object
    * Return 1 if they are the same, 0 otherwise
    */
    public static double EntityTypeAndObjectType(String eType, String oType) {
        if(eType.equalsIgnoreCase(oType)) {
            return 1;
        }
        return 0;
    }
    
}
