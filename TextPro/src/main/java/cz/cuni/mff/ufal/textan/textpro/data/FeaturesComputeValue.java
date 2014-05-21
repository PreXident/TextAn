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
    private double EntityTextAndObjectAlias(String stringA, String stringB) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //JaroWinkler algorithm = new JaroWinkler();
        //return algorithm.getSimilarity(stringA, stringB);
        return SimpleLevenshtein.getStringDistance( stringA, stringB);
        //return 0;
    }
    
    
    /*
    * Compare the type of entity and object
    * Return 1 if they are the same, 0 otherwise
    */
    private double EntityTypeAndObjectType(String eType, String oType) {
        if(eType.equalsIgnoreCase(oType)) {
            return 1;
        }
        return 0;
    }
    
    
    public static void main(String[] args) throws Exception {
        Entity e1 = new Entity("John", 0, 0, 1); // e.g 1 = person
        Entity e2 = new Entity("12.03.2013", 0, 0, 2); // e.g 2 = date
        Entity e3 = new Entity("Mary", 0, 0, 1); // e.g 1 = person
        
        /*
        * Get a database object, get all alias which it has
        * Test the similarity between object and entity
        * Waiting for the database
        */
        
        FeaturesComputeValue fcv = new FeaturesComputeValue();

        String group1 = "Mary Peter John 12.03.2014";
        String group2 = "John Mary";
        
        System.out.println("Test 0");
        double d = fcv.EntityTextAndObjectAlias("12.03", e2.getText());
        System.out.println("Similarity " + d);
        
        System.out.println("Test 1");
        double d2 = fcv.EntityTextAndObjectAlias(group1, group2);
        System.out.println("Similarity " + d2);
        
    }    
}
