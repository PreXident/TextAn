/*
 * To change this license header, choose License Headers in Project Properties.
 * To compute the feature value for machine learning
 * First, the difference between alias name and the entity text
 * Second, the difference between entity type and DBObject type
 * Third, The number of sharing sharing objects between the document and DBObject
 */

package cz.cuni.mff.ufal.textan.assigner.data;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import de.linuxusers.levenshtein.util.SimpleLevenshtein;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public static double EntityTypeAndObjectType(long eType, long oType) {
        if(eType == oType) {
            return 1;
        }
        return 0;
    }
    
    /*
     * Get the Mutual object 
    */
    public double EntityAndObjectMutual(String text, List<Entity> eList, Entity e, ObjectTable o, IObjectTableDAO objectTableDAO) {
        
        // List of all object table associated with OTHER entities
        List<ObjectTable> finalDocList = new ArrayList<ObjectTable>();
        for (Entity e_other:eList) {
            if(e_other.getText().equalsIgnoreCase(e.getText())) {
                continue;
            }
            List<ObjectTable> oList1 = getMutualObject(e_other, objectTableDAO); // List of object closed to the entity
            for(ObjectTable o2:oList1) {
                finalDocList.add(o2);
            }
        }
        // List of all object table which the Object is joined from
        Set<ObjectTable> oList2 = o.getObjectsThisIsJoinedFrom();
        
        double count = 0.00; // number of Mutual
        for (ObjectTable sample:finalDocList) {
            if(oList2.contains(sample)) {
                count ++;
            }
        }
        
        return count;
    }
    
    public List<ObjectTable> getMutualObject(Entity e, IObjectTableDAO objectTableDAO){
        return objectTableDAO.findAllByAliasSubstring(e.getText());
    }    
    
    /*
     * Number of objects that current object joined from
     * Maybe important
    */
    public double NumberOfComponentObject( ObjectTable o) {
        Set<ObjectTable> oList2 = o.getObjectsThisIsJoinedFrom();
        return oList2.size();
    }
}
